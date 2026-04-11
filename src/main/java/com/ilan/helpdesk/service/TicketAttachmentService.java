package com.ilan.helpdesk.service;


import ch.qos.logback.core.util.StringUtil;
import com.ilan.helpdesk.dto.TicketAttachmentResponse;
import com.ilan.helpdesk.enums.AttachmentFileCategory;
import com.ilan.helpdesk.enums.NotificationType;
import com.ilan.helpdesk.exception.AttachmentPreviewNotAllowedException;
import com.ilan.helpdesk.exception.InvalidAttachmentException;
import com.ilan.helpdesk.exception.ResourceNotFoundException;
import com.ilan.helpdesk.model.Ticket;
import com.ilan.helpdesk.model.TicketAttachment;
import com.ilan.helpdesk.model.User;
import com.ilan.helpdesk.repository.TicketAttachmentRepository;
import com.ilan.helpdesk.repository.TicketRepository;
import com.ilan.helpdesk.repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class TicketAttachmentService {
    private final TicketAttachmentRepository ticketAttachmentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final Path uploadPath;
    private final TicketService ticketService;
    private static final Set<String>ALLOWED_EXTENSIONS=Set.of(".png",".jpg",".jpeg","pdf",".txt");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/png", "image/jpeg", "application/pdf", "text/plain");

    private void validateAttachmentFile(MultipartFile file){
        if (file==null || file.isEmpty()){
            throw new InvalidAttachmentException("file is empty");
        }
        String originalFileName=file.getOriginalFilename();
        if (!StringUtils.hasText(originalFileName)){
            throw new InvalidAttachmentException("file name is missing");
        }
        String cleanFileName=StringUtils.cleanPath(originalFileName);
        if (cleanFileName.contains("..")){
            throw new InvalidAttachmentException("invalid file name");
        }
        String extension="";
        int dotIndex=cleanFileName.lastIndexOf(".");
        if (dotIndex!=-1){
            extension=cleanFileName.substring(dotIndex).toLowerCase();
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidAttachmentException("file extension is not allowed");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidAttachmentException("file type is not allowed");
        }
    }
    public TicketAttachmentService(TicketAttachmentRepository ticketAttachmentRepository, TicketRepository ticketRepository,
                                   TicketService ticketService, UserRepository userRepository,NotificationService notificationService,
                                   @Value("${file.upload-dir}") String uploadDir) throws IOException{
        this.ticketAttachmentRepository=ticketAttachmentRepository;
        this.ticketRepository=ticketRepository;
        this.ticketService=ticketService;
        this.userRepository=userRepository;
        this.notificationService=notificationService;
        this.uploadPath= Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadPath);
    }
    public TicketAttachmentResponse uploadAttachment(Long ticket_id, MultipartFile file, Authentication authentication)throws IOException{
        Ticket ticket=findTicket(ticket_id);
        User user=getCurrentUser(authentication);

        ticketService.validateTicketAccess(user,ticket);
        validateAttachmentFile(file);

        if (file.isEmpty()){
            throw new IllegalArgumentException("file is empty");
        }
        String originalFileName = file.getOriginalFilename();
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String storedFileName = UUID.randomUUID() + extension;
        Path targetLocation = uploadPath.resolve(storedFileName);

        Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);

        TicketAttachment attachment=new TicketAttachment();
        attachment.setCreatedBy(user);
        attachment.setTicket(ticket);
        attachment.setSize(file.getSize());
        attachment.setContentType(file.getContentType());
        attachment.setOriginalFileName(originalFileName);
        attachment.setStoredFileName(storedFileName);

        TicketAttachment saved=ticketAttachmentRepository.save(attachment);
        createAttachmentNotification(ticket,user);
        return mapToResponse(saved);
    }
    @Transactional(readOnly = true)
    public List<TicketAttachmentResponse> getAttachmentsByTicketId(Long ticketId, Authentication authentication){
        User user=getCurrentUser(authentication);
        Ticket ticket=findTicket(ticketId);

        ticketService.validateTicketAccess(user,ticket);

        List<TicketAttachment> attachments = ticketAttachmentRepository.findAllByTicketIdWithCreatedBy(ticketId);
        List<TicketAttachmentResponse>responses=new ArrayList<>();

        for (TicketAttachment attachment:attachments){
            responses.add(mapToResponse(attachment));
        }

        return responses;
    }
    @Transactional(readOnly = true)
    public Resource DownloadAttachment(long attachmentId,Authentication authentication)throws MalformedURLException{
        TicketAttachment attachment=ticketAttachmentRepository.findById(attachmentId).orElseThrow(()->new ResourceNotFoundException(
                "attachment with id: "+attachmentId+" not found"));
        User user=getCurrentUser(authentication);
        ticketService.validateTicketAccess(user,attachment.getTicket());

        Path filePath=uploadPath.resolve(attachment.getStoredFileName()).normalize();
        Resource resource=new UrlResource(filePath.toUri());

        if (!resource.exists()){
            throw new ResourceNotFoundException("File not found on the disk");
        }
        return resource;
    }
    public TicketAttachment getAttachmentById(long id){
        return ticketAttachmentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("attachment with id: "+id+" not found"));
    }
    private Ticket findTicket(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + ticketId + " not found"));
    }
    private User getCurrentUser(Authentication authentication){
        return userRepository.findByEmail(authentication.getName()).orElseThrow(()->new ResourceNotFoundException("user not found"));
    }
    private TicketAttachmentResponse mapToResponse(TicketAttachment ticketAttachment){
        TicketAttachmentResponse response=new TicketAttachmentResponse();
        response.setId(ticketAttachment.getId());
        response.setContentType(ticketAttachment.getContentType());
        response.setSize(ticketAttachment.getSize());
        response.setCreatedAt(ticketAttachment.getCreatedAt());
        response.setOriginalFileName(ticketAttachment.getOriginalFileName());
        response.setAttachmentFileCategory(resolveFileCategory(ticketAttachment.getContentType()));
        response.setPreviewable(isPreviewable(ticketAttachment.getContentType()));

        if (ticketAttachment.getCreatedBy()!=null){
            response.setUploadedByEmail(ticketAttachment.getCreatedBy().getEmail());
        }
        return response;
    }
    public void DeleteAttachment(long attachmentId,Authentication authentication) throws IOException {
        TicketAttachment attachment = ticketAttachmentRepository.findById(attachmentId)
                .orElseThrow(()->new ResourceNotFoundException("attachment with id: "+attachmentId+" not found"));
        User user=getCurrentUser(authentication);
        boolean isAdmin=user.getRole().name().equals("ADMIN");
        boolean isUploader=attachment.getCreatedBy()!=null && attachment.getCreatedBy().getId()== user.getId();

        if (!isAdmin && !isUploader){
            throw new ResourceNotFoundException("attachment with id: "+attachmentId+" not found");
        }
        Path filePath=uploadPath.resolve(attachment.getStoredFileName()).normalize();
        ticketAttachmentRepository.delete(attachment);
        Files.deleteIfExists(filePath);
    }
    private void createAttachmentNotification(Ticket ticket,User uploader){
        if (ticket.getCreatedBy()!=null && !ticket.getCreatedBy().getId().equals(uploader.getId())){
            notificationService.createNotification(ticket.getCreatedBy(),"a new attachment was added to ticket: "+ticket.getTitle()
                    , NotificationType.ATTACHMENT_ADDED,ticket.getId());
        }
        if (ticket.getAssignedTo()!=null && !ticket.getAssignedTo().getId().equals(uploader.getId())){
            notificationService.createNotification(ticket.getAssignedTo(),"a new attachment was added to ticket: "+ticket.getTitle()
                    ,NotificationType.ATTACHMENT_ADDED,ticket.getId());
        }
    }
    private AttachmentFileCategory resolveFileCategory(String contentType){
        if (contentType==null){
            return AttachmentFileCategory.OTHER;
        }
        String normalized=contentType.toLowerCase();
        if (normalized.equals("image/png")||normalized.equals("image/jpeg")){
            return AttachmentFileCategory.IMAGE;
        }
        if (normalized.equals("application/pdf")){
            return AttachmentFileCategory.PDF;
        }
        if (normalized.equals("text/plain")){
            return AttachmentFileCategory.TEXT;
        }
        return AttachmentFileCategory.OTHER;
    };
    private boolean isPreviewable(String contentType){
        AttachmentFileCategory category=resolveFileCategory(contentType);
        return category==AttachmentFileCategory.TEXT || category==AttachmentFileCategory.IMAGE ||
                category==AttachmentFileCategory.PDF;
    }
    @Transactional(readOnly = true)
    public Resource previewAttachment(Long attachmentId, Authentication authentication) throws MalformedURLException {
        TicketAttachment attachment = ticketAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "attachment with id: " + attachmentId + " not found"
                ));

        User user = getCurrentUser(authentication);
        ticketService.validateTicketAccess(user, attachment.getTicket());

        if (!isPreviewable(attachment.getContentType())) {
            throw new AttachmentPreviewNotAllowedException("This file cannot be previewed");
        }

        Path filePath = uploadPath.resolve(attachment.getStoredFileName()).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new ResourceNotFoundException("File not found on the disk");
        }

        return resource;
    }


}
