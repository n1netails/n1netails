package com.n1netails.n1netails.api.constant;

public class ProjectSecurityConstant {

    public static final long EXPIRATION_TIME = 432_000_000; // 5 days in milliseconds

    public static final String[] PUBLIC_URLS = {
            "/*.css",
            "/*.js",
            "/*.html",
            "/*.png",
            "/assets/**",
            "/svg/**",
            "/error**",
            "/actuator/health",
            "/actuator/info",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/user/login",
            "/api/user/register",
//            "/api/password/hello",
            "/"
    };

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String FORBIDDEN_MESSAGE = "You need to login in order to access this page.";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page.";

    private ProjectSecurityConstant() {}
}
