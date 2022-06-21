package com.haritha.haritha_farmer;

public class InventoryFertilizer {
    private String in_fertilizer_id;
    private String in_fertilizer_name;
    private String in_fertilizer_date;

    private Float in_fertilizer_amount;
    private Float in_fertilizer_cost;

    public InventoryFertilizer(){

    }

    public InventoryFertilizer(String in_fertilizer_id, String in_fertilizer_name, String in_fertilizer_date, Float in_fertilizer_amount, Float in_fertilizer_cost){


        this.in_fertilizer_id = in_fertilizer_id;
        this.in_fertilizer_name = in_fertilizer_name;
        this.in_fertilizer_date = in_fertilizer_date;
        this.in_fertilizer_amount = in_fertilizer_amount;
        this.in_fertilizer_cost = in_fertilizer_cost;
    }

    public String getIn_fertilizer_id(){return in_fertilizer_id;}
    public void setIn_fertilizer_id(String in_fertilizer_id){this.in_fertilizer_id = in_fertilizer_id;}

    public String getIn_fertilizer_name(){return in_fertilizer_name;}
    public void setIn_fertilizer_name(String in_fertilizer_name){this.in_fertilizer_name = in_fertilizer_name;}

    public String getIn_fertilizer_date(){return in_fertilizer_date;}
    public void setIn_fertilizer_date(String in_fertilizer_date){this.in_fertilizer_date = in_fertilizer_date;}



    public Float getIn_fertilizer_amount(){return in_fertilizer_amount;}
    public void setIn_fertilizer_amount(Float in_fertilizer_amount){this.in_fertilizer_amount = in_fertilizer_amount;}

    public Float getIn_fertilizer_cost(){return in_fertilizer_cost;}
    public void setIn_fertilizer_cost(Float in_fertilizer_cost) {
        this.in_fertilizer_cost = in_fertilizer_cost;
    }
}


