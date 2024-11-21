package com.printScript.PrintScriptService.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidateRequestDTO {

    private String code;

    private String version;
}
