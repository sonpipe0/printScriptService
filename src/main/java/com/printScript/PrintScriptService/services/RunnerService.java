package com.printScript.PrintScriptService.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.printScript.PrintScriptService.DTO.LintDTO;
import com.printScript.PrintScriptService.DTO.Response;
import com.printScript.PrintScriptService.error.Error;
import com.printScript.PrintScriptService.error.LintingError;
import com.printScript.PrintScriptService.error.ParsingError;
import com.printScript.PrintScriptService.web.BucketRequestExecutor;

import dataObjects.LinterResult;
import dataObjects.ParsingResult;
import factories.FormatterFactory;
import factories.LinterFactory;
import factories.Runner;
import factories.ValidatorFactory;
import utils.InterpreterResult;
import utils.MainStringInputProvider;
import utils.PercentageCollector;
import utils.StringInputProvider;

@Service
public class RunnerService {

    @Autowired
    BucketRequestExecutor bucketRequestExecutor;

    private static Logger logger = Logger.getLogger(RunnerService.class.getName());

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

public Response<Void> getLintingErrors(String text, String version, String userId) {
        InputStream code = new ByteArrayInputStream(text.getBytes());

        LinterFactory linter = new LinterFactory();
        PercentageCollector collector = new PercentageCollector();
        List<LintingError> errorList = new ArrayList<>();
        InputStream config;
        try {
            Response<String> response = bucketRequestExecutor.get("lint/" + userId, "");
            config = new ByteArrayInputStream(response.getData().getBytes());
        } catch (HttpClientErrorException e) {
            return Response.withError(new Error(e.getStatusCode().value(), e.getResponseBodyAsString()));
        }
        Iterator<LinterResult> results = linter.lintCode(code, lintDTO.getVersion(), config, collector).iterator();
        while (results.hasNext()) {
            LinterResult result = results.next();
            if (result.hasError()) {
                errorList.add(LintingError.of(result.getMessage()));
            }
        }
        if (errorList.isEmpty()) {
            return Response.withData(null);
        } else {
            String errors = "[";
            for (LintingError error : errorList) {
                errors += "{\"message\":\"" + error.getMessage() + "\"},";
            }
            errors = errors.substring(0, errors.length() - 1) + "]";
            return Response.withError(new Error(HttpStatus.EXPECTATION_FAILED.value(), errors));
        }
    }

    public Response<Void> formatFile(String text, String version, String userId, String snippetId) {
        InputStream code = new ByteArrayInputStream(text.getBytes());
        LinterFactory linter = new LinterFactory();
        PercentageCollector collector = new PercentageCollector();
        List<LintingError> errorList = new ArrayList<>();
        InputStream config = null;
        try {
            Response<String> response = bucketRequestExecutor.get("format/" + userId, "");
            config = new ByteArrayInputStream(response.getData().getBytes());
        } catch (HttpClientErrorException e) {
            return Response.withError(new Error(e.getStatusCode().value(), e.getResponseBodyAsString()));
        }
        FormatterFactory formatter = new FormatterFactory();
        StringWriter writer = new StringWriter();
        try {
            formatter.format(code, config, version, writer);
        } catch (Exception e) {
            return Response.withError(new Error(500, e.getMessage()));
        }
        writer.flush();
        String formattedCode = writer.toString();

        try {
            bucketRequestExecutor.put("formatted/" + snippetId, formattedCode, "");
        } catch (HttpClientErrorException e) {
            return Response.withError(new Error(e.getStatusCode().value(), e.getResponseBodyAsString()));
        }

        return Response.withData(null);
    }
}
