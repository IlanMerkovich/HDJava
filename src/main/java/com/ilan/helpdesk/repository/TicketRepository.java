package com.ilan.helpdesk.repository;

import com.ilan.helpdesk.enums.TicketPriority;
import com.ilan.helpdesk.enums.TicketStatus;
import com.ilan.helpdesk.model.Ticket;
import com.ilan.helpdesk.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByCreatedBy(User user, Pageable pageable);
    Page<Ticket> findByCreatedByAndStatus(User user, TicketStatus status, Pageable pageable);
    Page<Ticket> findByCreatedByAndPriority(User user, TicketPriority priority, Pageable pageable);
    Page<Ticket> findByCreatedByAndStatusAndPriority(User user, TicketStatus status, TicketPriority priority, Pageable pageable);
    Page<Ticket> findByAssignedTo(User user, Pageable pageable);
    Page<Ticket> findByAssignedToAndStatus(User user, TicketStatus status, Pageable pageable);
    Page<Ticket> findByAssignedToAndPriority(User user, TicketPriority priority, Pageable pageable);
    Page<Ticket> findByAssignedToAndStatusAndPriority(User user, TicketStatus status, TicketPriority priority, Pageable pageable);
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);
    Page<Ticket> findByPriority(TicketPriority priority, Pageable pageable);
    Page<Ticket> findByStatusAndPriority(TicketStatus status, TicketPriority priority, Pageable pageable);
    Page<Ticket> findAll(Pageable pageable);
    @Query("""
SELECT t FROM Ticket t
WHERE t.createdBy = :user
AND (:status IS NULL OR t.status = :status)
AND (:priority IS NULL OR t.priority = :priority)
AND (
    :query IS NULL OR :query = '' OR
    LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR
    LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))
)
""")
    Page<Ticket> searchClientTickets(@Param("user") User user,
                                     @Param("status") TicketStatus status,
                                     @Param("priority") TicketPriority priority,
                                     @Param("query") String query,
                                     Pageable pageable);

    @Query("""
SELECT t FROM Ticket t
WHERE t.assignedTo = :user
AND (:status IS NULL OR t.status = :status)
AND (:priority IS NULL OR t.priority = :priority)
AND (
    :query IS NULL OR :query = '' OR
    LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR
    LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))
)
""")
    Page<Ticket> searchAgentTickets(@Param("user") User user,
                                    @Param("status") TicketStatus status,
                                    @Param("priority") TicketPriority priority,
                                    @Param("query") String query,
                                    Pageable pageable);

    @Query("""
SELECT t FROM Ticket t
WHERE (:status IS NULL OR t.status = :status)
AND (:priority IS NULL OR t.priority = :priority)
AND (
    :query IS NULL OR :query = '' OR
    LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR
    LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))
)
""")
    Page<Ticket> searchAdminTickets(@Param("status") TicketStatus status,
                                    @Param("priority") TicketPriority priority,
                                    @Param("query") String query,
                                    Pageable pageable);



}