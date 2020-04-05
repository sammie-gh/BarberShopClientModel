package com.sammie.barbershopclientmodel.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sammie.barbershopclientmodel.Model.Barber;
import com.sammie.barbershopclientmodel.Model.BookingInformation;
import com.sammie.barbershopclientmodel.Model.MyToken;
import com.sammie.barbershopclientmodel.Model.Salon;
import com.sammie.barbershopclientmodel.Model.User;
import com.sammie.barbershopclientmodel.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.paperdb.Paper;

public class Common {
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_SALON_STORE = "SALON_SAVE";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final String KEY_BARBER_SELECTED = "BARBER_SELECTED";
    public static final int TIME_SLOT_TOTAL = 21;
    public static final Object DISABLE_TAG = "DISABLE";
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static final String EVENT_URI_CACHE = "URI_EVENT_SAVE";
    public static final String LOGGED_KEY = "UserLogged";
    public static String IS_LOGIN = "IsLogin";
    public static User currentUser;
    public static Salon currentSalon;
    public static int step = 0;
    public static String city = "";
    public static final String KEY_BARBER_LOAD_DONE = "BARBER_LOAD_DONE";
    public static final String TITLE_KEY = "title";
    public static final String CONTENT_TYPE = "content";
    public static Barber currentBarber;
    public static int currentTimeSlot = -1;
    public static Calendar bookingDate = Calendar.getInstance();
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
    public static BookingInformation currentBooking;
    public static String currentBookingId = "";

    //sldepay
    public static String MERCHANT_KEY = "1522254751831";
    // email or mobile number associated with the merchant account
    public static String EMAIL_OR_MOBILE_NUMBER = "ofori.d.evans@gmail.com";
    // callback url
    //    public static String CALLBACK_URL = "https://www.slydepay.com.gh/";
//    public static String CALLBACK_URL = "https://webhook.site/#!/25091d9b-4752-44f0-90bf-d6fde4012423/634cd837-794f-41d7-856f-425388cebfe7/1";
    public static String CALLBACK_URL = "https://webhook.site/25091d9b-4752-44f0-90bf-d6fde4012423";


    public class RequestCode {
        public static final int IMPORT = 9999;
        public static final int WRITE_PERMISSION = 101;
    }

    public static String convertTimeSlotToString(int slot) {
        switch (slot) {
            case 0:
                return "9:00-9:30";
            case 1:
                return "9:30-10:00";
            case 2:
                return "10:00-10:30";
            case 3:
                return "10:30-11:00";
            case 4:
                return "11:00-11:30";
            case 5:
                return "11:30-12:00";
            case 6:
                return "12:00-12:30";
            case 7:
                return "12:30-13:00";
            case 8:
                return "13:00-13:30";
            case 9:
                return "13:30-14:00";
            case 10:
                return "14:00-14:30";
            case 11:
                return "14:30-15:00";
            case 12:
                return "15:00-15:30";
            case 13:
                return "15:30-16:00";
            case 14:
                return "16:00-16:30";
            case 15:
                return "16:30-17:00";
            case 16:
                return "17:00-17:30";
            case 17:
                return "17:30-18:00";
            case 18:
                return "18:00-18:30";
            case 19:
                return "18:30-19:00";
            case 20:
                return "19:30-20:00";
            default:
                return "closed";

        }
    }

    public static String convertTimeStampToStringKey(Timestamp timestamp) {
        Date data = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        return simpleDateFormat.format(data);

    }

    public enum TOKEN_TYPE {
        CLIENT,
        BARBER,
        MANAGER,
    }

    public static void showNotification(Context context, int notification_id, String title, String content, Intent intent) {

        PendingIntent pendingIntent = null;
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(context,
                    notification_id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "medi_app_client_o1";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Medi App Booking Client App", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Client App");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);


        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_local_hospital_red_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_local_hospital_red_24dp));

        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        notificationManager.notify(notification_id, notification);


    }


    public static String formatShoppingItemName(String name) {

        return name.length() > 13 ? new StringBuilder(name.substring(0, 10)).append("...")
                .toString() : name;

    }

    public static void updateToken(Context context, String token) {

        FirebaseAuth s = FirebaseAuth.getInstance();

        if (s.getCurrentUser() != null) {
            MyToken myToken = new MyToken();
            myToken.setToken(token);
            myToken.setTokenType(TOKEN_TYPE.CLIENT); //cox code run from babrber staff app
            myToken.setUid(s.getUid());

            FirebaseFirestore.getInstance()
                    .collection("Tokens")
                    .document(s.getUid()) //to change to use member login change to userphone but only update token if user update profile in homeactivty or make uppdate to change profile
                    .set(myToken)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

        } else {
            Paper.init(context);
            String user = Paper.book().read(Common.LOGGED_KEY);
            if (user != null) {
                if (!TextUtils.isEmpty(user)) {
                    MyToken myToken = new MyToken();
                    myToken.setToken(token);
                    myToken.setTokenType(TOKEN_TYPE.CLIENT); //cox code run from babrber staff app
                    myToken.setUid(user);

                    FirebaseFirestore.getInstance()
                            .collection("Tokens")
                            .document(user)
                            .set(myToken)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                }
            }

        }


//        //First check if login
//        Paper.init(context);
//        String user = Paper.book().read(Common.LOGGED_KEY);
//        if (user != null) {
//            if (!TextUtils.isEmpty(user)) {
//                MyToken myToken = new MyToken();
//                myToken.setToken(token);
//                myToken.setTokenType(TOKEN_TYPE.BARBER); //cox code run from babrber staff app
//                myToken.setUser(user);
//
//                //submit on Firestore
//                FirebaseFirestore.getInstance()
//                        .collection("Tokens")
//                        .document(user)
//                        .set(myToken)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//
//                            }
//                        });
//
//
//            }
//        }
    }

}
