package com.exgerm.register;

/**
 * Created by mausv on 2/19/2016.
 */
public class PendingRegisterLocation {

    private int id;
    private String token;
    private String group_id;
    private String hospital_id;
    private String area_id;
    private String location_id;
    private String reference;
    private String location_set_by;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getHospital_id() {
        return hospital_id;
    }

    public void setHospital_id(String hospital_id) {
        this.hospital_id = hospital_id;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getLocation_set_by() {
        return location_set_by;
    }

    public void setLocation_set_by(String location_set_by) {
        this.location_set_by = location_set_by;
    }
}