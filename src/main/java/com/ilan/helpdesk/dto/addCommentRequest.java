package com.ilan.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class addCommentRequest {
    @NotBlank (message = "content is required")
    @Size (min = 2,max = 300, message = "content must be between 2 to 300 chars")
    private String content;

    @NotBlank(message = "Author name is required")
    @Size(min = 2, max = 50, message = "Author name must be between 2 and 50 characters")
    private String authorName;

    public addCommentRequest(){
    }
    public addCommentRequest(String authorName,String content){
        this.authorName=authorName;
        this.content=content;
    }
    public String getAuthorName() {
        return authorName;
    }
    public String getContent() {
        return content;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
