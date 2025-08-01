package com.n1netails.n1netails.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SendMailRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMailRequest {
    private String notificationTemplateId;
    private String to;
    private List<String> cc = new ArrayList<>();
    private List<String> bcc = new ArrayList<>();
    private Map<String, String> subjectParams  = new HashMap<>();
    private Map<String, String> bodyParams = new HashMap<>();
}
