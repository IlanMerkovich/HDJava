package com.ilan.helpdesk.controller;


import com.ilan.helpdesk.dto.TicketAttachmentResponse;
import com.ilan.helpdesk.model.TicketAttachment;
import com.ilan.helpdesk.service.TicketAttachmentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@RestController
public class TicketAttachmentController {
    private final TicketAttachmentService attachmentService;
    public TicketAttachmentController(TicketAttachmentService ticketAttachmentService) {
        this.attachmentService = ticketAttachmentService;
    }

    @PostMapping(value = "/api/tickets/{ticketId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('CLIENT','AGENT','ADMIN')")
    public TicketAttachmentResponse uploadAttachment(@PathVariable Long ticketId, @RequestParam("file")MultipartFile file, Authentication authentication)
        throws Exception {
        return attachmentService.uploadAttachment(ticketId,file,authentication);
    }

    @GetMapping("/api/tickets/{ticketId}/attachments")
    @PreAuthorize("hasAnyRole('CLIENT','AGENT','ADMIN')")
    public List<TicketAttachmentResponse> getAttachment(@PathVariable Long ticketId, Authentication authentication) {
        return attachmentService.getAttachmentsByTicketId(ticketId, authentication);
    }

    @GetMapping("/api/attachments/{attachmentId}/download")
    @PreAuthorize("hasAnyRole('CLIENT','AGENT','ADMIN')")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId, Authentication authentication) throws MalformedURLException {
        Resource resource = attachmentService.DownloadAttachment(attachmentId, authentication);
        TicketAttachment attachment = attachmentService.getAttachmentById(attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        attachment.getContentType() != null ? attachment.getContentType() : "application/octet-stream"
                ))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getOriginalFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/api/attachments/{attachmentId}")
    @PreAuthorize("hasAnyRole('CLIENT','AGENT','ADMIN')")
    public void deleteAttachment(@PathVariable Long attachmentId, Authentication authentication) throws Exception {
        attachmentService.DeleteAttachment(attachmentId, authentication);
    }
    @GetMapping("/api/attachments/{attachmentId}/preview")
    @PreAuthorize("hasAnyRole('CLIENT','AGENT','ADMIN')")
    public ResponseEntity<Resource> previewAttachment(@PathVariable Long attachmentId,
                                                      Authentication authentication) throws Exception {
        Resource resource = attachmentService.previewAttachment(attachmentId, authentication);
        TicketAttachment attachment = attachmentService.getAttachmentById(attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        attachment.getContentType() != null ? attachment.getContentType() : "application/octet-stream"
                ))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + attachment.getOriginalFileName() + "\"")
                .body(resource);
    }
}
