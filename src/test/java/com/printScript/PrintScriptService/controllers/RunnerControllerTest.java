package com.printScript.PrintScriptService.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import com.printScript.PrintScriptService.DTO.Response;
import com.printScript.PrintScriptService.DTO.TestContextDTO;
import com.printScript.PrintScriptService.DTO.ValidateRequestDTO;
import com.printScript.PrintScriptService.TestSecurityConfig;
import com.printScript.PrintScriptService.redis.FormatConsumer;
import com.printScript.PrintScriptService.redis.LintConsumer;
import com.printScript.PrintScriptService.services.RunnerService;

@ActiveProfiles("test")
@MockitoSettings(strictness = Strictness.LENIENT)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class RunnerControllerTest {
    @Autowired
    private RunnerController runnerController;

    @MockBean
    private RunnerService runnerService;

    @MockBean
    private LintConsumer lintConsumer;

    @MockBean
    private FormatConsumer formatConsumer;

    private String mockToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"mockUserId\",\"username\":\"mockUsername\",\"role\":\"user\",\"iat\":1609459200}";
        String signature = "mockSignature";

        mockToken = base64Encode(header) + "." + base64Encode(payload) + "." + signature;
        mockToken = "Bearer " + mockToken;

        when(jwt.getTokenValue()).thenReturn(mockToken);
        when(jwt.getClaim("sub")).thenReturn("mockUserId");
        when(jwt.getClaim("username")).thenReturn("mockUsername");
        when(jwt.getClaim("role")).thenReturn("user");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContextHolder.setContext(securityContext);
    }

    private String base64Encode(String value) {
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes());
    }

  @Test
  void testValidate() {
    when(runnerService.validate(anyString(), anyString())).thenReturn(Response.withData(null));

    ResponseEntity<Object> response =
        runnerController.validate(new ValidateRequestDTO("code", "version"));

    assertEquals(200, response.getStatusCode().value());
  }

  @Test
  void testExecuteTest() {
    when(runnerService.execute(anyString(), anyString(), anyList()))
        .thenReturn(Response.withData(List.of("Hello World!")));

    ResponseEntity<Object> response =
        runnerController.executeTest(
            new TestContextDTO("snippetId", "version", List.of("input"), List.of("Hello World!")));

    assertEquals(200, response.getStatusCode().value());

    ResponseEntity<Object> response2 =
        runnerController.executeTest(
            new TestContextDTO("snippetId", "version", List.of("input"), List.of("Hello World!2")));

    assertEquals(417, response2.getStatusCode().value());
  }
}
