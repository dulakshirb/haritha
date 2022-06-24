package com.haritha.haritha_farmer;

public class BiogasSell {
    public int cylinders;
    public String listed_date;

    public BiogasSell(){}

    public BiogasSell(int cylinders, String listed_date) {
        this.cylinders = cylinders;
        this.listed_date = listed_date;
    }

    public int getCylinders() {
        return cylinders;
    }

    public String getListed_date() {
        return listed_date;
    }
}
