package com.printScript.PrintScriptService.services;


import com.printScript.PrintScriptService.DTO.Response;
import com.printScript.PrintScriptService.error.ParsingError;
import dataObjects.ParsingResult;
import factories.ValidatorFactory;
import kotlin.sequences.Sequence;
import org.springframework.stereotype.Service;
import utils.PercentageCollector;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



@Service
public class RunnerService {

    public Response<List<ParsingError>> validate(String text, String version) {
        InputStream code = new ByteArrayInputStream(text.getBytes());
        ValidatorFactory validator = new ValidatorFactory();
        PercentageCollector collector = new PercentageCollector();
        List<ParsingError> errorList = new ArrayList<>();
        Sequence<ParsingResult> results = validator.validate(code, version, collector);
        while (results.iterator().hasNext()) {
            ParsingResult result = results.iterator().next();
            if (result.hasError()) {
                errorList.add(ParsingError.of(result.getError().getMessage()));
            }
        }
        if (errorList.isEmpty()) {
            return Response.withData(null);
        } else {
            return Response.withData(errorList);
        }
    }
}
