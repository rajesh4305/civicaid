package com.civicaid.service;

import com.civicaid.dto.request.ProgramRequest;
import com.civicaid.dto.response.ProgramResponse;
import com.civicaid.entity.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProgramService {
    ProgramResponse createProgram(ProgramRequest request);
    ProgramResponse getProgramById(Long id);
    Page<ProgramResponse> getAllPrograms(Pageable pageable);
    Page<ProgramResponse> getProgramsByStatus(Program.ProgramStatus status, Pageable pageable);
    Page<ProgramResponse> searchPrograms(String keyword, Pageable pageable);
    ProgramResponse updateProgram(Long id, ProgramRequest request);
    void updateProgramStatus(Long id, Program.ProgramStatus status);
    void deleteProgram(Long id);
}
