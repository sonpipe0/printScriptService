package com.printScript.PrintScriptService.DTO;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExecuteContextDTO {
    private String text;
    private String version;
    private List<String> inputs;
    private Map<String, String> envVars;

    public ExecuteContextDTO() {
    }
}
