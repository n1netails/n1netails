package com.n1netails.n1netails.api.model.request;

import lombok.Data;

@Data
public class NotePageRequest {
    private int page = 0; // Default to first page
    private int size = 10; // Default to 10 items per page
    private Long tailId;
}
