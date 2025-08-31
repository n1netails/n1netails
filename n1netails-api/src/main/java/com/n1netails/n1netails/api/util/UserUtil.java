package com.n1netails.n1netails.api.util;

import java.util.UUID;

import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import lombok.extern.slf4j.Slf4j;

import static com.n1netails.n1netails.api.service.impl.TailServiceImpl.N1NETAILS_ORG;

@Slf4j
public class UserUtil {

    public static String generateUserId() {
        return UUID.randomUUID().toString();
    }

    public static boolean isInN1netailsOrg(UserPrincipal currentUser) {
        boolean isN1netails = currentUser.getOrganizations().stream()
                .map(OrganizationEntity::getName)
                .anyMatch(N1NETAILS_ORG::equals);
        return isN1netails;
    }
}
