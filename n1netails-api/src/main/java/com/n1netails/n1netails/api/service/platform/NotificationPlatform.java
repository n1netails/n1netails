package com.n1netails.n1netails.api.service.platform;

import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;

public interface NotificationPlatform {

    String getPlatformName();

    void send(KudaTailRequest request, NotificationConfigEntity config) throws Exception;
}
