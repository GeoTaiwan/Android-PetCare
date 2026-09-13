package com.example.petcare.appointment;

import java.util.List;

public class InstitutionListResponse {
    private String message;
    private List<InstitutionDto> data;

    public String getMessage() {
        return message;
    }

    public List<InstitutionDto> getData() {
        return data;
    }
}