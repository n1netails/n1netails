package com.n1ne.n1netails.api.controller;

import com.n1ne.n1netails.api.model.request.TailRequest;
import com.n1ne.n1netails.api.model.response.TailResponse;
import com.n1ne.n1netails.api.service.TailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = {"/api/tail"})
public class TailController {

    private final TailService tailService;

    @PostMapping
    public ResponseEntity<TailResponse> create(@RequestBody TailRequest request) {
        return ResponseEntity.ok(tailService.createTail(request));
    }

    @GetMapping
    public ResponseEntity<List<TailResponse>> getAll() {
        return ResponseEntity.ok(tailService.getTails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tailService.getTailById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TailResponse> update(@PathVariable Long id, @RequestBody TailRequest request) {
        return ResponseEntity.ok(tailService.updateTail(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tailService.deleteTail(id);
        return ResponseEntity.noContent().build();
    }
}
