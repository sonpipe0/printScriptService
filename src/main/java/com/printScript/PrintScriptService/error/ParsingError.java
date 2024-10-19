package com.printScript.PrintScriptService.error;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParsingError {
    private String message;


    public static ParsingError of(String message) {
        return new ParsingError(message);
    }
}
