package com.civicaid.dto.response;

import com.civicaid.entity.CitizenDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CitizenDocumentResponse {
    private Long documentId;
    private Long citizenId;
    private String citizenName;
    private CitizenDocument.DocType docType;
    private String fileUri;
    private LocalDateTime uploadedDate;
    private CitizenDocument.VerificationStatus verificationStatus;
    private String verificationNotes;
}
