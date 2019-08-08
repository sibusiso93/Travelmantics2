package com.example.travelmantics;

import java.io.Serializable;

public class Deals implements Serializable
{
    private String id;
    private String Title;
    private String Description;
    private String Price;
    private String Imageurl;
    private String name;


    public Deals()
    {

    }


    public Deals( String title, String description, String price, String imageurl, String name)
    {

        Title = title;
        Description = description;
        Price = price;
        Imageurl = imageurl;
        this.name = name;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getImageurl() {
        return Imageurl;
    }

    public void setImageurl(String imageurl) {
        Imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
