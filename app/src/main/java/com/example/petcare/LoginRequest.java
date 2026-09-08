package com.example.petcare;

public class LoginRequest {
    private String account;
    private String password;

    public LoginRequest(String account, String password) {
        this.account = account;
        this.password = password;
    }
}