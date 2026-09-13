package com.example.petcare.caregroominglog;

import java.util.List;

public class CareGroomingLogListResponse {
    private String message;
    private List<CareGroomingLogDto> data;

    public String getMessage() { return message; }
    public List<CareGroomingLogDto> getData() { return data; }
}