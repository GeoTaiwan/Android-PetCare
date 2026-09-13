package com.example.petcare.appointment;

import java.util.List;
public class AppointmentListResponse {
    private String message;
    private List<AppointmentDTO> data;
    public List<AppointmentDTO> getData() { return data; }
}