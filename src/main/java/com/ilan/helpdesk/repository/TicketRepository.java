package com.ilan.helpdesk.repository;

import com.ilan.helpdesk.dto.DashboardAgentWorkloadProjection;
import com.ilan.helpdesk.dto.DashboardCountsProjection;
import com.ilan.helpdesk.enums.Role;
import com.ilan.helpdesk.enums.TicketPriority;
import com.ilan.helpdesk.enums.TicketStatus;
import com.ilan.helpdesk.model.Ticket;
import com.ilan.helpdesk.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByCreatedBy(User user, Pageable pageable);
    List<Ticket> findByCreatedBy(User user);
    Page<Ticket> findByCreatedByAndStatus(User user, TicketStatus status, Pageable pageable);
    Page<Ticket> findByCreatedByAndPriority(User user, TicketPriority priority, Pageable pageable);
    Page<Ticket> findByCreatedByAndStatusAndPriority(User user, TicketStatus status, TicketPriority priority, Pageable pageable);
    Page<Ticket> findByAssignedTo(User user, Pageable pageable);
    List<Ticket> findByAssignedTo(User user);
    Page<Ticket> findByAssignedToAndStatus(User user, TicketStatus status, Pageable pageable);
    Page<Ticket> findByAssignedToAndPriority(User user, TicketPriority priority, Pageable pageable);
    Page<Ticket> findByAssignedToAndStatusAndPriority(User user, TicketStatus status, TicketPriority priority, Pageable pageable);
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);
    Page<Ticket> findByPriority(TicketPriority priority, Pageable pageable);
    Page<Ticket> findByStatusAndPriority(TicketStatus status, TicketPriority priority, Pageable pageable);
    List<Ticket> findTop5ByOrderByCreatedAtDesc();
    List<Ticket> findTop5ByOrderByUpdatedAtDesc();
    List<Ticket> findTop5ByCreatedByOrderByCreatedAtDesc(User user);
    List<Ticket> findTop5ByCreatedByOrderByUpdatedAtDesc(User user);
    List<Ticket> findTop5ByAssignedToOrderByCreatedAtDesc(User user);
    List<Ticket> findTop5ByAssignedToOrderByUpdatedAtDesc(User user);

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

    @Query("""
select
    count(t) as totalTickets,
    coalesce(sum(case when t.status = :openStatus then 1 else 0 end), 0) as openTickets,
    coalesce(sum(case when t.status = :inProgressStatus then 1 else 0 end), 0) as inProgressTickets,
    coalesce(sum(case when t.status = :resolvedStatus then 1 else 0 end), 0) as resolvedTickets,
    coalesce(sum(case when t.status = :closedStatus then 1 else 0 end), 0) as closedTickets,
    coalesce(sum(case when t.priority = :highPriority then 1 else 0 end), 0) as highPriorityTickets,
    coalesce(sum(case when t.priority = :mediumPriority then 1 else 0 end), 0) as mediumPriorityTickets,
    coalesce(sum(case when t.priority = :lowPriority then 1 else 0 end), 0) as lowPriorityTickets,
    coalesce(sum(case when t.assignedTo is not null then 1 else 0 end), 0) as assignedTickets,
    coalesce(sum(case when t.assignedTo is null then 1 else 0 end), 0) as unassignedTickets
from Ticket t
where t.createdBy = :user
""")
    DashboardCountsProjection getClientDashboardCounts(@Param("user") User user,
                                                       @Param("openStatus") TicketStatus openStatus,
                                                       @Param("inProgressStatus") TicketStatus inProgressStatus,
                                                       @Param("resolvedStatus") TicketStatus resolvedStatus,
                                                       @Param("closedStatus") TicketStatus closedStatus,
                                                       @Param("highPriority") TicketPriority highPriority,
                                                       @Param("mediumPriority") TicketPriority mediumPriority,
                                                       @Param("lowPriority") TicketPriority lowPriority);

    @Query("""
select
    count(t) as totalTickets,
    coalesce(sum(case when t.status = :openStatus then 1 else 0 end), 0) as openTickets,
    coalesce(sum(case when t.status = :inProgressStatus then 1 else 0 end), 0) as inProgressTickets,
    coalesce(sum(case when t.status = :resolvedStatus then 1 else 0 end), 0) as resolvedTickets,
    coalesce(sum(case when t.status = :closedStatus then 1 else 0 end), 0) as closedTickets,
    coalesce(sum(case when t.priority = :highPriority then 1 else 0 end), 0) as highPriorityTickets,
    coalesce(sum(case when t.priority = :mediumPriority then 1 else 0 end), 0) as mediumPriorityTickets,
    coalesce(sum(case when t.priority = :lowPriority then 1 else 0 end), 0) as lowPriorityTickets,
    coalesce(sum(case when t.assignedTo is not null then 1 else 0 end), 0) as assignedTickets,
    coalesce(sum(case when t.assignedTo is null then 1 else 0 end), 0) as unassignedTickets
from Ticket t
where t.assignedTo = :user
""")
    DashboardCountsProjection getAgentDashboardCounts(@Param("user") User user,
                                                      @Param("openStatus") TicketStatus openStatus,
                                                      @Param("inProgressStatus") TicketStatus inProgressStatus,
                                                      @Param("resolvedStatus") TicketStatus resolvedStatus,
                                                      @Param("closedStatus") TicketStatus closedStatus,
                                                      @Param("highPriority") TicketPriority highPriority,
                                                      @Param("mediumPriority") TicketPriority mediumPriority,
                                                      @Param("lowPriority") TicketPriority lowPriority);

    @Query("""
select
    count(t) as totalTickets,
    coalesce(sum(case when t.status = :openStatus then 1 else 0 end), 0) as openTickets,
    coalesce(sum(case when t.status = :inProgressStatus then 1 else 0 end), 0) as inProgressTickets,
    coalesce(sum(case when t.status = :resolvedStatus then 1 else 0 end), 0) as resolvedTickets,
    coalesce(sum(case when t.status = :closedStatus then 1 else 0 end), 0) as closedTickets,
    coalesce(sum(case when t.priority = :highPriority then 1 else 0 end), 0) as highPriorityTickets,
    coalesce(sum(case when t.priority = :mediumPriority then 1 else 0 end), 0) as mediumPriorityTickets,
    coalesce(sum(case when t.priority = :lowPriority then 1 else 0 end), 0) as lowPriorityTickets,
    coalesce(sum(case when t.assignedTo is not null then 1 else 0 end), 0) as assignedTickets,
    coalesce(sum(case when t.assignedTo is null then 1 else 0 end), 0) as unassignedTickets
from Ticket t
""")
    DashboardCountsProjection getAdminDashboardCounts(@Param("openStatus") TicketStatus openStatus,
                                                      @Param("inProgressStatus") TicketStatus inProgressStatus,
                                                      @Param("resolvedStatus") TicketStatus resolvedStatus,
                                                      @Param("closedStatus") TicketStatus closedStatus,
                                                      @Param("highPriority") TicketPriority highPriority,
                                                      @Param("mediumPriority") TicketPriority mediumPriority,
                                                      @Param("lowPriority") TicketPriority lowPriority);

    @Query("""
select
    u.id as agentId,
    u.email as agentEmail,
    u.fullName as agentFullName,
    count(t) as assignedTickets,
    coalesce(sum(case when t.status = :openStatus then 1 else 0 end), 0) as openTickets,
    coalesce(sum(case when t.status = :inProgressStatus then 1 else 0 end), 0) as inProgressTickets,
    coalesce(sum(case when t.status = :resolvedStatus then 1 else 0 end), 0) as resolvedTickets,
    coalesce(sum(case when t.status = :closedStatus then 1 else 0 end), 0) as closedTickets,
    coalesce(sum(case when t.priority = :highPriority then 1 else 0 end), 0) as highPriorityTickets
from User u
left join Ticket t on t.assignedTo = u
where u.role = :agentRole
group by u.id, u.email, u.fullName
order by count(t) desc, u.fullName asc
""")
    List<DashboardAgentWorkloadProjection> getAllAgentsWorkload(
            @Param("agentRole") Role agentRole,
            @Param("openStatus") TicketStatus openStatus,
            @Param("inProgressStatus") TicketStatus inProgressStatus,
            @Param("resolvedStatus") TicketStatus resolvedStatus,
            @Param("closedStatus") TicketStatus closedStatus,
            @Param("highPriority") TicketPriority highPriority
    );

    @Query("""
select
    u.id as agentId,
    u.email as agentEmail,
    u.fullName as agentFullName,
    count(t) as assignedTickets,
    coalesce(sum(case when t.status = :openStatus then 1 else 0 end), 0) as openTickets,
    coalesce(sum(case when t.status = :inProgressStatus then 1 else 0 end), 0) as inProgressTickets,
    coalesce(sum(case when t.status = :resolvedStatus then 1 else 0 end), 0) as resolvedTickets,
    coalesce(sum(case when t.status = :closedStatus then 1 else 0 end), 0) as closedTickets,
    coalesce(sum(case when t.priority = :highPriority then 1 else 0 end), 0) as highPriorityTickets
from User u
left join Ticket t on t.assignedTo = u
where u.id = :agentId
group by u.id, u.email, u.fullName
""")
    List<DashboardAgentWorkloadProjection> getSingleAgentWorkload(
            @Param("agentId") Long agentId,
            @Param("openStatus") TicketStatus openStatus,
            @Param("inProgressStatus") TicketStatus inProgressStatus,
            @Param("resolvedStatus") TicketStatus resolvedStatus,
            @Param("closedStatus") TicketStatus closedStatus,
            @Param("highPriority") TicketPriority highPriority
    );



}