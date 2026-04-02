package com.civicaid.service;

import com.civicaid.dto.response.NotificationResponse;
import com.civicaid.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void sendNotification(Long userId, Long entityId, String message, Notification.NotificationCategory category);
    Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable);
    Page<NotificationResponse> getUnreadNotifications(Long userId, Pageable pageable);
    long getUnreadCount(Long userId);
    void markAsRead(Long notificationId);
    int markAllAsRead(Long userId);
    void deleteNotification(Long id);
}
