package com.ilan.helpdesk.dto;

import java.time.LocalDateTime;

public class CommentResponse {
    private long id;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
    public CommentResponse(){

    }
    public CommentResponse(long id,String authorName,String content,LocalDateTime createdAt){
        this.id=id;
        this.authorName=authorName;
        this.content=content;
        this.createdAt=createdAt;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getId() {
        return id;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(long id) {
        this.id = id;
    }
}
