package com.n1ne.n1netails.api.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1ne.n1netails.api.model.response.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static com.n1ne.n1netails.api.constant.ProjectSecurityConstant.FORBIDDEN_MESSAGE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAuthenticationEntryHandler extends Http403ForbiddenEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException exception) throws IOException {

        HttpResponse httpResponse = new HttpResponse(
                FORBIDDEN.value(),
                FORBIDDEN,
                FORBIDDEN.getReasonPhrase().toUpperCase(),
                FORBIDDEN_MESSAGE
        );

        httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(FORBIDDEN.value());
        OutputStream outputStream = httpServletResponse.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}
