package com.haritha.haritha_farmer;

public class Crop {
    private String userId;
    private String crop_type;
    private String crop_name;
    private String crop_variety;
    private String botanical_name;
    private String planted_date;
    private String start_method;
    private String light_profile;
    private String planting_details;
    private Integer days_to_emerge;
    private String harvest_unit;
    private Integer days_to_maturity;
    private Integer harvest_window;
    private Float plant_spacing;
    private Float row_spacing;
    private Float planting_depth;
    private Float estimated_revenue;
    private Float expected_yield;

    public Crop() {
    }

    public Crop(String userId, String crop_type, String crop_name, String crop_variety, String botanical_name, String planted_date, String start_method, String light_profile, String planting_details, Integer days_to_emerge, String harvest_unit, Integer days_to_maturity, Integer harvest_window, Float plant_spacing, Float row_spacing, Float planting_depth, Float estimated_revenue, Float expected_yield) {
        this.userId = userId;
        this.crop_type = crop_type;
        this.crop_name = crop_name;
        this.crop_variety = crop_variety;
        this.botanical_name = botanical_name;
        this.planted_date = planted_date;
        this.start_method = start_method;
        this.light_profile = light_profile;
        this.planting_details = planting_details;
        this.days_to_emerge = days_to_emerge;
        this.harvest_unit = harvest_unit;
        this.days_to_maturity = days_to_maturity;
        this.harvest_window = harvest_window;
        this.plant_spacing = plant_spacing;
        this.row_spacing = row_spacing;
        this.planting_depth = planting_depth;
        this.estimated_revenue = estimated_revenue;
        this.expected_yield = expected_yield;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCrop_type() {
        return crop_type;
    }

    public void setCrop_type(String crop_type) {
        this.crop_type = crop_type;
    }

    public String getCrop_name() {
        return crop_name;
    }

    public void setCrop_name(String crop_name) {
        this.crop_name = crop_name;
    }

    public String getCrop_variety() {
        return crop_variety;
    }

    public void setCrop_variety(String crop_variety) {
        this.crop_variety = crop_variety;
    }

    public String getBotanical_name() {
        return botanical_name;
    }

    public void setBotanical_name(String botanical_name) {
        this.botanical_name = botanical_name;
    }

    public String getPlanted_date() {
        return planted_date;
    }

    public void setPlanted_date(String planted_date) {
        this.planted_date = planted_date;
    }

    public String getStart_method() {
        return start_method;
    }

    public void setStart_method(String start_method) {
        this.start_method = start_method;
    }

    public String getLight_profile() {
        return light_profile;
    }

    public void setLight_profile(String light_profile) {
        this.light_profile = light_profile;
    }

    public String getPlanting_details() {
        return planting_details;
    }

    public void setPlanting_details(String planting_details) {
        this.planting_details = planting_details;
    }

    public Integer getDays_to_emerge() {
        return days_to_emerge;
    }

    public void setDays_to_emerge(Integer days_to_emerge) {
        this.days_to_emerge = days_to_emerge;
    }

    public String getHarvest_unit() {
        return harvest_unit;
    }

    public void setHarvest_unit(String harvest_unit) {
        this.harvest_unit = harvest_unit;
    }

    public Integer getDays_to_maturity() {
        return days_to_maturity;
    }

    public void setDays_to_maturity(Integer days_to_maturity) {
        this.days_to_maturity = days_to_maturity;
    }

    public Integer getHarvest_window() {
        return harvest_window;
    }

    public void setHarvest_window(Integer harvest_window) {
        this.harvest_window = harvest_window;
    }

    public Float getPlant_spacing() {
        return plant_spacing;
    }

    public void setPlant_spacing(Float plant_spacing) {
        this.plant_spacing = plant_spacing;
    }

    public Float getRow_spacing() {
        return row_spacing;
    }

    public void setRow_spacing(Float row_spacing) {
        this.row_spacing = row_spacing;
    }

    public Float getPlanting_depth() {
        return planting_depth;
    }

    public void setPlanting_depth(Float planting_depth) {
        this.planting_depth = planting_depth;
    }

    public Float getEstimated_revenue() {
        return estimated_revenue;
    }

    public void setEstimated_revenue(Float estimated_revenue) {
        this.estimated_revenue = estimated_revenue;
    }

    public Float getExpected_yield() {
        return expected_yield;
    }

    public void setExpected_yield(Float expected_yield) {
        this.expected_yield = expected_yield;
    }
}
