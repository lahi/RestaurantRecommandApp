package com.test.recommand.service;

/**
 * Created by sooyoungbyun on 2014. 6. 9..
 */

public final class Constants {

    // Defines a custom Intent action
    public static final String BROADCAST_STATUS_UPDATE = "com.restaurant.twitter.STATUS_UPDATE";
    public static final String BROADCAST_LOCATION_UPDATE = "com.restaurant.twitter.LOCATION_UPDATE";

    // intent data
    public static final String DATA_RESTAURANT_TWEET_LIST = "com.restaurant.twitter.EXTRA_DATA";

    public static final String DATA_UPDATE_CURRENT_LONGITUDE = "com.update.location.longitude";
    public static final String DATA_UPDATE_CURRENT_LATITUDE = "com.update.location.latitude";
    public static final String DATA_UPDATE_CURRENT_LOCALITY = "com.update.location.locality";

    // The background thread is doing logging
    public static final int STATE_LOG = -1;
}
