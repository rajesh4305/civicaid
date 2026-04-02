package com.civicaid.service.impl;

import com.civicaid.dto.request.CitizenDocumentRequest;
import com.civicaid.dto.response.CitizenDocumentResponse;
import com.civicaid.entity.Citizen;
import com.civicaid.entity.CitizenDocument;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.CitizenDocumentRepository;
import com.civicaid.repository.CitizenRepository;
import com.civicaid.service.CitizenDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CitizenDocumentServiceImpl implements CitizenDocumentService {

    private final CitizenDocumentRepository documentRepository;
    private final CitizenRepository citizenRepository;

    @Override
    @Transactional
    public CitizenDocumentResponse uploadDocument(CitizenDocumentRequest request) {
        Citizen citizen = citizenRepository.findById(request.getCitizenId())
                .orElseThrow(() -> new ResourceNotFoundException("Citizen", request.getCitizenId()));

        CitizenDocument document = CitizenDocument.builder()
                .citizen(citizen)
                .docType(request.getDocType())
                .fileUri(request.getFileUri())
                .verificationStatus(CitizenDocument.VerificationStatus.PENDING)
                .build();

        return mapToResponse(documentRepository.save(document));
    }

    @Override
    public CitizenDocumentResponse getDocumentById(Long id) {
        return mapToResponse(documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id)));
    }

    @Override
    public List<CitizenDocumentResponse> getDocumentsByCitizen(Long citizenId) {
        return documentRepository.findByCitizen_CitizenId(citizenId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public CitizenDocumentResponse verifyDocument(Long id, CitizenDocument.VerificationStatus status, String notes) {
        CitizenDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));
        document.setVerificationStatus(status);
        document.setVerificationNotes(notes);
        return mapToResponse(documentRepository.save(document));
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) throw new ResourceNotFoundException("Document", id);
        documentRepository.deleteById(id);
    }

    private CitizenDocumentResponse mapToResponse(CitizenDocument d) {
        return CitizenDocumentResponse.builder()
                .documentId(d.getDocumentId())
                .citizenId(d.getCitizen().getCitizenId())
                .citizenName(d.getCitizen().getName())
                .docType(d.getDocType())
                .fileUri(d.getFileUri())
                .uploadedDate(d.getUploadedDate())
                .verificationStatus(d.getVerificationStatus())
                .verificationNotes(d.getVerificationNotes())
                .build();
    }
}
