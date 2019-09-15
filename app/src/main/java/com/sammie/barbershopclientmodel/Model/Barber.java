package com.sammie.barbershopclientmodel.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Barber implements Parcelable {

    private String name,password,username,barberId;
    private long rating;

    public Barber() {
    }

    protected Barber(Parcel in) {
        name = in.readString();
        password = in.readString();
        username = in.readString();
        barberId = in.readString();
        rating = in.readLong();
    }

    public static final Creator<Barber> CREATOR = new Creator<Barber>() {
        @Override
        public Barber createFromParcel(Parcel in) {
            return new Barber(in);
        }

        @Override
        public Barber[] newArray(int size) {
            return new Barber[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getBarberId() {
        return barberId;
    }

    public void setBarberId(String barberId) {
        this.barberId = barberId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(password);
        dest.writeString(username);
        dest.writeString(barberId);
        dest.writeLong(rating);
    }
}
