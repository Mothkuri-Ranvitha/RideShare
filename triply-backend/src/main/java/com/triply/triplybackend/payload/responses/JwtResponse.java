package com.triply.triplybackend.payload.responses;

public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String role;
    private String name;

    public JwtResponse(String token, String type, Long id, String email, String role, String name) {
        this.token = token;
        this.type = type;
        this.id = id;
        this.email = email;
        this.role = role;
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
