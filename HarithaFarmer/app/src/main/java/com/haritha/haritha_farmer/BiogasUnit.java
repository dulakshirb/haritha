package com.haritha.haritha_farmer;

public class BiogasUnit {
    public Double lpg;
    public Double temperature;
    public Double humidity;
    public int status;

    public BiogasUnit(){}

    public BiogasUnit(Double lpg, Double temperature, Double humidity, int status) {
        this.lpg = lpg;
        this.temperature = temperature;
        this.humidity = humidity;
        this.status = status;
    }
}
