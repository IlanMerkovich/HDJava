package com.ilan.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddCommentRequest {
    @NotBlank (message = "content is required")
    @Size (min = 2,max = 300, message = "content must be between 2 to 300 chars")
    private String content;

    public AddCommentRequest(){
    }
    public AddCommentRequest(String content){
        this.content=content;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
