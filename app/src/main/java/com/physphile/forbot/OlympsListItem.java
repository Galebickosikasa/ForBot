package com.physphile.forbot;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.annotations.NotNull;

import java.io.Serializable;

//наш класс элементов, которые мы будем пушить в БД

@IgnoreExtraProperties
public class OlympsListItem implements Serializable{
    private String name;
    private String level;

    OlympsListItem(){

    }

    public OlympsListItem(String _name, String _level) {
        this.level = _level;
        this.name = _name;
    }

    public String getName() {
        return name;
    }

    String getLevel() {
        return level;
    }

    public void setName (String name) {
        this.name = name;
    }

    void setLevel(String level) {
        this.level = level;
    }

}