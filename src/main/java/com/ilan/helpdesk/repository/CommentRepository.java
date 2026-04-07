package com.ilan.helpdesk.repository;

import com.ilan.helpdesk.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTicketId(Long ticketId);
}