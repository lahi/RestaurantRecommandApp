package com.test.recommand.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * Created by sooyoungbyun on 2014. 6. 2..
 */
@Element (name = "channel")
public class ChannelType {

    @Element
    private String title;

    @Element
    private String link;

    @Element
    private String description;

    @Element
    private String lastBuildDate;

    @Element
    private String total;

    @Element
    private String start;

    @Element
    private String display;

    @ElementList(entry = "item", inline = true)
    private List<ItemType> itemList;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public List<ItemType> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemType> itemList) {
        this.itemList = itemList;
    }
}
