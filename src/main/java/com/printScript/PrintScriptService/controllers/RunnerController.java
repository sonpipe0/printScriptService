package com.printScript.PrintScriptService.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.printScript.PrintScriptService.DTO.Response;
import com.printScript.PrintScriptService.DTO.ValidateRequestDTO;
import com.printScript.PrintScriptService.error.ParsingError;
import com.printScript.PrintScriptService.services.RunnerService;

@RestController
@RequestMapping("/runner")
public class RunnerController {

    @Autowired
    private RunnerService runnerService;

    @PostMapping("/validate/file")
    public ResponseEntity<Object> validateFile(@RequestParam("file") MultipartFile file,
            @RequestParam("version") String version) throws IOException {
        String code = new String(file.getBytes(), StandardCharsets.UTF_8);
        return getObjectResponseEntity(version, code);
    }

    @PostMapping("/validate")
    public ResponseEntity<Object> validate(@RequestBody ValidateRequestDTO validateRequestDTO) {
        String code = validateRequestDTO.getCode();
        String version = validateRequestDTO.getVersion();
        return getObjectResponseEntity(version, code);
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
