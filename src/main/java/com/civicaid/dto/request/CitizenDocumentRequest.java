package com.civicaid.dto.request;

import com.civicaid.entity.CitizenDocument;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CitizenDocumentRequest {
    @NotNull
    private Long citizenId;
    @NotNull
    private CitizenDocument.DocType docType;
    @NotBlank
    private String fileUri;
}
