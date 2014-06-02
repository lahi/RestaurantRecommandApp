package com.test.recommand.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by sooyoungbyun on 2014. 6. 2..
 */
@Root
public class RssType {

    @Element
    private ChannelType channel;

    @Attribute
    private String version;

    public ChannelType getChannel() {
        return channel;
    }

    public void setChannel(ChannelType channel) {
        this.channel = channel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
