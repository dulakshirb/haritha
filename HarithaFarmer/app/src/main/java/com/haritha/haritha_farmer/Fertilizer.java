package com.haritha.haritha_farmer;

public class Fertilizer {
    private String fertilizer_id;
    private String fertilizer_name;
    private String fertilizer_description;
    private String fertilizer_crops_touse;
    private Float fertilizer_amount;
    private Float fertilizer_restock_amount;

    public Fertilizer(){

    }

    public Fertilizer(String fertilizer_id, String fertilizer_name, String fertilizer_description, String fertilizer_crops_touse, Float fertilizer_amount, Float fertilizer_restock_amount){

        this.fertilizer_id = fertilizer_id;
        this.fertilizer_name = fertilizer_name;
        this.fertilizer_description = fertilizer_description;
        this.fertilizer_crops_touse = fertilizer_crops_touse;
        this.fertilizer_amount = fertilizer_amount;
        this.fertilizer_restock_amount = fertilizer_restock_amount;
    }

    public String getFertilizer_id(){return fertilizer_id;}
    public void setFertilizer_id(String fertilizerId){this.fertilizer_id = fertilizerId;}

    public String getFertilizer_name(){return fertilizer_name;}
    public void setFertilizer_name(String fertilizerName){this.fertilizer_name = fertilizer_name;}

    public String getFertilizer_description(){return fertilizer_description;}
    public void setFertilizer_description(String fertilizerDescription){this.fertilizer_description = fertilizer_description;}

    public String getFertilizer_crops_touse(){return fertilizer_crops_touse;}
    public void setFertilizer_crops_touse(String cropsToUse){this.fertilizer_crops_touse = fertilizer_crops_touse;}

    public Float getFertilizer_amount(){return fertilizer_amount;}
    public void setFertilizer_amount(Float fertilizer_amount){this.fertilizer_amount = fertilizer_amount;}

    public Float getFertilizer_restock_amount(){return fertilizer_restock_amount;}
    public void setFertilizer_restock_amount(Float fertilizer_restock_amount) {
        this.fertilizer_restock_amount = fertilizer_restock_amount;
    }
}
