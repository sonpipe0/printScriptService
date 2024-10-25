package com.printScript.PrintScriptService.DTO;

import java.io.InputStream;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LintRequestDTO {

    private String code;

    @NotEmpty
    private String version;

    private InputStream config;
}
