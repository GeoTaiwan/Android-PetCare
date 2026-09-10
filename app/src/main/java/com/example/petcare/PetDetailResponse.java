package com.example.petcare;

// 同時建立一個 Response 類別來承接外層
public class PetDetailResponse {
    private String message;
    private PetDetailDto data;

    public String getMessage() { return message; }
    public PetDetailDto getData() { return data; }
}