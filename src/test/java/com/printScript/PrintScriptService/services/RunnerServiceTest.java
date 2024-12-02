package com.printScript.PrintScriptService.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import com.printScript.PrintScriptService.DTO.Response;
import com.printScript.PrintScriptService.TestSecurityConfig;
import com.printScript.PrintScriptService.error.ParsingError;
import com.printScript.PrintScriptService.redis.LintConsumer;
import com.printScript.PrintScriptService.redis.FormatConsumer;
import com.printScript.PrintScriptService.web.BucketRequestExecutor;

import DTO.FormatConfigDTO;
import DTO.LintingConfigDTO;
import Utils.FormatSerializer;
import Utils.LintSerializer;

@ActiveProfiles("test")
@MockitoSettings(strictness = Strictness.LENIENT)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class RunnerServiceTest {
    @Autowired
    private RunnerService runnerService;

    @MockBean
    private BucketRequestExecutor bucketRequestExecutor;

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

        when(bucketRequestExecutor.get(startsWith("snippets/"), anyString()))
                .thenReturn(Response.withData("println('Hello World!');"));

        LintingConfigDTO lintingConfigDTO = new LintingConfigDTO();
        lintingConfigDTO.setIdentifierFormat(LintingConfigDTO.IdentifierFormat.CAMEL_CASE);
        lintingConfigDTO.setRestrictPrintln(true);
        lintingConfigDTO.setRestrictReadInput(false);
        String lintJson = new LintSerializer().serialize(lintingConfigDTO);

        when(bucketRequestExecutor.get(startsWith("lint/"), anyString())).thenReturn(Response.withData(lintJson));

        FormatConfigDTO formatConfigDTO = new FormatConfigDTO();
        formatConfigDTO.setIndentInsideBraces(4);
        formatConfigDTO.setIfBraceBelowLine(false);
        formatConfigDTO.setSpaceAfterColon(true);
        formatConfigDTO.setSpaceBeforeColon(true);
        formatConfigDTO.setEnforceSpacingAroundOperators(true);
        formatConfigDTO.setNewLineAfterSemicolon(true);
        formatConfigDTO.setSpaceAroundEquals(true);
        formatConfigDTO.setEnforceSpacingBetweenTokens(false);
        formatConfigDTO.setLinesBeforePrintln(0);
        String formatJson = new FormatSerializer().serialize(formatConfigDTO);

        when(bucketRequestExecutor.get(startsWith("format/"), anyString())).thenReturn(Response.withData(formatJson));

        when(bucketRequestExecutor.put(startsWith("formatted/"), anyString(), anyString()))
                .thenReturn(Response.withData(null));
    }

    private String base64Encode(String value) {
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes());
    }

    @Test
    void testValidate() {
        Response<List<ParsingError>> response = runnerService.validate("println('Hello World!');", "1.1");

        assertNull(response.getData());

        Response<List<ParsingError>> response2 = runnerService.validate("println('Hello World!')", "1.1");

        assertTrue(response2.getData().size() == 1);
    }

    @Test
    void testExecute() {
        Response<List<String>> response = runnerService.execute("snippetId", "1.1", List.of());

        assertEquals(List.of("Hello World!"), response.getData());
    }

    @Test
    void testGetLintingErrors() {
        Response<Void> response = runnerService.getLintingErrors("println('Hello ' + 'World!');", "1.1", "userId");

        assertNull(response.getData());
    }

    @Test
    void testFormatFile() {
        Response<Void> response = runnerService.formatFile("println('Hello World!');", "1.1", "userId", "snippetId");

        assertNull(response.getData());
    }
}
