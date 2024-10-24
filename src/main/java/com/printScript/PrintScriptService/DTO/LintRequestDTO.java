package com.printScript.PrintScriptService.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LintRequestDTO {

    private String code;

    private String version;

    private String config;
}
