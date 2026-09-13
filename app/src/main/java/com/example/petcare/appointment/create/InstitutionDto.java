package com.example.petcare.appointment.create;

import com.google.gson.annotations.SerializedName;

public class InstitutionDto {
    @SerializedName("institutionID")
    private String InstitutionID;
    private String name;
    private String TypeName;
    private String StatusName;
    private String Address;
    private String Phone;

    public String getPhone() {
        return Phone;
    }

    public String getAddress() {
        return Address;
    }

    public String getStatusName() {
        return StatusName;
    }

    public String getTypeName() {
        return TypeName;
    }

    public String getName() {
        return name;
    }

    public String getInstitutionID() {
        return InstitutionID;
    }

    @Override
    public String toString() {
        return name; // 這裡回傳的一定要是你用來顯示的變數 (例如 name)
    }
}
