package com.example.petcare.petdetail;

public class PetDetailDto {
    private int petId;
    private String name;
    private String species;
    private String breed;
    private String gender;
    private String dateOfBirth;
    private String microchipNo;
    private String specialNotes;
    private String profilePhotoUrl;
    private Double latestWeight; // 注意用 Double 處理 null
    private String latestWeightDate;
    private int medicalRecordCount;
    private int groomingLogCount;

    // 請用 Android Studio (右鍵 -> Generate) 自動產生所有 Getter 方法
    // ...
    public int getPetId() {
        return petId;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public String getBreed() {
        return breed;
    }

    public String getGender() {
        return gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getMicrochipNo() {
        return microchipNo;
    }

    public String getSpecialNotes() {
        return specialNotes;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public Double getLatestWeight() {
        return latestWeight;
    }

    public String getLatestWeightDate() {
        return latestWeightDate;
    }

    public int getMedicalRecordCount() {
        return medicalRecordCount;
    }

    public int getGroomingLogCount() {
        return groomingLogCount;
    }
}

