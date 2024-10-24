package com.printScript.PrintScriptService.controllers;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.printScript.PrintScriptService.DTO.LintRequestDTO;
import com.printScript.PrintScriptService.DTO.Response;
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
