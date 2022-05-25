package com.haritha.haritha_farmer;

import java.sql.Date;

public class Crops {
    private String cropID, cropType, cropName, variety, botanicalName, plantedDate, startMethod, lightProfile, plantingDetails, harvestUnit;
    private Integer daysToEmerge, daysToMaturity, harvestWindow;
    private Float plantSpacing, rowSpacing, plantingDepth, estimatedRevenue, expectedYield;

    public Crops() {
    }

    public Crops(String cropID, String cropType, String cropName, String variety, String botanicalName, String plantedDate, String startMethod, String lightProfile, String plantingDetails, String harvestUnit, Integer daysToEmerge, Integer daysToMaturity, Integer harvestWindow, Float plantSpacing, Float rowSpacing, Float plantingDepth, Float estimatedRevenue, Float expectedYield) {
        this.cropID = cropID;
        this.cropType = cropType;
        this.cropName = cropName;
        this.variety = variety;
        this.botanicalName = botanicalName;
        this.plantedDate = plantedDate;
        this.startMethod = startMethod;
        this.lightProfile = lightProfile;
        this.plantingDetails = plantingDetails;
        this.harvestUnit = harvestUnit;
        this.daysToEmerge = daysToEmerge;
        this.daysToMaturity = daysToMaturity;
        this.harvestWindow = harvestWindow;
        this.plantSpacing = plantSpacing;
        this.rowSpacing = rowSpacing;
        this.plantingDepth = plantingDepth;
        this.estimatedRevenue = estimatedRevenue;
        this.expectedYield = expectedYield;
    }

    public String getCropID() {
        return cropID;
    }

    public void setCropID(String cropID) {
        this.cropID = cropID;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getBotanicalName() {
        return botanicalName;
    }

    public void setBotanicalName(String botanicalName) {
        this.botanicalName = botanicalName;
    }

    public String getPlantedDate() {
        return plantedDate;
    }

    public void setPlantedDate(String plantedDate) {
        this.plantedDate = plantedDate;
    }

    public String getStartMethod() {
        return startMethod;
    }

    public void setStartMethod(String startMethod) {
        this.startMethod = startMethod;
    }

    public String getLightProfile() {
        return lightProfile;
    }

    public void setLightProfile(String lightProfile) {
        this.lightProfile = lightProfile;
    }

    public String getPlantingDetails() {
        return plantingDetails;
    }

    public void setPlantingDetails(String plantingDetails) {
        this.plantingDetails = plantingDetails;
    }

    public String getHarvestUnit() {
        return harvestUnit;
    }

    public void setHarvestUnit(String harvestUnit) {
        this.harvestUnit = harvestUnit;
    }

    public Integer getDaysToEmerge() {
        return daysToEmerge;
    }

    public void setDaysToEmerge(Integer daysToEmerge) {
        this.daysToEmerge = daysToEmerge;
    }

    public Integer getDaysToMaturity() {
        return daysToMaturity;
    }

    public void setDaysToMaturity(Integer daysToMaturity) {
        this.daysToMaturity = daysToMaturity;
    }

    public Integer getHarvestWindow() {
        return harvestWindow;
    }

    public void setHarvestWindow(Integer harvestWindow) {
        this.harvestWindow = harvestWindow;
    }

    public Float getPlantSpacing() {
        return plantSpacing;
    }

    public void setPlantSpacing(Float plantSpacing) {
        this.plantSpacing = plantSpacing;
    }

    public Float getRowSpacing() {
        return rowSpacing;
    }

    public void setRowSpacing(Float rowSpacing) {
        this.rowSpacing = rowSpacing;
    }

    public Float getPlantingDepth() {
        return plantingDepth;
    }

    public void setPlantingDepth(Float plantingDepth) {
        this.plantingDepth = plantingDepth;
    }

    public Float getEstimatedRevenue() {
        return estimatedRevenue;
    }

    public void setEstimatedRevenue(Float estimatedRevenue) {
        this.estimatedRevenue = estimatedRevenue;
    }

    public Float getExpectedYield() {
        return expectedYield;
    }

    public void setExpectedYield(Float expectedYield) {
        this.expectedYield = expectedYield;
    }
}

