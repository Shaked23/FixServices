package com.example.fixservices.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class MyLocation implements Parcelable{
    private String Latitude;
    private String Longitude;

    public MyLocation(String x , String y)
    {
        Latitude = x;
        Longitude = y ;

    }

    protected MyLocation(Parcel in) {
        Latitude = in.readString();
        Longitude = in.readString();
    }

    public static final Parcelable.Creator<MyLocation> CREATOR = new Parcelable.Creator<MyLocation>() {
        @Override
        public MyLocation createFromParcel(Parcel in) {
            return new MyLocation(in);
        }

        @Override
        public MyLocation[] newArray(int size) {
            return new MyLocation[size];
        }
    };

    public String get_Latitude ()
    {
        return Latitude;
    }
    public String get_Longitude ()
    {
        return Longitude;
    }
    public void set_Latitude(String x)
    {
        Latitude = x;
    }
    public void set_Longitude(String y)
    {
        Longitude = y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Latitude);
        parcel.writeString(Longitude);
    }
}