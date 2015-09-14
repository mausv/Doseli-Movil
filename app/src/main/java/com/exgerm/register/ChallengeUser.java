package com.exgerm.register;

/**
 * Created by Mauricio on 9/14/2015.
 */
public class ChallengeUser {
    private int id;
    private String name;
    private int percent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setName(String name){
        this.name = name;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getName() {
        return this.name;
    }

    public int getPercent() {
        return this.percent;
    }
}
