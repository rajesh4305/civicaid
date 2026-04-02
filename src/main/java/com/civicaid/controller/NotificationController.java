package com.civicaid.controller;

import com.civicaid.dto.response.ApiResponse;
import com.civicaid.dto.response.NotificationResponse;
import com.civicaid.entity.User;
import com.civicaid.exception.ResourceNotFoundException;
import com.civicaid.repository.UserRepository;
import com.civicaid.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        User user = resolveUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getUserNotifications(user.getUserId(), pageable)));
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        User user = resolveUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getUnreadNotifications(user.getUserId(), pageable)));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = resolveUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUnreadCount(user.getUserId())));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification marked as read"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Integer>> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = resolveUser(userDetails);
        int count = notificationService.markAllAsRead(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(count, count + " notifications marked as read"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification deleted"));
    }

    private User resolveUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
