package com.exgerm.register;

/**
 * Created by mausv on 2/19/2016.
 */
public class PendingReport {
    private int id;
    private String token;
    private String state;
    private String device_comment;
    private String users_id;
    private String user_name;
    private String lowBattery;
    private String changeBattery;
    private String lowLiquid;
    private String changeLiquid;
    private String physicalDamage;
    private String physicalRepair;
    private String trayClean;
    private String machineClean;
    private String hospitals_id;
    private String hospital_name;

    public String getTrayClean() {
        return trayClean;
    }

    public void setTrayClean(String trayClean) {
        this.trayClean = trayClean;
    }

    public String getMachineClean() {
        return machineClean;
    }

    public void setMachineClean(String machineClean) {
        this.machineClean = machineClean;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }

    public String getLowBattery() {
        return lowBattery;
    }

    public void setLowBattery(String lowBattery) {
        this.lowBattery = lowBattery;
    }

    public String getChangeBattery() {
        return changeBattery;
    }

    public void setChangeBattery(String changeBattery) {
        this.changeBattery = changeBattery;
    }

    public String getLowLiquid() {
        return lowLiquid;
    }

    public void setLowLiquid(String lowLiquid) {
        this.lowLiquid = lowLiquid;
    }

    public String getChangeLiquid() {
        return changeLiquid;
    }

    public void setChangeLiquid(String changeLiquid) {
        this.changeLiquid = changeLiquid;
    }

    public String getPhysicalDamage() {
        return physicalDamage;
    }

    public void setPhysicalDamage(String physicalDamage) {
        this.physicalDamage = physicalDamage;
    }

    public String getPhysicalRepair() {
        return physicalRepair;
    }

    public void setPhysicalRepair(String physicalRepair) {
        this.physicalRepair = physicalRepair;
    }

    public String getHospitals_id() {
        return hospitals_id;
    }

    public void setHospitals_id(String hospitals_id) {
        this.hospitals_id = hospitals_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setId (int id){
        this.id = id;
    }

    public void setToken (String token) {
        this.token = token;
    }

    public int getId () {
        return this.id;
    }

    public String getToken() {
        return this.token;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDevice_comment() {
        return device_comment;
    }

    public void setDevice_comment(String device_comment) {
        this.device_comment = device_comment;
    }

    public String getUsers_id() {
        return users_id;
    }

    public void setUsers_id(String users_id) {
        this.users_id = users_id;
    }
}