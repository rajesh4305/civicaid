package com.civicaid.controller;

import com.civicaid.dto.request.SchemeRequest;
import com.civicaid.dto.response.ApiResponse;
import com.civicaid.dto.response.SchemeResponse;
import com.civicaid.entity.Scheme;
import com.civicaid.service.SchemeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schemes")
@RequiredArgsConstructor
public class SchemeController {

    private final SchemeService schemeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<SchemeResponse>> createScheme(@Valid @RequestBody SchemeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(schemeService.createScheme(request), "Scheme created"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SchemeResponse>> getSchemeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(schemeService.getSchemeById(id)));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<SchemeResponse>>> getAllSchemes(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(schemeService.getAllSchemes(pageable)));
    }

    @GetMapping("/program/{programId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SchemeResponse>>> getSchemesByProgram(@PathVariable Long programId) {
        return ResponseEntity.ok(ApiResponse.success(schemeService.getSchemesByProgram(programId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<SchemeResponse>> updateScheme(
            @PathVariable Long id, @Valid @RequestBody SchemeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(schemeService.updateScheme(id, request), "Scheme updated"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> updateSchemeStatus(
            @PathVariable Long id, @RequestParam Scheme.SchemeStatus status) {
        schemeService.updateSchemeStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(null, "Scheme status updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteScheme(@PathVariable Long id) {
        schemeService.deleteScheme(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Scheme deleted"));
    }
}
