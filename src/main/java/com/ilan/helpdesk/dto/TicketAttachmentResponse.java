package com.ilan.helpdesk.dto;

import com.ilan.helpdesk.enums.AttachmentFileCategory;

import java.time.LocalDateTime;

public class TicketAttachmentResponse {
    private Long id;
    private String originalFileName;
    private String contentType;
    private long size;
    private LocalDateTime createdAt;
    private String uploadedByEmail;
    private boolean previewable;
    private AttachmentFileCategory attachmentFileCategory;



    public TicketAttachmentResponse(){};

    public AttachmentFileCategory getAttachmentFileCategory() {
        return attachmentFileCategory;
    }

    public boolean isPreviewable() {
        return previewable;
    }

    public void setAttachmentFileCategory(AttachmentFileCategory attachmentFileCategory) {
        this.attachmentFileCategory = attachmentFileCategory;
    }

    public void setPreviewable(boolean previewable) {
        this.previewable = previewable;
    }

    public String getContentType() {
        return contentType;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getSize() {
        return size;
    }

    public Long getId() {
        return id;
    }

    public String getUploadedByEmail() {
        return uploadedByEmail;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUploadedByEmail(String uploadedByEmail) {
        this.uploadedByEmail = uploadedByEmail;
    }
}
