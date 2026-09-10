package com.example.petcare.petlist;

import java.util.List;

public class PetListResponse {
    private String message;
    private List<PetDto> data;

    public String getMessage() { return message; }
    public List<PetDto> getData() { return data; }
}