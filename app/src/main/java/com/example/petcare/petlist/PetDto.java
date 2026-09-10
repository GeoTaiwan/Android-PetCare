package com.example.petcare.petlist;

public class PetDto {
    private int petId;
    private String name;
    private String species;
    private String breed;


    private String gender;
    private String dateOfBirth;
    private String profilePhotoUrl;

    // 請利用 Android Studio 的右鍵 Generate -> Getter，幫這些屬性產生對應的 get 方法
    public int getPetId(){
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

    public String getProfilePhotoUrl(){
        return profilePhotoUrl;
    }

    // ... 其餘 Getter 略 ...
}