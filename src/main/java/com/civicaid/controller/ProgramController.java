package com.civicaid.controller;

import com.civicaid.dto.request.ProgramRequest;
import com.civicaid.dto.response.ApiResponse;
import com.civicaid.dto.response.ProgramResponse;
import com.civicaid.entity.Program;
import com.civicaid.service.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<ProgramResponse>> createProgram(@Valid @RequestBody ProgramRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(programService.createProgram(request), "Program created"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProgramResponse>> getProgramById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(programService.getProgramById(id)));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ProgramResponse>>> getAllPrograms(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) Program.ProgramStatus status,
            @RequestParam(required = false) String search) {
        Page<ProgramResponse> result;
        if (search != null) result = programService.searchPrograms(search, pageable);
        else if (status != null) result = programService.getProgramsByStatus(status, pageable);
        else result = programService.getAllPrograms(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<ProgramResponse>> updateProgram(
            @PathVariable Long id, @Valid @RequestBody ProgramRequest request) {
        return ResponseEntity.ok(ApiResponse.success(programService.updateProgram(id, request), "Program updated"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('PROGRAM_MANAGER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> updateProgramStatus(
            @PathVariable Long id, @RequestParam Program.ProgramStatus status) {
        programService.updateProgramStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(null, "Program status updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteProgram(@PathVariable Long id) {
        programService.deleteProgram(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Program deleted"));
    }
}
