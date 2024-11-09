package com.printScript.PrintScriptService.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TestContextDTO {

    private String snippetId;

    private String version;

    private List<String> inputs;

    private List<String> expected;
}
