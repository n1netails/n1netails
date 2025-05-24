package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.response.UserResponse;
import com.n1netails.n1netails.api.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Admin Controller", description = "Operations for administrators")
@RestController
@RequestMapping(path = {"/api/admin"}, produces = APPLICATION_JSON)
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<Page<UserResponse>> listUsers(
            @PageableDefault(size = 10, sort = "email") Pageable pageable
    ) {
        log.info("Admin request to list users, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
}
