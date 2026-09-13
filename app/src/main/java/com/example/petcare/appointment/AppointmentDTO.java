package com.example.petcare.appointment;

public class AppointmentDTO {
    private String appointmentID;
    private String petName;
    private String institutionName;
    private String serviceType;
    private String apptDateTime;
    private String status;

    public String getAppointmentID() { return appointmentID; }
    public String getPetName() { return petName; }
    public String getInstitutionName() { return institutionName; }
    public String getServiceType() { return serviceType; }
    public String getApptDateTime() { return apptDateTime; }
    public String getStatus() { return status; }
}