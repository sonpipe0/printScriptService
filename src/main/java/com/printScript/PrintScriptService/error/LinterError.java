package com.printScript.PrintScriptService.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinterError {
    private String message;

    public static LinterError of(String message) {
        return new LinterError(message);
    }
}
