package com.ilan.helpdesk.repository;

import com.ilan.helpdesk.model.Notification;
import com.ilan.helpdesk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long>{
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient);
}
