package com.haritha.harithaagriofficer;

public class ReadWriteUserDetails {
    public String mobile, gender, district;

    public ReadWriteUserDetails(){}

    public ReadWriteUserDetails(String txtMobile, String txtGender, String txtDistrict) {
        this.mobile = txtMobile;
        this.gender = txtGender;
        this.district = txtDistrict;
    }
}
