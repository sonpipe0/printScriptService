package com.printScript.PrintScriptService.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.printScript.PrintScriptService.DTO.*;
import com.printScript.PrintScriptService.error.ParsingError;
import com.printScript.PrintScriptService.services.RunnerService;
import com.printScript.PrintScriptService.utils.TokenUtils;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/runner")
public class RunnerController {

    @Autowired
    private RunnerService runnerService;

    @PostMapping("/validate")
    public ResponseEntity<Object> validate(@RequestBody ValidateRequestDTO validateRequestDTO) {
        String code = validateRequestDTO.getCode();
        String version = validateRequestDTO.getVersion();

        return getObjectResponseEntity(version, code);
    }

    @PostMapping("/execute")
    public ResponseEntity<Object> execute(@RequestBody ExecuteContextDTO executeContextDTO) {
        Response<List<String>> result = getExectutionResponse(executeContextDTO);

        if (result.isError()) {
            return ResponseEntity.status(result.getError().code()).body(result.getError().message());
        } else {
            return new ResponseEntity<>(result.getData(), HttpStatus.OK);
        }
    }

    @PostMapping("/test")
    public ResponseEntity<Object> executeTest(@RequestBody TestContextDTO testContextDTO) {
        ExecuteContextDTO executeContextDTO = new ExecuteContextDTO(testContextDTO.getText(),
                testContextDTO.getVersion(), testContextDTO.getInputs(), testContextDTO.getEnvVars());
        Response<List<String>> result = getExectutionResponse(executeContextDTO);

        if (result.isError()) {
            return ResponseEntity.status(result.getError().code()).body(result.getError().message());
        }
        if (result.getData().equals(testContextDTO.getExpected())) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    private Response<List<String>> getExectutionResponse(ExecuteContextDTO executeContextDTO) {
        String code = executeContextDTO.getText();
        String version = executeContextDTO.getVersion();
        List<String> inputs = executeContextDTO.getInputs();
        Map<String, String> envVars = executeContextDTO.getEnvVars();
        Response<List<String>> result = runnerService.execute(code, version, inputs, envVars);
        return result;
    }

    @NotNull
    private ResponseEntity<Object> getObjectResponseEntity(@RequestParam("version") String version, String code) {
        Response<List<ParsingError>> response = runnerService.validate(code, version);
        if (response.getData() != null) {
            return new ResponseEntity<>(response.getData(), HttpStatus.EXPECTATION_FAILED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PostMapping("/lintingErrors")
    public ResponseEntity<Object> getLintingErrors(@RequestBody Map<String, String> body,
            @RequestHeader Map<String, String> headers) {
        String code = body.get("code");
        String version = body.get("version");
        String token = headers.get("authorization").substring(7);
        Map<String, String> userInfo = TokenUtils.decodeToken(token);
        String userId = userInfo.get("userId");
        Response<Void> response = runnerService.getLintingErrors(code, version, userId);
        if (response.isError()) {
            return ResponseEntity.status(response.getError().code()).body(response.getError().message());
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
