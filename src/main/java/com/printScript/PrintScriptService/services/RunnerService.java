package com.printScript.PrintScriptService.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.printScript.PrintScriptService.DTO.Response;
import com.printScript.PrintScriptService.error.Error;
import com.printScript.PrintScriptService.error.LintingError;
import com.printScript.PrintScriptService.error.ParsingError;

import dataObjects.LinterResult;
import dataObjects.ParsingResult;
import factories.LinterFactory;
import factories.Runner;
import factories.ValidatorFactory;
import utils.InterpreterResult;
import utils.MainStringInputProvider;
import utils.PercentageCollector;
import utils.StringInputProvider;

@Service
public class RunnerService {

    public Response<List<ParsingError>> validate(String text, String version) {
        InputStream code = new ByteArrayInputStream(text.getBytes());
        ValidatorFactory validator = new ValidatorFactory();
        PercentageCollector collector = new PercentageCollector();
        List<ParsingError> errorList = new ArrayList<>();
        Iterator<ParsingResult> results = validator.validate(code, version, collector).iterator();
        while (results.hasNext()) {
            ParsingResult result = results.next();
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

    public Response<List<String>> execute(String text, String version, List<String> inputs,
            Map<String, String> envVars) {
        InputStream code = new ByteArrayInputStream(text.getBytes());
        StringInputProvider provider = new MainStringInputProvider(inputs.iterator());
        Runner runner = new Runner();
        Iterator<InterpreterResult> results = runner.run(code, version, provider, envVars, false).iterator();
        List<String> output = new ArrayList<>();
        while (results.hasNext()) {
            InterpreterResult result = results.next();
            if (result.hasPrintln()) {
                output.add(result.getPrintln());
            }
            if (result.hasException()) {
                return Response.withError(new Error(500, "The execution failed, please check the inputs"));
            }
        }
        return Response.withData(output);
    }

    public Response<List<LintingError>> getLintingErrors(String text, String version, InputStream config) {
        InputStream code = new ByteArrayInputStream(text.getBytes());
        LinterFactory linter = new LinterFactory();
        PercentageCollector collector = new PercentageCollector();
        List<LintingError> errorList = new ArrayList<>();
        Iterator<LinterResult> results = linter.lintCode(code, version, config, collector).iterator();
        while (results.hasNext()) {
            LinterResult result = results.next();
            if (result.hasError()) {
                errorList.add(LintingError.of(result.getMessage()));
            }
        }
        if (errorList.isEmpty()) {
            return Response.withData(null);
        } else {
            return Response.withData(errorList);
        }
    }
}
