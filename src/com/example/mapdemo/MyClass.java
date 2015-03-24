package com.example.mapdemo;

public class MyClass extends GlobalParcelable {
	 
	 public String name;  // "Detroit (HQ) Office"
	 public String address; // "One ePrize Drive"
	 public String address2; // ""
	 public String city;  // "Pleasant Ridge"
	 public String state;  // "MI",
	 public String zip_postal_code; // "48069",
	 public String phone; //"(877) 837-7493",
	 public String fax; // "(248) 543-3777",
	 public String latitude; // "42.475162",
	 public String longitude; // "-83.134733",
	 public String office_image; // "http://www.helloworld.com/img/locations/places/det-office.jpg"
	 
	 MyClass(String name, String address, String address2,
			 String city, String state, String zip_postal_code,
		 	 String phone, String fax, String latitude,
		 	 String longitude, String office_image){
		 this.name = name;
		 this.address = address;
		 this.address2 = address2;
		 this.city = name;
		 this.state = address;
		 this.zip_postal_code = address2;
		 this.phone = name;
		 this.fax = address;
		 this.latitude = latitude;
		 this.longitude = longitude;
		 this.office_image = office_image;
	 }

	 public String getName(){
		 return this.name;
	 }
	 
	 public void setName(String name){
		 this.name = name;
		 
	 }

	 public String getAddress(){
		 return this.address;
	 }
	 
	 public void setAddress(String address){
		 this.address = address;
		 
	 }
	 

	 public String getAddress2(){
		 return this.address2;
	 }
	 
	 public void setAddress2(String address2){
		 this.address2 = address2;
		 
	 }
	 

	 public String getZip(){
		 return this.zip_postal_code;
	 }
	 
	 public void setZip(String zip_postal_code){
		 this.zip_postal_code = zip_postal_code;
		 
	 }
	 
	 public String getPhone(){
		 return this.phone;
	 }
	 
	 public void setPhone(String phone){
		 this.phone = phone;
		 
	 }
	 
	 public String getFax(){
		 return this.fax;
	 }
	 
	 public void setFax(String fax){
		 this.fax = fax;
		 
	 }
	 
	 
	 public String getState(){
		 return this.state;
	 }
	 
	 public void setState(String state){
		 this.state = state;
		 
	 }
	 
	 
	 public String getCity(){
		 return this.city;
	 }
	 
	 public void setCity(String city){
		 this.city = city;
		 
	 }
	 	 
	 public String getLong(){
		 return this.longitude;
	 }
	 
	 public void setLong(String long_val){
		 this.longitude = long_val;
		 
	 }
	 
	 public String getLat(){
		 return this.latitude;
	 }
	 
	 public void setLat(String lat_val){
		 this.latitude = lat_val;
		 
	 }
	 
	 public String getOfficeImage(){
		 return this.office_image;
	 }
	 
	 public void setOfficeImage(String oofice_image){
		 this.office_image = office_image;
	 }
	 
	 
}

