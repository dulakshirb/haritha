package com.haritha.harithaagriofficer;

public class Farm {
    private String farm_id;
    private String farmName;
    private String userName;
    private String district;
    private String gender;
    private String location;
    private String email;
    private String mobile;

    public Farm(){}

    public Farm(String farm_id, String farm_name, String owner_name, String district, String gender, String location, String email, String phone) {
        this.farm_id = farm_id;
        this.farmName = farm_name;
        this.userName = owner_name;
        this.district = district;
        this.gender = gender;
        this.location = location;
        this.email = email;
        this.mobile = phone;
    }

    public String getFarm_id() {
        return farm_id;
    }

    public String getFarmName() {
        return farmName;
    }

    public String getUserName() {
        return userName;
    }

    public String getDistrict() {
        return district;
    }

    public String getGender() {
        return gender;
    }

    public String getLocation() {
        return location;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }
}
