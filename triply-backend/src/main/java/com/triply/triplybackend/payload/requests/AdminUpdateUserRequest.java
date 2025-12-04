package com.triply.triplybackend.payload.requests;

public class AdminUpdateUserRequest {

    private String name;
    private String phone;
    private String role; // ROLE_ADMIN / ROLE_DRIVER / ROLE_PASSENGER
    private Boolean blocked;
    private Boolean driverVerified;
    private String vehicleModel;
    private String licensePlate;
    private Integer capacity;

    public AdminUpdateUserRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Boolean getDriverVerified() {
        return driverVerified;
    }

    public void setDriverVerified(Boolean driverVerified) {
        this.driverVerified = driverVerified;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}


