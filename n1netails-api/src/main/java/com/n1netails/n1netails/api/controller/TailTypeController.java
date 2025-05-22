package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.service.TailTypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Tail Type Controller", description = "Operations related to Tail Types")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/api/tail-type"})
public class TailTypeController {

    private final TailTypeService tailTypeService;
}
