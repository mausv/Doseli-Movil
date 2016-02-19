package com.exgerm.register;

/**
 * Created by mausv on 2/19/2016.
 */
public class PendingRegisterHandset {
    private int id;
    private String model;
    private String serial_number;
    private String associated_by;
    private String token;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getAssociated_by() {
        return associated_by;
    }

    public void setAssociated_by(String associated_by) {
        this.associated_by = associated_by;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}