package com.printScript.PrintScriptService.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateRequestDTO {

    private String code;

    @NotEmpty
    private String version;
}
