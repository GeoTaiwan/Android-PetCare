package com.example.petcare.caregroominglog;

public class CareGroomingLogDto {
    private int logID;
    private String logDate;
    private String staffID;
    private int petID;
    private String activityDetails;
    private String dietNotes;
    private String petName; // 🌟 新增

    public String getPetName() { return petName; }

    // 請補上 Getter 方法
    public int getLogID() { return logID; }
    public String getLogDate() { return logDate; }
    public String getStaffID() { return staffID; }
    public int getPetID() { return petID; }
    public String getActivityDetails() { return activityDetails; }
    public String getDietNotes() { return dietNotes; }
}