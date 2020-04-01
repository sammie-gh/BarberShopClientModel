package com.sammie.barbershopclientmodel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sammie.barbershopclientmodel.Adapter.MyViewPagerAdapter;
import com.sammie.barbershopclientmodel.Common.Common;
import com.sammie.barbershopclientmodel.Common.NonSwipeViewPager;
import com.sammie.barbershopclientmodel.Model.Barber;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookingActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;
    SweetAlertDialog dialog;
    CollectionReference barberRef;

    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.view_pager)
    NonSwipeViewPager viewPager;
    @BindView(R.id.btn_previous)
    Button btn_Previous_step;
    @BindView(R.id.btn_next)
    Button btn_next_step;

    //Event
    @OnClick(R.id.btn_previous)
    void previousStep() {

        if (Common.step == 3 || Common.step > 0) {
            Common.step--;

            viewPager.setCurrentItem(Common.step);
            if (Common.step < 3)//alwyas enable NEXT when < 3
            {
                btn_next_step.setEnabled(true);
                setColorButton();
            }
        }
    }

    @OnClick(R.id.btn_next)
    void nextClick() {
        if (Common.step < 3 || Common.step == 0) {
            Common.step++;//increase here
            if (Common.step == 1) //after choose salon
            {
                if (Common.currentSalon != null)
                    loadBarberBySalon(Common.currentSalon.getSalonId());

            } else if (Common.step == 2)//time slot
            {
                if (Common.currentBarber != null)
                    loadTimeSlotOfBarber(Common.currentBarber.getBarberId());
            } else if (Common.step == 3)//confirm
            {
                if (Common.currentTimeSlot != -1)
                    confirmBooking();

            }
            viewPager.setCurrentItem(Common.step);
        }
    }

    private void confirmBooking() {
        //send broadcast to step four
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeSlotOfBarber(String barberId) {

        //send local broadcast to fragment step
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadBarberBySalon(String salonId) {

        dialog.show();

        //now select all barber
//AllSalon/Ho/Branch/IW9io42puOugPB5do2rj/Barbers
        if (!TextUtils.isEmpty(Common.city)) {
            Log.d("CitySelected", "" + Common.city);
//            Toast.makeText(this, "" + Common.city, Toast.LENGTH_SHORT).show();
            barberRef = FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.city)
                    .collection("Branch")
                    .document(salonId)
                    .collection("Barbers");


            barberRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            ArrayList<Barber> barbers = new ArrayList<>();
                            for (QueryDocumentSnapshot barberSnapshot : task.getResult()) {
                                Barber barber = barberSnapshot.toObject(Barber.class);
                                barber.setPassword("");
                                barber.setBarberId(barberSnapshot.getId());

                                barbers.add(barber);
                            }

                            //send broadcast to bookingFrag2 load Recycler
                            Intent intent = new Intent(Common.KEY_BARBER_LOAD_DONE);
                            intent.putParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE, barbers);
                            localBroadcastManager.sendBroadcast(intent);
                            dialog.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                }
            });
        }

    }

    //Broadcast Receiver
    private BroadcastReceiver buttonNextReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int step = intent.getIntExtra(Common.KEY_STEP, 0);

            if (step == 1)
                Common.currentSalon = intent.getParcelableExtra(Common.KEY_SALON_STORE);
            else if (step == 2)
                Common.currentBarber = intent.getParcelableExtra(Common.KEY_BARBER_SELECTED);
            else if (step == 3)
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT, -1);
            btn_next_step.setEnabled(true);
            setColorButton();
        }
    };


//    @Override
//    public void onBackPressed() {
//        //super.onBackPressed();
//        //create a dialog to ask yes no question whether or not the user wants to exit
//    }
    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReciever);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(BookingActivity.this);

        dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Loading");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReciever, new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        setupStepView();
        setColorButton();

        //View
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4); // we have four fragments
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                //show step
                stepView.go(i, true);
                if (i == 0)
                    btn_Previous_step.setEnabled(false);
                else
                    btn_Previous_step.setEnabled(true);

                //set diasable btn next here
                btn_next_step.setEnabled(false);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setColorButton() {

        if (btn_next_step.isEnabled()) {
            btn_next_step.setBackgroundResource(R.color.colorButton);
        } else {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if (btn_Previous_step.isEnabled()) {
            btn_Previous_step.setBackgroundResource(R.color.colorButton);
        } else {
            btn_Previous_step.setBackgroundResource(android.R.color.darker_gray);
        }

    }

    private void setupStepView() {

        List<String> stepList = new ArrayList<>();
        stepList.add("Department");
        stepList.add("Doctor");
        stepList.add("Time");
        stepList.add("Confirm");

        stepView.setSteps(stepList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("FragmentA.java","onActivityResult called");
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

//        if (requestCode == Common.RequestCode.IMPORT && data != null) {
//            if (resultCode == Activity.RESULT_OK) {
////                AddInvoiceToDatabase();
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toasty.error(this, "Payment failed", Toast.LENGTH_SHORT).show();
//
//            } else if (resultCode == Activity.RESULT_FIRST_USER)
//                Toasty.error(this, "Payment was cancelled by user", Toast.LENGTH_SHORT).show();
//
//
//        }


    }
}
