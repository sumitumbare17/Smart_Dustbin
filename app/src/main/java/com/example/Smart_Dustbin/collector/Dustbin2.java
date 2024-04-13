package com.example.Smart_Dustbin.collector;
// Dustbin2.java
import android.os.Parcel;
import android.os.Parcelable;

public class Dustbin2 implements Parcelable {
    private String id;
    private String location;

    public Dustbin2(String id, String location) {
        this.id = id;
        this.location = location;
    }

    protected Dustbin2(Parcel in) {
        id = in.readString();
        location = in.readString();
    }

    public static final Creator<Dustbin2> CREATOR = new Creator<Dustbin2>() {
        @Override
        public Dustbin2 createFromParcel(Parcel in) {
            return new Dustbin2(in);
        }

        @Override
        public Dustbin2[] newArray(int size) {
            return new Dustbin2[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(location);
    }
}
