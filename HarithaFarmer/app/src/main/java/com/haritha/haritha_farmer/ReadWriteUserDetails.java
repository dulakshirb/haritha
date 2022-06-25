package com.haritha.haritha_farmer;

public class ReadWriteUserDetails {
    public String userName, email, mobile, gender, farmName, location, district;

    public ReadWriteUserDetails(){}

    public ReadWriteUserDetails(String txtUserName, String txtEmail, String txtMobile, String txtGender, String txtFarmName, String txtLocation, String txtDistrict) {
        this.userName = txtUserName;
        this.email = txtEmail;
        this.mobile = txtMobile;
        this.gender = txtGender;
        this.farmName = txtFarmName;
        this.location = txtLocation;
        this.district = txtDistrict;
    }
}
