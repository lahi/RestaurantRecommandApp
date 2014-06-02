package com.test.recommand.model;

import org.simpleframework.xml.Element;

/**
 * Created by sooyoungbyun on 2014. 6. 2..
 */
@Element (name = "item")
public class ItemType {

    @Element (required = false)
    private String title;

    @Element (required = false)
    private String link;

    @Element (required = false)
    private String category;

    @Element (required = false)
    private String description;

    @Element (required = false)
    private String telephone;

    @Element (required = false)
    private String address;

    @Element (required = false)
    private String roadAddress;

    @Element (required = false)
    private String mapx;

    @Element (required = false)
    private String mapy;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

    public String getMapx() {
        return mapx;
    }

    public void setMapx(String mapx) {
        this.mapx = mapx;
    }

    public String getMapy() {
        return mapy;
    }

    public void setMapy(String mapy) {
        this.mapy = mapy;
    }

}

