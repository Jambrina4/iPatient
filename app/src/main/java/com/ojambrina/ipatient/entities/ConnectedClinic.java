package com.ojambrina.ipatient.entities;

import java.io.Serializable;

public class ConnectedClinic implements Serializable {

    private String name;
    private String image;

    public ConnectedClinic() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
