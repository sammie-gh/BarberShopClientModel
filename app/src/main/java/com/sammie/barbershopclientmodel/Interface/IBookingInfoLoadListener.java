package com.sammie.barbershopclientmodel.Interface;

import com.sammie.barbershopclientmodel.Model.BookingInformation;

public interface IBookingInfoLoadListener {
    void onBookingInfoLoadEmpty();
    void onBookingInfoLoadSuccess(BookingInformation bookingInformation,String documentId);
    void onBookingInfoLoadFailed(String message);

}
