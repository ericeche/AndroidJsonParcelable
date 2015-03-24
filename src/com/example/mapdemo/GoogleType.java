	/** Implement this class from "Parcelable"
	* So that you can pass this class Object to another using Intents
	* Otherwise you can't pass to another actitivy
	* write custom code for marshaling and unmarshaling so it creates 
	* less garbage objects in comparison to Serialization.
	* 
	* Serialization is a marker interface, 
	* which implies the user cannot marshal the data according to their requirements. 
	* In Serialization, a marshaling operation is performed on a Java Virtual Machine (JVM) 
	* using the Java reflection API. 
	* This helps identify the Java objects member and behavior,
	*  but also ends up creating a lot of garbage objects. 
	*  Due to this, the Serialization process is slow in comparison to Parcelable
	* */
package com.example.mapdemo;

 import android.os.Parcel;  
 import android.os.Parcelable;  
 
 public class GoogleType implements Parcelable {

		
	 private String strValue; 
	 private Integer intValue;  
     
	 /** * standard getter * * @return strValue */ 

	 public String getStrValue() { return strValue; }  

	 /** * Standard setter * * @param strValue */

	 public void setStrValue(String strValue) { this.strValue = strValue; } 

	 /** * standard getter * * @return */

	 public Integer getIntValue() { return intValue; }   

	 /** * Standard setter * * @param intValue */
	 public void setIntValue(Integer intValue) { this.intValue = intValue; } 

	 
	 /** * Standard basic constructor for non-parcel * object creation */

	 public GoogleType() { ; };  
	  //* * @param in a parcel from which to read this object */ 

	   
    protected GoogleType(Parcel in) {
        strValue = in.readString();
        intValue = in.readByte() == 0x00 ? null : in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strValue);
        if (intValue == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(intValue);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GoogleType> CREATOR = new Parcelable.Creator<GoogleType>() {
        @Override
        public GoogleType createFromParcel(Parcel in) {
            return new GoogleType(in);
        }

        @Override
        public GoogleType[] newArray(int size) {
            return new GoogleType[size];
        }
    };
}