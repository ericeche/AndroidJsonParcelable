package com.example.mapdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Experiment implements Parcelable {
private String experimentName = "";
private String name = "";
private String address = "";
private String address2 = "";
private String city = "";
private String state = "";
private String country = "";
private String zip = "";
private String phone = "";
private String fax = "";
private String clat = "";
private String clon = "";
private String office_image_url = "";
private HashMap<String, ArrayList<Double>> measurements = new HashMap<String, ArrayList<Double>>();
private HashMap<String, ArrayList<MyClass>> ObjectsList = new HashMap<String, ArrayList<MyClass>>();

//every Experiment needs a name to make it unique
public Experiment(String experimentName) {
   this.experimentName = experimentName;
}

/**
* a method which adds values to a measurement
* 
* @param measurementName
*            the name of the measurement you want to write a value to
* @param measurement
*            the value of the measurement you want to add
* @return the sequence from the fasta
*/
public void addValueToMeasurements(String measurementName,
       double measurement) {
   // try to add the measurement
   if (measurements.get(measurementName) == null) {
       // Measurements m = new Measurements(measurementName);
       ArrayList<Double> _data = new ArrayList<Double>();
       // m.addMeasurement(measurement);
       _data.add(measurement);
       measurements.put(measurementName, _data);
   }
   // if the measurement does not exist, create it
   else {
       measurements.get(measurementName).add(measurement);
   }
}

/**
* a method which returns the experiment name
* 
* @return the experiment name
*/
public String getExperimentName() {
   return experimentName;
}

//This makes the object parselable, I don't understand why
/**
* a constructor for the parcelable variant of the object
*/
public Experiment(Parcel in) {
 //  measurements = new HashMap<String, ArrayList<Double>>();
	ObjectsList = new HashMap<String, ArrayList<MyClass>>();
   readFromParcel(in);
}

@Override
public int describeContents() {
   // TODO Auto-generated method stub
   return 0;
}

@Override
/**
* a method which writes the objects to a parcel
*/
public void writeToParcel(Parcel dest, int flags) {
   // dest.writeString(experimentName);
   // for(String s: (Set<String>)measurements.keySet()){
   // dest.writeString(s);
   // dest.writeValue(measurements.get(s));
   // }
   Bundle b = new Bundle();
   b.putString("name",name);
   b.putString("address",address);
   b.putString("addryress2",address2);
   b.putString("city",city);
   b.putString("state",state);
   b.putString("country",country);
   b.putString("zip_postal_code",zip);
   b.putString("phone",phone);
   b.putString("fax",fax);
   b.putString("latitude",clat);
   b.putString("longitude",clon);
   b.putString("office_image_url",office_image_url);
   // write hash
   b.putSerializable("object", ObjectsList);
   dest.writeBundle(b);
}

/**
* a method which reads from a parcel
*/
public void readFromParcel(Parcel in) {
   // experimentName = in.readString();
   Bundle b = in.readBundle();
   experimentName = b.getString("experimentName");
   name = b.getString("name");
   address = b.getString("address");
   address2 = b.getString("addryress2");
   city = b.getString("city");
   state = b.getString("state");
   country = b.getString("country");
   zip = b.getString("zip_postal_code");
   phone = b.getString("phone");
   fax= b.getString("fax");
   clat = b.getString("latitude");
   clon = b.getString("longitude");
   office_image_url = b.getString("office_image_url");
   ObjectsList = (HashMap<String, ArrayList<MyClass>>) b.getSerializable("ObjectsList");
}

/**
* generic parcelable creator method
*/
public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
   public Experiment createFromParcel(Parcel in) {
       return new Experiment(in);
   }

   public Experiment[] newArray(int size) {
       return new Experiment[size];
   }
};
}
