package com.example.petcare.petcreate;

import java.util.List;
import java.util.Map;

public class PetOptionsDto {
    private List<String> species;
    private List<String> genders;
    private Map<String, List<String>> breeds; // 完美的對應 C# 的 Dictionary

    public List<String> getSpecies() { return species; }
    public List<String> getGenders() { return genders; }
    public Map<String, List<String>> getBreeds() { return breeds; }
}