package com.n1netails.n1netails.api.inari.service;

import com.n1netails.n1netails.api.model.dto.Note;
import com.n1netails.n1netails.api.model.response.TailResponse;

import java.util.List;

public interface InariService {

    void handleTailAlert(String owner, String repository, String branch, TailResponse tailResponse) throws Exception;
}
