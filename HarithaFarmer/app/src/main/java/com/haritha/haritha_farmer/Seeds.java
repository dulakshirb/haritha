package com.haritha.haritha_farmer;

public class Seeds {
    private String seed_id;
    private String seed_name;
    private String seed_description;
    private String seed_variety;
    private String seed_type;
    private Float seed_amount;
    private Float seed_restockAmount;

    public Seeds(String seed_id, String seed_name, String seed_description, String seed_variety, String seed_type, Float seed_amount, Float seed_restockAmount){

        this.seed_id = seed_id;
        this.seed_name = seed_name;
        this.seed_description = seed_description;
        this.seed_variety = seed_variety;
        this.seed_type = seed_type;
        this.seed_amount = seed_amount;
        this.seed_restockAmount = seed_restockAmount;
    }

    public String getSeed_id(){return seed_id;}
    public void setSeed_id(String seed_id){this.seed_id = seed_id;}

    public String getSeed_name(){return seed_name;}
    public void  setSeed_name(String seed_name){this.seed_name = seed_name;}

    public String getSeed_description(){return seed_description;}
    public  void  setSeed_description(String seed_description){this.seed_description = seed_description;}

    public String getSeed_variety(){return seed_variety;}
    public void setSeed_variety(String seed_variety){this.seed_variety = seed_variety;}

    public String getSeed_type(){return seed_type;}
    public  void  setSeed_type(String seed_type){this.seed_type = seed_type;}

    public Float getSeed_amount(){return seed_amount;}
    public void setSeed_amount(Float seed_amount){this.seed_amount = seed_amount;}

    public Float getSeed_restockAmount(){return seed_restockAmount;}
    public void setSeed_restockAmount(Float seed_restockAmount){this.seed_restockAmount = seed_restockAmount;}
}
