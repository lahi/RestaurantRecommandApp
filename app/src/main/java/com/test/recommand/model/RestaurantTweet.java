package com.test.recommand.model;

import java.io.Serializable;

/**
 * Created by sooyoungbyun on 2014. 6. 9..
 */
public class RestaurantTweet implements Serializable{
    private String id;
    private String text;
    private String profile_image_url;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }
}
