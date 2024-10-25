package com.printScript.PrintScriptService.controllers;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.printScript.PrintScriptService.DTO.ExecuteContextDTO;
import com.printScript.PrintScriptService.DTO.LintRequestDTO;
import com.printScript.PrintScriptService.DTO.Response;
import com.printScript.PrintScriptService.DTO.TestContextDTO;
import com.printScript.PrintScriptService.DTO.ValidateRequestDTO;
import com.printScript.PrintScriptService.error.LintingError;
import com.printScript.PrintScriptService.error.ParsingError;
import com.printScript.PrintScriptService.services.RunnerService;

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

    @GetMapping("/lintingErrors")
    public ResponseEntity<Object> getLintingErrors(@RequestBody LintRequestDTO lintRequestDTO) {
        String code = lintRequestDTO.getCode();
        String version = lintRequestDTO.getVersion();
        InputStream config = lintRequestDTO.getConfig();
        Response<List<LintingError>> response = runnerService.getLintingErrors(code, version, config);
        if (response.getData() != null) {
            return ResponseEntity.ok(response.getData());
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
