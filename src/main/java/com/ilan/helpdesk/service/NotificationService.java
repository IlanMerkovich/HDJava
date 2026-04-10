package com.ilan.helpdesk.service;

import com.ilan.helpdesk.dto.NotificationResponse;
import com.ilan.helpdesk.enums.NotificationType;
import com.ilan.helpdesk.exception.ResourceNotFoundException;
import com.ilan.helpdesk.model.Notification;
import com.ilan.helpdesk.model.User;
import com.ilan.helpdesk.repository.NotificationRepository;
import com.ilan.helpdesk.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,UserRepository userRepository){
        this.notificationRepository=notificationRepository;
        this.userRepository=userRepository;
    }
    public List<NotificationResponse> getMyResponses(Authentication authentication){
        User user=getCurrentUser(authentication);
        List<Notification>notifications=notificationRepository.findByRecipientOrderByCreatedAtDesc(user);

        List<NotificationResponse>responses=new ArrayList<>();
        for (Notification notification:notifications){
            responses.add(mapToResponse(notification));
        }
        return responses;
    }
    public void createNotification(User user, String message, NotificationType type,long relatedId){
        Notification notification=new Notification();
        notification.setMessage(message);
        notification.setRecipient(user);
        notification.setType(type);
        notification.setRelatedTicketId(relatedId);

        notificationRepository.save(notification);
    }
    public void markAllAsRead(Authentication authentication){
        User user=getCurrentUser(authentication);
        List<Notification>notifications=notificationRepository.findByRecipientOrderByCreatedAtDesc(user);

        for (Notification notification:notifications){
            notification.setRead(true);
        }
        notificationRepository.saveAll(notifications);
    }
    public NotificationResponse markAsRead(long id,Authentication authentication){
        User user=getCurrentUser(authentication);
        Notification notification=notificationRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("notification not found"));

        if (!notification.getRecipient().getId().equals(notification.getRecipient())){
            throw new ResourceNotFoundException("notification with id "+id+ " not found");
        }
        notification.setRead(true);
        Notification saved=notificationRepository.save(notification);

        return mapToResponse(saved);
    }



    private User getCurrentUser(Authentication authentication){
        String email=authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("user not found"));
    }
    private NotificationResponse mapToResponse(Notification notification){
        NotificationResponse response=new NotificationResponse();

        response.setId(notification.getId());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setCreatedAt(notification.getCreatedAt());
        response.setRelatedTicket(notification.getRelatedTicketId());
        response.setRead(notification.isRead());

        return response;
    }

}
