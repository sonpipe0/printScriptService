package com.printScript.PrintScriptService.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.printScript.PrintScriptService.services.RestTemplateService;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplateService snippetRestTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplateService restTemplateService = new RestTemplateService(builder);
        restTemplateService.snippetRestTemplate(builder);
        return restTemplateService;
    }

    @Bean
    public RestTemplateService permissionsRestTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplateService restTemplateService = new RestTemplateService(builder);
        restTemplateService.permissionsRestTemplate(builder);
        return restTemplateService;
    }

    @Bean
    public RestTemplateService bucketRestTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplateService restTemplateService = new RestTemplateService(builder);
        restTemplateService.bucketRestTemplate(builder);
        return restTemplateService;
    }
}
