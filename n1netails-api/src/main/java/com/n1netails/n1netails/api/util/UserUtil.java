package com.n1netails.n1netails.api.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;

@Slf4j
public class UserUtil {

    public static String generateUserId() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', '9')
                .build();
        return generator.generate(10);
    }
}
