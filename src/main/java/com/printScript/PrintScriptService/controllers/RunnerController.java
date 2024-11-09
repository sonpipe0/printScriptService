package com.printScript.PrintScriptService.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.printScript.PrintScriptService.DTO.*;
import com.printScript.PrintScriptService.error.ParsingError;
import com.printScript.PrintScriptService.services.RunnerService;

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

    @PostMapping("/test")
    public ResponseEntity<Object> executeTest(@RequestBody TestContextDTO testContextDTO) {
        Response<List<String>> result = runnerService.execute(testContextDTO.getSnippetId(),
                testContextDTO.getVersion(), testContextDTO.getInputs());
        if (result.isError()) {
            return ResponseEntity.status(result.getError().code()).body(result.getError().message());
        }
        if (result.getData().equals(testContextDTO.getExpected())) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
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
}
