package com.ilan.helpdesk.service;

import com.ilan.helpdesk.dto.*;
import com.ilan.helpdesk.enums.Role;
import com.ilan.helpdesk.enums.TicketHistoryActionType;
import com.ilan.helpdesk.enums.TicketPriority;
import com.ilan.helpdesk.enums.TicketStatus;
import com.ilan.helpdesk.exception.InvalidTicketStateException;
import com.ilan.helpdesk.exception.InvalidUserRoleException;
import com.ilan.helpdesk.exception.ResourceNotFoundException;
import com.ilan.helpdesk.model.Comment;
import com.ilan.helpdesk.model.Ticket;
import com.ilan.helpdesk.model.TicketHistory;
import com.ilan.helpdesk.model.User;
import com.ilan.helpdesk.repository.CommentRepository;
import com.ilan.helpdesk.repository.TicketHistoryRepository;
import com.ilan.helpdesk.repository.TicketRepository;
import com.ilan.helpdesk.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TicketHistoryRepository ticketHistoryRepository;

    public PagedTicketResponse getAllTicketsPaged(TicketStatus status, TicketPriority priority, int page, int size,
                                                  String sortBy, String direction, Authentication authentication, String query) {
        User currentUser = getCurrentUser(authentication);

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        if (size > 100) {
            size = 100;
        }

        List<String> allowedSortFields = List.of("id", "title", "status", "priority","createdAt","updatedAt");
        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "id";
        }

        Sort sort;
        if (direction.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Ticket> ticketPage;
        if (currentUser.getRole()== Role.CLIENT){
            ticketPage=ticketRepository.searchClientTickets(currentUser,status,priority,query,pageable);
        } else if (currentUser.getRole()==Role.AGENT){
            ticketPage=ticketRepository.searchAgentTickets(currentUser,status,priority,query,pageable);
        }
        else {
            ticketPage=ticketRepository.searchAdminTickets(status,priority,query,pageable);
        }

        List<TicketResponse> responses = new ArrayList<>();
        for (Ticket ticket : ticketPage.getContent()) {
            responses.add(mapTicketToResponse(ticket));
        }

        PagedTicketResponse response = new PagedTicketResponse();
        response.setContent(responses);
        response.setPage(ticketPage.getNumber());
        response.setSize(ticketPage.getSize());
        response.setTotalElements(ticketPage.getTotalElements());
        response.setTotalPages(ticketPage.getTotalPages());
        response.setFirst(ticketPage.isFirst());
        response.setLast(ticketPage.isLast());

        return response;
    }
    public TicketResponse assignTicket(long ticketId, AssignTicketRequest request, Authentication authentication) {
        Ticket ticket = findTicketEntityById(ticketId);
        User changedBy = getCurrentUser(authentication);
        User agent = userRepository.findById(request.getAgentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "agent with id " + request.getAgentId() + " not found"
                ));

        if (agent.getRole() != Role.AGENT) {
            throw new InvalidUserRoleException("cant assign ticket to a non-agent user");
        }
        String oldAssignedToEmail;
        if (ticket.getAssignedTo() != null) {
            oldAssignedToEmail = ticket.getAssignedTo().getEmail();
        } else {
            oldAssignedToEmail = null;
        }
        ticket.setAssignedTo(agent);
        Ticket updatedTicket = ticketRepository.save(ticket);
        createAssignmentHistory(updatedTicket, oldAssignedToEmail, agent.getEmail(), changedBy);

        return mapTicketToResponse(updatedTicket);
    }
    public TicketService(TicketRepository ticketRepository, CommentRepository commentRepository, UserRepository userRepository, TicketHistoryRepository ticketHistoryRepository) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.userRepository=userRepository;
        this.ticketHistoryRepository=ticketHistoryRepository;
    }
    /*
    public List<TicketResponse> getAllTickets(TicketStatus status, TicketPriority priority, Authentication authentication) {
        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Ticket> tickets;

        if (currentUser.getRole() == Role.CLIENT) {
            if (status != null && priority != null) {
                tickets = ticketRepository.findByCreatedByAndStatusAndPriority(currentUser, status, priority);
            } else if (status != null) {
                tickets = ticketRepository.findByCreatedByAndStatus(currentUser, status);
            } else if (priority != null) {
                tickets = ticketRepository.findByCreatedByAndPriority(currentUser, priority);
            } else {
                tickets = ticketRepository.findByCreatedBy(currentUser);
            }
        }
        else if (currentUser.getRole() == Role.AGENT) {
            if (status != null && priority != null) {
                tickets = ticketRepository.findByAssignedToAndStatusAndPriority(currentUser, status, priority);
            } else if (status != null) {
                tickets = ticketRepository.findByAssignedToAndStatus(currentUser, status);
            } else if (priority != null) {
                tickets = ticketRepository.findByAssignedToAndPriority(currentUser, priority);
            } else {
                tickets = ticketRepository.findByAssignedTo(currentUser);
            }
        }
        else {
            if (status != null && priority != null) {
                tickets = ticketRepository.findByStatusAndPriority(status, priority);
            } else if (status != null) {
                tickets = ticketRepository.findByStatus(status);
            } else if (priority != null) {
                tickets = ticketRepository.findByPriority(priority);
            } else {
                tickets = ticketRepository.findAll();
            }
        }

        List<TicketResponse> responses = new ArrayList<>();
        for (Ticket ticket : tickets) {
            responses.add(mapTicketToResponse(ticket));
        }

        return responses;
    }
     */
    public TicketResponse getTicketById(long id,Authentication authentication) {
        Ticket ticket = findTicketEntityById(id);
        User user=getCurrentUser(authentication);
        validateTicketAccess(user,ticket);

        return mapTicketToResponse(ticket);
    }
    public TicketResponse createTicket(CreateTicketRequest request, Authentication authentication) {
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("user not found"));

        Ticket ticket = new Ticket();
        ticket.setCreatedBy(user);
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(request.getPriority());

        Ticket savedTicket = ticketRepository.save(ticket);
        return mapTicketToResponse(savedTicket);
    }
    public TicketResponse updateTicketStatus(Long id, UpdateTicketStatusRequest request,Authentication authentication) {
        Ticket ticket = findTicketEntityById(id);
        User user=getCurrentUser(authentication);
        validateTicketAccess(user,ticket);

        TicketStatus oldStatus = ticket.getStatus();
        TicketStatus newStatus = request.getStatus();

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new InvalidTicketStateException("Cannot update a closed ticket");
        }

        if (newStatus == TicketStatus.CLOSED && ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new InvalidTicketStateException("Ticket can be closed only after it is resolved");
        }

        ticket.setStatus(newStatus);
        Ticket updatedTicket = ticketRepository.save(ticket);

        createStatusHistory(updatedTicket,oldStatus,newStatus,user);

        return mapTicketToResponse(updatedTicket);
    }
    public List<CommentResponse> getCommentsByTicketId(long ticketId,Authentication authentication) {
        Ticket ticket=findTicketEntityById(ticketId);
        User user=getCurrentUser(authentication);
        validateTicketAccess(user, ticket);

        List<Comment> comments = commentRepository.findByTicketId(ticketId);
        List<CommentResponse> responses = new ArrayList<>();

        for (Comment comment : comments) {
            responses.add(mapCommentToResponse(comment));
        }

        return responses;
    }
    public CommentResponse addCommentToTicket(Long ticketId, AddCommentRequest request, Authentication authentication) {
        Ticket ticket = findTicketEntityById(ticketId);
        User user=getCurrentUser(authentication);
        validateTicketAccess(user,ticket);

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setTicket(ticket);
        comment.setAuthor(user);

        Comment savedComment = commentRepository.save(comment);
        return mapCommentToResponse(savedComment);
    }
    private Ticket findTicketEntityById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + id + " not found"));
    }
    private TicketResponse mapTicketToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setStatus(ticket.getStatus());
        response.setPriority(ticket.getPriority());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());

        if (ticket.getCreatedBy() != null) {
            response.setCreatedByEmail(ticket.getCreatedBy().getEmail());
        }
        if (ticket.getAssignedTo()!=null){
            response.setAssignedToFullName(ticket.getAssignedTo().getFullName());
            response.setAssignedToEmail(ticket.getAssignedTo().getEmail());
        }

        response.setCommentsCount(0);
        response.setComments(new ArrayList<>());

        return response;
    }
    private CommentResponse mapCommentToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();

        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());

        if (comment.getAuthor()!=null){
            response.setAuthorEmail(comment.getAuthor().getEmail());
            response.setAuthorName(comment.getAuthor().getFullName());
        }
        return response;
    }
    private User getCurrentUser(Authentication authentication){
        String email=authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("user not found"));
    }
    private boolean canAccessTicket(User user,Ticket ticket){
        if (user.getRole()==Role.ADMIN){
            return true;
        }
        if (user.getRole()==Role.CLIENT){
            return ticket.getCreatedBy()!=null && ticket.getCreatedBy().getId().equals(user.getId());
        }
        if (user.getRole()==Role.AGENT){
            return ticket.getAssignedTo()!=null && ticket.getAssignedTo().getId().equals(user.getId());
        }
        return false;
    }
    private void validateTicketAccess(User user,Ticket ticket){
        if (!canAccessTicket(user,ticket)){
            throw new ResourceNotFoundException("Ticket with id "+ticket.getId()+" not found");
        }
    }
    private void createStatusHistory(Ticket ticket, TicketStatus oldStatus, TicketStatus newStatus, User changedBy) {
        TicketHistory history = new TicketHistory();
        history.setTicket(ticket);
        history.setActionType(TicketHistoryActionType.STATUS_CHANGED);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(changedBy);
        history.setChangedAt(LocalDateTime.now());

        ticketHistoryRepository.save(history);
    }
    private void createAssignmentHistory(Ticket ticket, String oldAssignedToEmail, String newAssignedToEmail, User changedBy) {
        TicketHistory history = new TicketHistory();
        history.setTicket(ticket);
        history.setActionType(TicketHistoryActionType.ASSIGNED);
        history.setOldAssignedToEmail(oldAssignedToEmail);
        history.setNewAssignedToEmail(newAssignedToEmail);
        history.setChangedBy(changedBy);
        history.setChangedAt(LocalDateTime.now());

        ticketHistoryRepository.save(history);
    }
    public List<TicketHistoryResponse> getTicketHistory(long ticketId, Authentication authentication){
        Ticket ticket=findTicketEntityById(ticketId);
        User user=getCurrentUser(authentication);
        validateTicketAccess(user,ticket);

        List<TicketHistory> historyList= ticketHistoryRepository.findByTicketIdOrderByChangedAtAsc(ticketId);
        List<TicketHistoryResponse> responses=new ArrayList<>();

        for (TicketHistory ticketHistory:historyList){
            responses.add(mapHistoryToResponse(ticketHistory));
        }
        return responses;
    }
    private TicketHistoryResponse mapHistoryToResponse(TicketHistory history) {
        TicketHistoryResponse response = new TicketHistoryResponse();
        response.setId(history.getId());
        response.setActionType(history.getActionType());
        response.setOldStatus(history.getOldStatus());
        response.setNewStatus(history.getNewStatus());
        response.setOldAssignedToEmail(history.getOldAssignedToEmail());
        response.setNewAssignedToEmail(history.getNewAssignedToEmail());
        response.setChangedAt(history.getChangedAt());

        if (history.getChangedBy() != null) {
            response.setChangedByEmail(history.getChangedBy().getEmail());
            response.setChangedByFullName(history.getChangedBy().getFullName());
        }

        return response;
    }
    public TicketStatsResponse getTicketStats(Authentication authentication){
        User user=getCurrentUser(authentication);
        TicketStatsResponse response=new TicketStatsResponse();
        List<Ticket>tickets=getVisibleTicketsForStats(user);
        response.setTotalTickets(tickets.size());

        for (Ticket ticket : tickets) {
            if (ticket.getStatus() == TicketStatus.OPEN) {
                response.setOpenTickets(response.getOpenTickets() + 1);
            } else if (ticket.getStatus() == TicketStatus.IN_PROGRESS) {
                response.setInProgressTickets(response.getInProgressTickets() + 1);
            } else if (ticket.getStatus() == TicketStatus.RESOLVED) {
                response.setResolvedTickets(response.getResolvedTickets() + 1);
            } else if (ticket.getStatus() == TicketStatus.CLOSED) {
                response.setClosedTickets(response.getClosedTickets() + 1);
            }
            if (ticket.getPriority() == TicketPriority.HIGH) {
                response.setHighPriorityTickets(response.getHighPriorityTickets() + 1);
            } else if (ticket.getPriority() == TicketPriority.MEDIUM) {
                response.setMediumPriorityTickets(response.getMediumPriorityTickets() + 1);
            } else if (ticket.getPriority() == TicketPriority.LOW) {
                response.setLowPriorityTickets(response.getLowPriorityTickets() + 1);
            }
            if (ticket.getAssignedTo() != null) {
                response.setAssignedTickets(response.getAssignedTickets() + 1);
            } else {
                response.setUnassignedTickets(response.getUnassignedTickets() + 1);
            }
        }
        return response;
    }
    private List<Ticket> getVisibleTicketsForStats(User user){
        if (user.getRole()==Role.ADMIN){
            return ticketRepository.findAll();
        }
        if (user.getRole()==Role.AGENT){
            return ticketRepository.findByAssignedTo(user);
        }
        else{
            return ticketRepository.findByCreatedBy(user);
        }
    }
    public TicketResponse reopenTicket(long id,Authentication authentication){
        Ticket ticket = findTicketEntityById(id);
        User user=getCurrentUser(authentication);

        if (user.getRole()!=Role.ADMIN && user.getRole()!=Role.CLIENT){
            throw new InvalidUserRoleException("Only client or admin can reopen tickets");
        }
        if (user.getRole()==Role.CLIENT){
            validateTicketAccess(user,ticket);
        }
        if (ticket.getStatus()!=TicketStatus.CLOSED){
            throw new InvalidTicketStateException("Only closed tickets can be reopened");
        }

        TicketStatus oldStatus=ticket.getStatus();
        ticket.setStatus(TicketStatus.OPEN);

        Ticket updatedTicket=ticketRepository.save(ticket);
        reopenHistoryChange(updatedTicket,oldStatus,TicketStatus.OPEN,user);

        return mapTicketToResponse(updatedTicket);
    }
    private void reopenHistoryChange(Ticket ticket, TicketStatus oldStatus, TicketStatus newStatus, User changedBy){
        TicketHistory ticketHistory=new TicketHistory();

        ticketHistory.setTicket(ticket);
        ticketHistory.setActionType(TicketHistoryActionType.REOPENED);
        ticketHistory.setChangedBy(changedBy);
        ticketHistory.setOldStatus(oldStatus);
        ticketHistory.setNewStatus(newStatus);
        ticketHistory.setChangedAt(LocalDateTime.now());
        ticketHistoryRepository.save(ticketHistory);
    }
    public TicketResponse unassignTicket(long id,Authentication authentication){
        Ticket ticket=findTicketEntityById(id);
        User curr_user=getCurrentUser(authentication);

        if (curr_user.getRole()!=Role.ADMIN){
            throw new InvalidUserRoleException("Non admin user cant unassign a ticket");
        }
        if (ticket.getAssignedTo()==null){
            throw new InvalidTicketStateException("This ticket is already unassigned");
        }
        String oldAssignedToEmail=ticket.getAssignedTo().getEmail();
        ticket.setAssignedTo(null);

        Ticket updatedTicket=ticketRepository.save(ticket);
        createUnassignedHistory(updatedTicket,oldAssignedToEmail,curr_user);

        return mapTicketToResponse(updatedTicket);
    }
    public void createUnassignedHistory(Ticket updatedTicket,String oldAssignedToEmail,User user){
        TicketHistory ticketHistory=new TicketHistory();

        ticketHistory.setTicket(updatedTicket);
        ticketHistory.setChangedAt(LocalDateTime.now());
        ticketHistory.setActionType(TicketHistoryActionType.UNASSIGNED);
        ticketHistory.setOldAssignedToEmail(oldAssignedToEmail);
        ticketHistory.setChangedBy(user);
        ticketHistory.setNewAssignedToEmail(null);

        ticketHistoryRepository.save(ticketHistory);
    }
    public DashboardSummaryResponse getDashboardSummary(Authentication authentication){
        User user=getCurrentUser(authentication);
        List<Ticket> tickets=getVisibleTicketsForStats(user);

        DashboardStatsResponse statsResponse=new DashboardStatsResponse();
        DashboardPriorityResponse priorityResponse=new DashboardPriorityResponse();
        DashboardAssignmentResonse assignmentResponse=new DashboardAssignmentResonse();

        statsResponse.setTotalTickets(tickets.size());
        for (Ticket ticket:tickets){
            if (ticket.getStatus()==TicketStatus.OPEN){
                statsResponse.setOpenTickets(statsResponse.getOpenTickets()+1);
            } else if (ticket.getStatus()==TicketStatus.IN_PROGRESS){
                statsResponse.setInProgressTickets(statsResponse.getInProgressTickets()+1);
            } else if (ticket.getStatus()==TicketStatus.RESOLVED){
                statsResponse.setResolvedTickets(statsResponse.getResolvedTickets()+1);
            }else {
                statsResponse.setClosedTickets(statsResponse.getClosedTickets()+1);
            }
            if (ticket.getPriority() == TicketPriority.HIGH) {
                priorityResponse.setHighPriorityTickets(priorityResponse.getHighPriorityTickets() + 1);
            } else if (ticket.getPriority() == TicketPriority.MEDIUM) {
                priorityResponse.setMediumPriorityTickets(priorityResponse.getMediumPriorityTickets() + 1);
            } else if (ticket.getPriority() == TicketPriority.LOW) {
                priorityResponse.setLowPriorityTickets(priorityResponse.getLowPriorityTickets() + 1);
            }

            if (ticket.getAssignedTo() != null) {
                assignmentResponse.setAssignedTickets(assignmentResponse.getAssignedTickets() + 1);
            } else {
                assignmentResponse.setUnassignedTickets(assignmentResponse.getUnassignedTickets() + 1);
            }
        }
        List<TicketResponse> recentTickets = getRecentTicketsForDashboard(user);
        List<TicketResponse> recentlyUpdatedTickets = getRecentlyUpdatedTicketsForDashboard(user);

        DashboardSummaryResponse response =new DashboardSummaryResponse();
        response.setAssignmentDashboard(assignmentResponse);
        response.setPriorityResponse(priorityResponse);
        response.setStatsResponse(statsResponse);
        response.setRecentTickets(recentTickets);
        response.setRecentlyUpdatedTickets(recentlyUpdatedTickets);

        List<AgentWorkloadResponse>agentWorkload=getAgenWorkload(user);
        response.setAgentWorkload(agentWorkload);


        return response;
    }
    private List<AgentWorkloadResponse> getAgenWorkload(User curr_user){
        List<AgentWorkloadResponse> result=new ArrayList<>();
        if (curr_user.getRole()==Role.CLIENT){
            return result;
        }
        List<User>users=userRepository.findAll();
        for(User user:users){
            if (user.getRole()!=Role.AGENT){
                continue;
            }
            if (curr_user.getRole()==Role.AGENT && !curr_user.getId().equals(user.getId())){
                continue;
            }
            List<Ticket> assignedTickets=ticketRepository.findByAssignedTo(user);
            AgentWorkloadResponse agentWorkload=new AgentWorkloadResponse();
            agentWorkload.setAgentEmail(user.getEmail());
            agentWorkload.setAgentId(user.getId());
            agentWorkload.setAgentName(user.getFullName());
            agentWorkload.setAssignedTickets(assignedTickets.size());

            for (Ticket ticket:assignedTickets){
                if(ticket.getStatus()==TicketStatus.OPEN){
                    agentWorkload.setOpenedTickets(agentWorkload.getOpenedTickets()+1);
                } else if (ticket.getStatus()==TicketStatus.IN_PROGRESS){
                    agentWorkload.setInProgressTickets(agentWorkload.getInProgressTickets()+1);
                } else if (ticket.getStatus()==TicketStatus.RESOLVED){
                    agentWorkload.setResolvedTickets(agentWorkload.getResolvedTickets()+1);
                } else if (ticket.getStatus() == TicketStatus.CLOSED) {
                    agentWorkload.setClosedTickets(agentWorkload.getClosedTickets() + 1);}
                if (ticket.getPriority() == TicketPriority.HIGH) {
                    agentWorkload.setHighPriorityTickets(agentWorkload.getHighPriorityTickets() + 1);
                }
            }
            result.add(agentWorkload);
        }
        return result;
    }
    private List<TicketResponse> getRecentTicketsForDashboard(User user){
        List<Ticket> tickets;
        if (user.getRole()==Role.ADMIN){
            tickets=ticketRepository.findTop5ByOrderByCreatedAtDesc();
        }
        if (user.getRole()==Role.AGENT){
            tickets=ticketRepository.findTop5ByAssignedToOrderByCreatedAtDesc(user);
        }
        else
            tickets=ticketRepository.findTop5ByCreatedByOrderByCreatedAtDesc(user);
        List<TicketResponse> responses=new ArrayList<>();
        for (Ticket ticket:tickets){
            responses.add(mapTicketToResponse(ticket));
        }
        return responses;
    }
    private List<TicketResponse> getRecentlyUpdatedTicketsForDashboard(User user) {
        List<Ticket> tickets;
        if (user.getRole() == Role.ADMIN) {
            tickets = ticketRepository.findTop5ByOrderByUpdatedAtDesc();
        } else if (user.getRole() == Role.AGENT) {
            tickets = ticketRepository.findTop5ByAssignedToOrderByUpdatedAtDesc(user);
        } else {
            tickets = ticketRepository.findTop5ByCreatedByOrderByUpdatedAtDesc(user);
        }
        List<TicketResponse> responses = new ArrayList<>();
        for (Ticket ticket : tickets) {
            responses.add(mapTicketToResponse(ticket));
        }

        return responses;
    }

}