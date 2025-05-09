package com.n1ne.n1netails.api.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1ne.n1netails.api.model.response.HttpErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static com.n1ne.n1netails.api.constant.ProjectSecurityConstant.ACCESS_DENIED_MESSAGE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException exception) throws IOException, ServletException {
        HttpErrorResponse httpErrorResponse = new HttpErrorResponse(
                UNAUTHORIZED.value(),
                UNAUTHORIZED,
                UNAUTHORIZED.getReasonPhrase().toUpperCase(),
                ACCESS_DENIED_MESSAGE
        );

        httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(UNAUTHORIZED.value());
        OutputStream outputStream = httpServletResponse.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpErrorResponse);
        outputStream.flush();
    }
}
