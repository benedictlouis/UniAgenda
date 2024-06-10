package com.ProyekOOP.UniAgenda_android.model;


public class LoginResponse {
    private String message;
    private String account_id;

    public LoginResponse(String message, String account_id) {
        this.message = message;
        this.account_id = account_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }
}
