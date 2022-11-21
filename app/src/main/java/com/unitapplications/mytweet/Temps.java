package com.unitapplications.mytweet;

public class Temps {
    int id;
    String name,templ;

    public Temps() { }

    public Temps(String name, String templ) {
        this.name = name;
        this.templ = templ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTempl() {
        return templ;
    }

    public void setTempl(String templ) {
        this.templ = templ;
    }
}