package com.example.slidingpuzzle;

import java.io.Serializable;

public class Picture implements Serializable  {
    private final String photo;

    public Picture(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }
}
