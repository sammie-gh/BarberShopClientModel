package com.sammie.barbershopclientmodel;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sammie.barbershopclientmodel.Adapter.MyHistoryAdapter;
import com.sammie.barbershopclientmodel.Common.Common;
import com.sammie.barbershopclientmodel.EventBus.UserBookingLoadEvent;
import com.sammie.barbershopclientmodel.Model.BookingInformation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class HistoryActivity extends AppCompatActivity {

    // path /Users/0266663536/Booking
    @BindView(R.id.recycler_history)
    RecyclerView recycler_history;

    @BindView(R.id.txt_history)
    TextView txt_history;
    private Context mContext;
    SweetAlertDialog dialog;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ButterKnife.bind(this);

        mContext = HistoryActivity.this;
        init();
        initView();
        loadUserBookingInformation();
    }

    private void loadUserBookingInformation() {
        dialog.show();
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        //value
        userBooking.whereEqualTo("done", false) //change to true when confirm is implented
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        EventBus.getDefault().post(new UserBookingLoadEvent(false,
                                e.getMessage()));
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<BookingInformation> bookingInformationList = new ArrayList<>();
                    for (DocumentSnapshot userBookingSnapShot : task.getResult()) {
                        BookingInformation bookingInformation = userBookingSnapShot.toObject(BookingInformation.class);
                        bookingInformationList.add(bookingInformation);
                    }

                    //Use EventBus to send
                    EventBus.getDefault().post(new UserBookingLoadEvent(true, bookingInformationList)); //change false

                }

            }
        });
    }

    private void init() {
        dialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Loading ...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recycler_history.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_history.setLayoutManager(layoutManager);
        recycler_history.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

    }

    //Here we will implemet EventBus process
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayData(UserBookingLoadEvent event) {

        if (event.isSuccess()) {
            MyHistoryAdapter adapter = new MyHistoryAdapter(this, event.getBookingInformationList());
            recycler_history.setAdapter(adapter);

            txt_history.setText(new StringBuilder("HISTORY (").append(event.getBookingInformationList().size()).append(")"));

            getSupportActionBar().setTitle(new StringBuilder("Bookings (").append(event.getBookingInformationList().size()).append(")"));

        } else {
            Toast.makeText(mContext, "" + event.getMessage(), Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }
}
