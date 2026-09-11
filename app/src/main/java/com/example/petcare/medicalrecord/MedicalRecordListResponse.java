package com.example.petcare.medicalrecord;

import java.util.List;

public class MedicalRecordListResponse {
    private String message;
    private List<MedicalRecordDto> data;

    public String getMessage() { return message; }
    public List<MedicalRecordDto> getData() { return data; }
}