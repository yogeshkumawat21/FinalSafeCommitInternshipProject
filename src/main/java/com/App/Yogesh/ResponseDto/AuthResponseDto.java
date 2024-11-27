package com.App.Yogesh.ResponseDto;

public class AuthResponseDto {
    public AuthResponseDto(){};
    public AuthResponseDto(String message, String token) {
        super();
        this.message = message;
        this.token = token;
    }
    public AuthResponseDto(String message) {
        super();
        this.message = message;
    }


    private String token;
 private String message;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
