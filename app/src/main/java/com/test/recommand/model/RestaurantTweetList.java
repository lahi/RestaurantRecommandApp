package com.test.recommand.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sooyoungbyun on 2014. 6. 10..
 */
public class RestaurantTweetList implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<RestaurantTweet> itemList;

    public RestaurantTweetList(ArrayList<RestaurantTweet> items) {
        this.itemList = items;
    }

    public ArrayList<RestaurantTweet> getRestaurantList() {
        return itemList;
    }
}