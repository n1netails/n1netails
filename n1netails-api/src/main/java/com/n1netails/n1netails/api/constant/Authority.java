package com.n1netails.n1netails.api.constant;

public class Authority {
    public static final String[] USER_AUTHORITIES = { "user:read" };
    public static final String[] HR_AUTHORITIES = { "user:read", "user:update" };
    public static final String[] MANAGER_AUTHORITIES = { "user:read", "user:update" };
    public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:update", "user:create", "user:admin" };
    public static final String[] SUPER_ADMIN_AUTHORITIES = { "user:read", "user:update", "user:create", "user:admin", "user:delete", "user:super" };

    private Authority() {}
}
