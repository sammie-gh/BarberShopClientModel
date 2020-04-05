package com.sammie.barbershopclientmodel.Service;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sammie.barbershopclientmodel.Common.Common;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.paperdb.Paper;

public class MyFCMService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Common.updateToken(this, s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //comment to prevent notication when staff click done but
        // but enable if notification is from confrim booking
//        Common.showNotification(this, new Random().nextInt(),
//                remoteMessage.getData().get(Common.TITLE_KEY),
//                remoteMessage.getData().get(Common.CONTENT_TYPE),
//                null);


        //datasend
        if (remoteMessage.getData() != null) {
            if (remoteMessage.getData().get("update_done") != null) {
                updateLastBookingHistory();
            } else if (remoteMessage.getData().get("isConfirm") != null) {
                //set  notification
                Common.showNotification(this, new Random().nextInt(),
                        remoteMessage.getData().get(Common.TITLE_KEY),
                        remoteMessage.getData().get(Common.CONTENT_TYPE),
                        null);

                updateConfirmStatus();
            }

//            if (remoteMessage.getData().get(Common.TITLE_KEY) != null &&
//                    remoteMessage.getData().get(Common.CONTENT_TYPE) != null) {
//                Common.showNotification(this, new Random().nextInt(),
//                        remoteMessage.getData().get(Common.TITLE_KEY), remoteMessage.getData().get(Common.CONTENT_TYPE),
//                        null);
//            }
        }


    }


    private void updateLastBookingHistory() {

        CollectionReference userBooking;

        if (Common.currentUser != null) {
            userBooking = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking");

        } else {

            // if app anot running
            Paper.init(this);
            String user = Paper.book().read(Common.LOGGED_KEY);

            userBooking = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(user)
                    .collection("Booking");

        }

        //check if exist by get currentdate
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 0);
        calendar.add(Calendar.MINUTE, 0);

        Timestamp timestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
        userBooking.whereGreaterThanOrEqualTo("timestamp", timestamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyFCMService.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        DocumentReference userBookingCurrentDocument = null;
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            userBookingCurrentDocument = userBooking.document(documentSnapshot.getId());

                        }

                        if (userBookingCurrentDocument != null) {
                            Map<String, Object> dataUpate = new HashMap<>();
                            dataUpate.put("done", true);
                            userBookingCurrentDocument.update(dataUpate)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MyFCMService.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }

            }
        });
    }

    private void updateConfirmStatus() {

        CollectionReference userBooking;

        if (Common.currentUser != null) {
            userBooking = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking");

        } else {

            // if app anot running
            Paper.init(this);
            String user = Paper.book().read(Common.LOGGED_KEY);

            userBooking = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(user)
                    .collection("Booking");

        }

        //check if exist by get currentdate
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 0);
        calendar.add(Calendar.MINUTE, 0);

        Timestamp timestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
        userBooking.whereGreaterThanOrEqualTo("timestamp", timestamp)
                .whereEqualTo("isConfirm", "Booking is not confirmed")
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyFCMService.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        DocumentReference userBookingCurrentDocument = null;
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            userBookingCurrentDocument = userBooking.document(documentSnapshot.getId());

                        }

                        if (userBookingCurrentDocument != null) {
                            Map<String, Object> dataUpate = new HashMap<>();
                            dataUpate.put("isConfirm", "Booking Confirmed");
                            userBookingCurrentDocument.update(dataUpate)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MyFCMService.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }

            }
        });
    }


}
