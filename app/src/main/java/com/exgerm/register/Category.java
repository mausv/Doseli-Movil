package com.exgerm.register;

/**
 * Created by Mauricio on 5/5/2015.
 */
public class Category {

    private int id;
    private String name;

    public Category(Category category){
        id = category.id;
        name = category.name;
    }

    public Category(int id, String name){
        this.id = id;
        this.name = name;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

}