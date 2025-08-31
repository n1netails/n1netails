package com.n1netails.n1netails.api.util;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserUtil {

    public static String generateUserId() {
        return UUID.randomUUID().toString();
    }
}
