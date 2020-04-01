package com.sammie.barbershopclientmodel.Model;

public class User {
    String name,phoneNumber,address,nxtKin,gender,age,dOb;
    String idNumber;

    public User() {
    }

    public User(String name, String phoneNumber, String address, String nxtKin, String gender, String age, String dOb, String idNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.nxtKin = nxtKin;
        this.gender = gender;
        this.age = age;
        this.dOb = dOb;
        this.idNumber = idNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNxtKin() {
        return nxtKin;
    }

    public void setNxtKin(String nxtKin) {
        this.nxtKin = nxtKin;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getdOb() {
        return dOb;
    }

    public void setdOb(String dOb) {
        this.dOb = dOb;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
}
