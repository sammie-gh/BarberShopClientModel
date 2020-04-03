package com.sammie.barbershopclientmodel.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sammie.barbershopclientmodel.Common.Common;
import com.sammie.barbershopclientmodel.Model.BookingInformation;
import com.sammie.barbershopclientmodel.Model.FCMResponse;
import com.sammie.barbershopclientmodel.Model.FCMSendData;
import com.sammie.barbershopclientmodel.Model.MyNotification;
import com.sammie.barbershopclientmodel.Model.MyToken;
import com.sammie.barbershopclientmodel.PaymentActivity;
import com.sammie.barbershopclientmodel.R;
import com.sammie.barbershopclientmodel.Retrofit.IFCMApi;
import com.sammie.barbershopclientmodel.Retrofit.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class BookingStep4Fragment extends Fragment {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private SweetAlertDialog dialog;
    private SimpleDateFormat simpleDateFormat;
    private LocalBroadcastManager localBroadcastManager;
    Activity activity;
    @BindView(R.id.txt_booking_barber_text)
    TextView txt_booking_barber_text;
    Unbinder unbinder;
    @BindView(R.id.txt_booking_time_text)
    TextView txt_booking_time_text;
    @BindView(R.id.txt_salona_ddress)
    TextView txt_salon_address;
    @BindView(R.id.txt_salon_name)
    TextView txt_salon_name;
    @BindView(R.id.txt_salon_open_hours)
    TextView txt_salon_open_hours;
    @BindView(R.id.txt_salon_phone)
    TextView txt_salon_phone;
    @BindView(R.id.txt_salon_website)
    TextView txt_salon_website;

    private IFCMApi ifcmApi;


    @OnClick(R.id.btn_confirm)
    void confirmBooking() {
        requestPay();
//        confirmBookingMethod();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof Activity) {
            activity = (Activity) context;
        }

    }

    private void requestPay() {
        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        startActivity(intent);
        getActivity().finish();


        //        new AlertDialog.Builder(getActivity())
//                .setTitle("Payment Notice !")
//                .setMessage("Please to continue booking please make the following payment of ...GH₵")
//                .setCancelable(false)
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int i) {
//                        PayWithSlydepay.Pay(getActivity(), "Payment for room",
//                                3,
//                                "Payment made for booking " + Common.currentUser.getName() + " tel: " + Common.currentUser.getPhoneNumber(),
//                                Common.currentUser.getName(),
//                                "", "121", "", RequestCode.IMPORT);
//                        dialog.dismiss();
//
//                    }
//                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialog.dismiss();
//            }
//        }).show();
    }


    private void confirmBookingMethod() {
        //create booking information
        //process Timestamp
        //Timestamp to filter all booking  within date is greater today
        //or only display all future booking
        dialog.show();
        String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
        String[] convertTime = startTime.split("-");  //split ex :9:00 -10:00
        //get startime :get 9:00
        String[] startTimeConvert = convertTime[0].split(":");
        int startHourInt = Integer.parseInt(startTimeConvert[0].trim()); // we get 9
        int startMinInt = Integer.parseInt(startTimeConvert[1].trim()); // we get 00

        Calendar bookingDateWithhourHouse = Calendar.getInstance();
        bookingDateWithhourHouse.setTimeInMillis(Common.bookingDate.getTimeInMillis());
        bookingDateWithhourHouse.set(Calendar.HOUR_OF_DAY, startHourInt);
        bookingDateWithhourHouse.set(Calendar.MINUTE, startMinInt);

        //create timestamp object and apply to BookingInformation
        Timestamp timestamp = new Timestamp(bookingDateWithhourHouse.getTime());
        final BookingInformation bookingInformation = new BookingInformation();
        bookingInformation.setCityBook(Common.city);
        bookingInformation.setTimestamp(timestamp);
        bookingInformation.setDone(false);   // always fals coz we will use this  field to filter display
        bookingInformation.setBarberId(Common.currentBarber.getBarberId());
        bookingInformation.setBarberName(Common.currentBarber.getName());
        bookingInformation.setCustomerName(Common.currentUser.getName());
        bookingInformation.setCustomerPhone(Common.currentUser.getPhoneNumber());
        bookingInformation.setSalonAddress(Common.currentSalon.getAddress());
        bookingInformation.setSalonId(Common.currentSalon.getSalonId());
        bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));
        bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append(" on ")
                .append(simpleDateFormat.format(bookingDateWithhourHouse.getTime())).toString());
        bookingInformation.setCustomer_id(Common.currentUser.getIdNumber());
        bookingInformation.setCustomer_id(Common.currentUser.getGender());

        //submit to babrber documment
        DocumentReference bookingDate = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.city)
                .collection("Branch")
                .document(Common.currentSalon.getSalonId())
                .collection("Barbers")
                .document(Common.currentBarber.getBarberId())
                .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                .document(String.valueOf(Common.currentTimeSlot));

        //write data
        bookingDate.set(bookingInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //function to check if booking exist we preevent nw booking
                        addToUserBooking(bookingInformation);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.success(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToUserBooking(final BookingInformation bookingInformation) {

        //First create new collection
        final CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        //Check if exist document in this collection

        //Get current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Timestamp todayTimeStamp = new Timestamp(calendar.getTime());
        userBooking
                .whereGreaterThanOrEqualTo("timestamp", todayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.getResult().isEmpty()) {
                            //setData
                            userBooking.document()
                                    .set(bookingInformation)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Create Notification
                                            //AllSalon/Ho/Branch/IW9io42puOugPB5do2rj/Barbers/H1fXpNpzFt0FH28mUpTr/29_03_2020/19
                                            //AllSalon/Ho/Branch/IW9io42puOugPB5do2rj/Barbers/H1fXpNpzFt0FH28mUpTr/Notifications/082c5034-0846-42e3-926c-7dc85cdb70a1
                                            MyNotification myNotification = new MyNotification();
                                            myNotification.setUid(UUID.randomUUID().toString());
                                            myNotification.setTitle("New Booking");
                                            myNotification.setContent("You have a new appointment with " + Common.currentUser.getName());
                                            myNotification.setRead(false); //filter notification with read is false on click
                                            myNotification.setServerTimeStamp(FieldValue.serverTimestamp());


                                            //submit Notification to Notification collection of Barber
                                            FirebaseFirestore.getInstance()
                                                    .collection("AllSalon")
                                                    .document(Common.city)
                                                    .collection("Branch")
                                                    .document(Common.currentSalon.getSalonId())
                                                    .collection("Barbers")
                                                    .document(Common.currentBarber.getBarberId())
                                                    .collection("Notifications") //if not avail create auto
                                                    .document(myNotification.getUid()) //create unique key
                                                    .set(myNotification)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //get token base on babrberid or doctor
                                                            FirebaseFirestore.getInstance().collection("Tokens")
                                                                    .whereEqualTo("uid", Common.currentBarber.getUsername())//userPhone == my uid change with phone later
                                                                    .limit(1)
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful() && task.getResult().size() > 0) {
                                                                                MyToken myToken = new MyToken();
                                                                                for (DocumentSnapshot tokenSnap : task.getResult())
                                                                                    myToken = tokenSnap.toObject(MyToken.class);

                                                                                //create data to send
                                                                                FCMSendData sendRequest = new FCMSendData();
                                                                                Map<String, String> dataSend = new HashMap<>();
                                                                                dataSend.put(Common.TITLE_KEY, "New Booking");
                                                                                dataSend.put(Common.CONTENT_TYPE, "You have a new booking from " + Common.currentUser.getName()); // use fmuath to save name later

                                                                                sendRequest.setTo(myToken.getToken());
                                                                                sendRequest.setData(dataSend);

                                                                                compositeDisposable.add(ifcmApi.sendNotification(sendRequest)
                                                                                        .subscribeOn(Schedulers.io())
                                                                                        .observeOn(AndroidSchedulers.mainThread())
                                                                                        .subscribe(new Consumer<FCMResponse>() {
                                                                                            @Override
                                                                                            public void accept(FCMResponse fcmResponse) throws Exception {
                                                                                                if (dialog.isShowing())
                                                                                                    dialog.dismiss();

                                                                                                addToCalender(Common.bookingDate, Common.convertTimeSlotToString(Common.currentTimeSlot));
                                                                                                resetStaticData();
                                                                                                getActivity().finish(); //close activity
                                                                                                Toasty.success(getContext(), "Success!", Toast.LENGTH_SHORT).show();


                                                                                            }
                                                                                        }, new Consumer<Throwable>() {
                                                                                            @Override
                                                                                            public void accept(Throwable throwable) throws Exception {
                                                                                                Log.d("NOTIFICATION_ERROR", throwable.getMessage());
                                                                                                addToCalender(Common.bookingDate, Common.convertTimeSlotToString(Common.currentTimeSlot));
                                                                                                resetStaticData();
                                                                                                getActivity().finish(); //close activity
                                                                                                Toasty.success(getContext(), "Success!", Toast.LENGTH_SHORT).show();

                                                                                                if (dialog.isShowing())
                                                                                                    dialog.dismiss();
                                                                                            }
                                                                                        }));


                                                                            }

                                                                        }
                                                                    });

                                                        }
                                                    });


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if (dialog.isShowing())
                                                dialog.dismiss();
                                            Toasty.error(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            if (dialog.isShowing())
                                dialog.dismiss();

                            resetStaticData();
                            getActivity().finish(); //close actvi
                            Toasty.success(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addToCalender(Calendar bookingDate, String startDate) {

        String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
        String[] convertTime = startTime.split("-");  //split ex :9:00 -10:00
        //get startime :get 9:00
        String[] startTimeConvert = convertTime[0].split(":");
        int startHourInt = Integer.parseInt(startTimeConvert[0].trim()); // we get 9
        int startMinInt = Integer.parseInt(startTimeConvert[1].trim()); // we get 00

        String[] endTimeConvert = convertTime[1].split(":");
        int endHourInt = Integer.parseInt(endTimeConvert[0].trim()); // we get 10
        int endMinInt = Integer.parseInt(endTimeConvert[1].trim()); // we get 00

        Calendar startEvent = Calendar.getInstance();
        startEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        startEvent.set(Calendar.HOUR_OF_DAY, startHourInt); //set event start hour
        startEvent.set(Calendar.MINUTE, startMinInt);  // set event start min

        Calendar endEvent = Calendar.getInstance();
        endEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        endEvent.set(Calendar.HOUR_OF_DAY, endHourInt); //set event start hour
        endEvent.set(Calendar.MINUTE, endMinInt);  // set event start min

        //aftr==  we have startevent and end## convert it to fortmat string
        SimpleDateFormat calendarFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String startEventTime = calendarFormat.format(startEvent.getTime());
        String endEventTime = calendarFormat.format(endEvent.getTime());

        addToDeviceCalendar(startEventTime, endEventTime, "Appointment with Doctor " + Common.currentBarber.getName(),
                new StringBuilder("from") //haircut
                        .append(startTime)
                        .append(" with")
                        .append(" at ")
                        .append(Common.currentSalon.getName()).toString(), // emergency
                new StringBuilder("Address: ").append(Common.currentSalon.getAddress()).toString());

    }

    private void addToDeviceCalendar(String startEventTime, String endEventTime, String title, String description, String location) {
        SimpleDateFormat calendarFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        try {
            Date start = calendarFormat.parse(startEventTime);
            Date end = calendarFormat.parse(endEventTime);

            ContentValues event = new ContentValues();
            //put
            event.put(CalendarContract.Events.CALENDAR_ID, getCalendar(getContext()));
            event.put(CalendarContract.Events.TITLE, title);
            event.put(CalendarContract.Events.DESCRIPTION, description);
            event.put(CalendarContract.Events.EVENT_LOCATION, location);

            //Time
            event.put(CalendarContract.Events.DTSTART, start.getTime());
            event.put(CalendarContract.Events.DTEND, end.getTime());
            event.put(CalendarContract.Events.ALL_DAY, 0);
            event.put(CalendarContract.Events.HAS_ALARM, 1);

            String timeZone = TimeZone.getDefault().getID();
            event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);

            Uri calendars;
            if (Build.VERSION.SDK_INT >= 8)
                calendars = Uri.parse("content://com.android.calendar/events");
            else
                calendars = Uri.parse("content://calendar/events");

            Uri save_uri = getActivity().getContentResolver().insert(calendars, event);
            //save to cache
            Paper.init(getActivity());
            Paper.book().write(Common.EVENT_URI_CACHE, save_uri.toString());


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private String getCalendar(Context context) {
        //get default calendar Id of calendar of gmail
        String gmailIdCalendar = "";
        String[] projection = {"_id", "calendar_displayName"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");
        ContentResolver contentResolver = context.getContentResolver();

        //select al calander
        Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);
        if (managedCursor.moveToFirst()) {
            String calName;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do {
                calName = managedCursor.getString(nameCol);
                if (calName.contains("@gmail.com")) {
                    gmailIdCalendar = managedCursor.getString(idCol);
                    break; //exist as soon we got id

                }
            } while (managedCursor.moveToNext());
            managedCursor.close();
        }

        return gmailIdCalendar;
    }

    private void resetStaticData() {
        Common.step = 0;
        Common.currentTimeSlot = -1;
        Common.currentSalon = null;
        Common.currentBarber = null;
        Common.bookingDate.add(Calendar.DATE, 0); //s
        // current date added

    }

    private BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    private void setData() {
        txt_booking_barber_text.setText(Common.currentBarber.getName());
        txt_booking_time_text.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append(" on ")
                .append(simpleDateFormat.format(Common.bookingDate.getTime())));

        txt_salon_address.setText(Common.currentSalon.getAddress());
        txt_salon_website.setText(Common.currentSalon.getWebsite());
        txt_salon_name.setText(Common.currentSalon.getName());
        txt_salon_open_hours.setText(Common.currentSalon.getOpenHours());
        txt_salon_phone.setText(Common.currentSalon.getPhone());

    }

    static BookingStep4Fragment instance;

    public static BookingStep4Fragment getInstance() {

        if (instance == null)
            instance = new BookingStep4Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ifcmApi = RetrofitClient.getInstance().create(IFCMApi.class);

        //apply format for date display confirm
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReceiver, new IntentFilter(Common.KEY_CONFIRM_BOOKING));

        dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Booking please wait");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_booking_step_four, container, false);

        unbinder = ButterKnife.bind(this, itemView);
        return itemView;

    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);
        compositeDisposable.clear();
        super.onDestroy();
    }


}
