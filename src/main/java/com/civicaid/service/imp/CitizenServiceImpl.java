package com.civicaid.service.impl;

import com.civicaid.dto.request.CitizenRequest;
import com.civicaid.dto.response.CitizenResponse;
import com.civicaid.entity.Citizen;
import com.civicaid.entity.User;
import com.civicaid.exception.BusinessException;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.CitizenRepository;
import com.civicaid.repository.UserRepository;
import com.civicaid.service.CitizenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CitizenServiceImpl implements CitizenService {

    private final CitizenRepository citizenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CitizenResponse registerCitizen(Long userId, CitizenRequest request) {
        if (citizenRepository.existsByUser_UserId(userId)) {
            throw new BusinessException("Citizen profile already exists for this user");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Citizen citizen = Citizen.builder()
                .name(request.getName())
                .dob(request.getDob())
                .gender(request.getGender())
                .address(request.getAddress())
                .contactInfo(request.getContactInfo())
                .user(user)
                .status(Citizen.CitizenStatus.PENDING)
                .build();
        return mapToResponse(citizenRepository.save(citizen));
    }

    @Override
    public CitizenResponse getCitizenById(Long id) {
        return mapToResponse(citizenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen", id)));
    }

    @Override
    public CitizenResponse getCitizenByUserId(Long userId) {
        return mapToResponse(citizenRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen profile not found for user: " + userId)));
    }

    @Override
    @Transactional
    public CitizenResponse updateCitizen(Long id, CitizenRequest request) {
        Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen", id));
        citizen.setName(request.getName());
        citizen.setDob(request.getDob());
        citizen.setGender(request.getGender());
        citizen.setAddress(request.getAddress());
        citizen.setContactInfo(request.getContactInfo());
        return mapToResponse(citizenRepository.save(citizen));
    }

    @Override
    @Transactional
    public void updateCitizenStatus(Long id, Citizen.CitizenStatus status) {
        Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen", id));
        citizen.setStatus(status);
        citizenRepository.save(citizen);
    }

    @Override
    public Page<CitizenResponse> getAllCitizens(Pageable pageable) {
        return citizenRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public Page<CitizenResponse> getCitizensByStatus(Citizen.CitizenStatus status, Pageable pageable) {
        return citizenRepository.findByStatus(status, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<CitizenResponse> searchCitizens(String name, Pageable pageable) {
        return citizenRepository.searchByName(name, pageable).map(this::mapToResponse);
    }

    private CitizenResponse mapToResponse(Citizen c) {
        return CitizenResponse.builder()
                .citizenId(c.getCitizenId())
                .name(c.getName())
                .dob(c.getDob())
                .gender(c.getGender())
                .address(c.getAddress())
                .contactInfo(c.getContactInfo())
                .status(c.getStatus())
                .userId(c.getUser() != null ? c.getUser().getUserId() : null)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
