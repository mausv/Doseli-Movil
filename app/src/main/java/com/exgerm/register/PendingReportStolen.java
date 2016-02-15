package com.exgerm.register;

/**
 * Created by mausv on 2/15/2016.
 */
public class PendingReportStolen {
    private int id;
    private String token;
    private String deleted_by;

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

    public String getDeleted_by() {
        return deleted_by;
    }

    public void setDeleted_by(String deleted_by) {
        this.deleted_by = deleted_by;
    }
}
