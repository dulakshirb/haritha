package com.haritha.haritha_farmer;

public class ReadWriteUserDetails {
    public String mobile, gender, farmName, location, district;

    public ReadWriteUserDetails(){}

    public ReadWriteUserDetails(String txtMobile, String txtGender, String txtFarmName, String txtLocation, String txtDistrict) {
        this.mobile = txtMobile;
        this.gender = txtGender;
        this.farmName = txtFarmName;
        this.location = txtLocation;
        this.district = txtDistrict;
    }
}
