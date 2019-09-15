package com.sammie.barbershopclientmodel.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Salon implements Parcelable {

   private String name,address,salonId,phone,website,openHours;

    public Salon() {
    }

    public Salon(String name, String address, String salonId, String phone, String website, String openHours) {
        this.name = name;
        this.address = address;
        this.salonId = salonId;
        this.phone = phone;
        this.website = website;
        this.openHours = openHours;
    }

    protected Salon(Parcel in) {
        name = in.readString();
        address = in.readString();
        salonId = in.readString();
        phone = in.readString();
        website = in.readString();
        openHours = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(salonId);
        dest.writeString(phone);
        dest.writeString(website);
        dest.writeString(openHours);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Salon> CREATOR = new Creator<Salon>() {
        @Override
        public Salon createFromParcel(Parcel in) {
            return new Salon(in);
        }

        @Override
        public Salon[] newArray(int size) {
            return new Salon[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSalonId() {
        return salonId;
    }

    public void setSalonId(String salonId) {
        this.salonId = salonId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }
}