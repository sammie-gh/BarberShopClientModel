package com.sammie.barbershopclientmodel.Model;

import com.google.firebase.Timestamp;

public class BookingInformation {
  private String cityBook,customerName,customerPhone,time,barberId,
          barberName,salonId,salonName,salonAddress,customer_id,gender,uid,isConfirm; //can chatge to booking status
  private Long slot;
  private com.google.firebase.Timestamp timestamp;
  private boolean done;



    public BookingInformation() {
    }

  public BookingInformation(String cityBook, String customerName, String customerPhone, String time, String barberId, String barberName, String salonId, String salonName, String salonAddress, String customer_id, String gender, Long slot, Timestamp timestamp, boolean done) {
    this.cityBook = cityBook;
    this.customerName = customerName;
    this.customerPhone = customerPhone;
    this.time = time;
    this.barberId = barberId;
    this.barberName = barberName;
    this.salonId = salonId;
    this.salonName = salonName;
    this.salonAddress = salonAddress;
    this.customer_id = customer_id;
    this.gender = gender;
    this.slot = slot;
    this.timestamp = timestamp;
    this.done = done;
  }

  public String getCityBook() {
    return cityBook;
  }

  public void setCityBook(String cityBook) {
    this.cityBook = cityBook;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getCustomerPhone() {
    return customerPhone;
  }

  public void setCustomerPhone(String customerPhone) {
    this.customerPhone = customerPhone;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getBarberId() {
    return barberId;
  }

  public void setBarberId(String barberId) {
    this.barberId = barberId;
  }

  public String getBarberName() {
    return barberName;
  }

  public void setBarberName(String barberName) {
    this.barberName = barberName;
  }

  public String getSalonId() {
    return salonId;
  }

  public void setSalonId(String salonId) {
    this.salonId = salonId;
  }

  public String getSalonName() {
    return salonName;
  }

  public void setSalonName(String salonName) {
    this.salonName = salonName;
  }

  public String getSalonAddress() {
    return salonAddress;
  }

  public void setSalonAddress(String salonAddress) {
    this.salonAddress = salonAddress;
  }

  public String getCustomer_id() {
    return customer_id;
  }

  public void setCustomer_id(String customer_id) {
    this.customer_id = customer_id;
  }

  public Long getSlot() {
    return slot;
  }

  public void setSlot(Long slot) {
    this.slot = slot;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getIsConfirm() {
    return isConfirm;
  }

  public void setIsConfirm(String isConfirm) {
    this.isConfirm = isConfirm;
  }
}
