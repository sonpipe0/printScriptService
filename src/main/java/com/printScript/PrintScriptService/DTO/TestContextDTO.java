package com.printScript.PrintScriptService.DTO;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TestContextDTO extends ExecuteContextDTO {
    private List<String> expected;

    public TestContextDTO(String text, String version, List<String> inputs, Map<String, String> envVars,
            List<String> expected) {
        super(text, version, inputs, envVars);
        this.expected = expected;
    }
}
