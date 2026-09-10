package com.example.petcare.petcreate;
public class PetCreateDto {
    private String name;
    private String species;
    private String breed;
    private String microchipNo;

    // 為了方便建立物件，我們寫一個建構子
    public PetCreateDto(String name, String species, String breed, String microchipNo) {
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.microchipNo = microchipNo;
    }
}