package com.ilan.helpdesk.dto;

import java.util.List;

public class pagedTicketResponse {
    private List<TicketResponse>content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    public pagedTicketResponse(){}

    public int getPage() {
        return page;
    }
    public int getSize() {
        return size;
    }
    public int getTotalPages() {
        return totalPages;
    }
    public List<TicketResponse> getContent() {
        return content;
    }
    public long getTotalElements() {
        return totalElements;
    }
    public void setContent(List<TicketResponse> content) {
        this.content = content;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    public boolean isFirst() {
        return first;
    }
    public void setFirst(boolean first) {
        this.first = first;
    }
    public boolean isLast() {
        return last;
    }
    public void setLast(boolean last) {
        this.last = last;
    }

}
