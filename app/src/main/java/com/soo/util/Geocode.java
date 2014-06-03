package com.soo.util;

import android.widget.ListView;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by sooyoungbyun on 2014. 6. 3..
 */
@Root
public class Geocode {

    @Element
    private String status;

    @Element
    private GeocodeResult result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GeocodeResult getResult() {
        return result;
    }

    public void setResult(GeocodeResult result) {
        this.result = result;
    }

    @Element (name = "result")
    public class GeocodeResult {

        @Element
        private String type;

        @Element
        private String formatted_address;

        @ElementList(entry = "address_component", inline = true)
        private List<AddressComponent> addressComponent;

        @Element
        private GeoMetry geoMetry;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFormatted_address() {
            return formatted_address;
        }

        public void setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;
        }

        public List<AddressComponent> getAddressComponent() {
            return addressComponent;
        }

        public void setAddressComponent(List<AddressComponent> addressComponent) {
            this.addressComponent = addressComponent;
        }

        public GeoMetry getGeoMetry() {
            return geoMetry;
        }

        public void setGeoMetry(GeoMetry geoMetry) {
            this.geoMetry = geoMetry;
        }
    }

    @Element (name = "address_component")
    public class AddressComponent {
        @Element
        private String long_name;

        @Element
        private String short_name;

        @Element
        private String type;

        public String getLong_name() {
            return long_name;
        }

        public void setShort_name(String short_name) {
            this.short_name = short_name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @Element (name = "geometry")
    public class GeoMetry {
        @Element
        private Loc location;

        @Element
        private  String location_type;

        @Element
        private  ViewPort viewport;

        public String getLocation_type() {
            return location_type;
        }

        public void setLocation_type(String location_type) {
            this.location_type = location_type;
        }

        public Loc getLocation() {
            return location;
        }

        public void setLocation(Loc location) {
            this.location = location;
        }

        public ViewPort getViewport() {
            return viewport;
        }

        public void setViewport(ViewPort viewport) {
            this.viewport = viewport;
        }
    }

    @Element (name = "location")
    public class Loc {

        @Element
        private  String lat;

        @Element
        private  String lng;

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
    }

    @Element (name = "viewport")
    public class ViewPort {

        @Element
        private Southwest southwest;

        @Element
        private Northwest northwest;

        public Southwest getSouthwest() {
            return southwest;
        }

        public void setSouthwest(Southwest southwest) {
            this.southwest = southwest;
        }

        public Northwest getNorthwest() {
            return northwest;
        }

        public void setNorthwest(Northwest northwest) {
            this.northwest = northwest;
        }
    }

    @Element (name = "southwest")
    public class Southwest {
        @Element
        private  String lat;

        @Element
        private  String lng;

        public String getLat() {
            return lat;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getLng() {
            return lng;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
    }

    @Element (name = "northeast")
    public class Northwest {

        @Element
        private  String lat;

        @Element
        private  String lng;

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }
    }
}
