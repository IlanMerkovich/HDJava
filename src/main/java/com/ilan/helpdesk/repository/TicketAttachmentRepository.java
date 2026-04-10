package com.ilan.helpdesk.repository;

import com.ilan.helpdesk.model.TicketAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketAttachmentRepository extends JpaRepository<TicketAttachment, Long> {

    @Query("""
            select a
            from TicketAttachment a
            join fetch a.createdBy
            where a.ticket.id = :ticketId
            order by a.createdAt desc
            """)
    List<TicketAttachment> findAllByTicketIdWithCreatedBy(@Param("ticketId") Long ticketId);
}