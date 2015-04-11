package com.example.mapdemo;

import com.google.android.gms.maps.model.LatLng;

 class NamedLocation {
    public final String name;
    public final LatLng location;
    public final String formatted_phone_number;
    public final String address;
    public final String office_image_url;
    public final String distance;

    NamedLocation(String name, LatLng location, String formatted_phone_number, String address, String office_image_url, String distance) {
        this.name = name;
        this.location = location;
        this.formatted_phone_number = formatted_phone_number;
        this.address = address;
        this.office_image_url = office_image_url;
        this.distance = distance;
    }
}