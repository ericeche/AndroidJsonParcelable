package com.example.mapdemo;

import java.io.Serializable;

import com.google.api.client.util.Key;

/** Implement this class from "Serializable"
* So that you can pass this class Object to another using Intents
* Otherwise you can't pass to another actitivy
* */
public class Place implements Serializable {

	@Key
	public String id;
	
	@Key
	public String name;
	
	@Key
	public String address;
	
	@Key
	public String address2;
	
	@Key
	public String city;
	
	@Key
	public String state;
	
	@Key
	public String country;

	@Key
	public String zip_postal_code;
	
	@Key
	public String fax;
	
	@Key
	public String office_image_url;
	
	@Key
	public String reference;
	
	@Key
	public String icon;
	
	@Key
	public String vicinity;
	
	@Key
	public Geometry geometry;
	
	@Key
	public Location location;
	
	@Key
	public String formatted_address;
	
	@Key
	public String formatted_phone_number;

	public Place(String name2, String address3, String address22, String city2,
			String state2, String zip, String phone, String fax2, String clat,
			String clon, String office_image_url2,String vicinity) {
		// TODO Auto-generated constructor stub
		this.name = name2;
		this.address = address3;
		this.address2 = address22;
		this.city = city2;
		this.state = state2;
		this.zip_postal_code = zip;
		this.formatted_phone_number = phone;
		this.fax = fax2;
		this.location = new Location(new Double(clat),new Double(clon));
		this.office_image_url = office_image_url2;
		this.vicinity = vicinity;

	}

	@Override
	public String toString() {
		return name + " - " + id + " - " + reference;
	}
	
	public  class Geometry implements Serializable
	{
		@Key
		public Location location;
		
	}
	
	public  class Location implements Serializable
	{
		public Location(double parseDouble, double parseDouble2) {
			// TODO Auto-generated constructor stub
			this.lat = parseDouble;
			this.lng = parseDouble2;
		}

		@Key
		public double lat;
		
		@Key
		public double lng;
		
	}
	
}
