package com.example.petcare.medicalrecord;

public class MedicalRecordDto {
    private int recordID;
    private String visitDate;
    private String vetID;
    private String symptoms;
    private String diagnosis;
    private String testResultsFile;
    private String vetName;

    private String petName;




    public int getRecordID() { return recordID; }
    public String getVisitDate() { return visitDate; }
    public String getVetID() { return vetID; }
    public String getSymptoms() { return symptoms; }
    public String getDiagnosis() { return diagnosis; }
    public String getTestResultsFile() { return testResultsFile; }
    public String getVetName() { return vetName; }
    public String getPetName() { return petName; }
}