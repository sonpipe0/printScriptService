package com.printScript.PrintScriptService.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LintingError {
    private String message;

    public static LintingError of(String message) {
        return new LintingError(message);
    }
}
