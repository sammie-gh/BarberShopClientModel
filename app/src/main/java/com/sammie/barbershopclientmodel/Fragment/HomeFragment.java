package com.sammie.barbershopclientmodel.Fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;
import com.sammie.barbershopclientmodel.Adapter.HomeSliderAdapter;
import com.sammie.barbershopclientmodel.Adapter.LookBookAdapter;
import com.sammie.barbershopclientmodel.BookingActivity;
import com.sammie.barbershopclientmodel.CartActivity;
import com.sammie.barbershopclientmodel.Common.Common;
import com.sammie.barbershopclientmodel.Database.CartDatabase;
import com.sammie.barbershopclientmodel.Database.DatabaseUtils;
import com.sammie.barbershopclientmodel.Interface.IBannerLoadListener;
import com.sammie.barbershopclientmodel.Interface.IBookingInfoLoadListener;
import com.sammie.barbershopclientmodel.Interface.IBookingInformationChangeListener;
import com.sammie.barbershopclientmodel.Interface.ICountItemCartListener;
import com.sammie.barbershopclientmodel.Interface.ILookBookLoadlistener;
import com.sammie.barbershopclientmodel.Model.Banner;
import com.sammie.barbershopclientmodel.Model.BookingInformation;
import com.sammie.barbershopclientmodel.R;
import com.sammie.barbershopclientmodel.Service.PicassoImageLoadingService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import ss.com.bannerslider.Slider;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements IBannerLoadListener, ILookBookLoadlistener, IBookingInfoLoadListener, IBookingInformationChangeListener, ICountItemCartListener {

    CartDatabase cartDatabase;
    @BindView(R.id.notification_badge)
    NotificationBadge notificationBadge;
    @BindView(R.id.layout_user_information)
    LinearLayout layout_user_info;
    @BindView(R.id.txt_user_name)
    TextView txt_userName;
    @BindView(R.id.banner_slider)
    Slider banner_slidey;
    @BindView(R.id.recycler_look_book)
    RecyclerView recyclerView_look_book;
    @BindView(R.id.card_booking_info)
    CardView card_booking_info;
    @BindView(R.id.txt_time)
    TextView txt_time;
    @BindView(R.id.txt_salon_address)
    TextView txt_salon_address;
    @BindView(R.id.txt_salon_barber)
    TextView txt_salon_barber;
    @BindView(R.id.txt_time_remain)
    TextView txt_time_remain;
    @BindView(R.id.lotiie_animation)
    LottieAnimationView lotiie_animation;
    @BindView(R.id.txt_book_info)
    TextView txt_book_info;

    @BindView(R.id.txt_phone)
    TextView txt_phone;
    @BindView(R.id.txt_membership_id_number)
    TextView txt_membership_id_number;
    @BindView(R.id.img_user)
    ImageView img_user;

    private SweetAlertDialog dialog;

    //Firetsore
    CollectionReference bannerRef, lookBookRef;
    //interface
    IBannerLoadListener iBannerLoadListener;
    ILookBookLoadlistener iLookBookLoadlistener;
    IBookingInfoLoadListener iBookingInfoLoadListener;
    IBookingInformationChangeListener iBookingInformationChangeListener;
    private FirebaseAuth mAuth;
    private Unbinder unbinder;

    public HomeFragment() {
        bannerRef = FirebaseFirestore.getInstance().collection("Banner");
        lookBookRef = FirebaseFirestore.getInstance().collection("Lookbook");


    }

    @OnClick(R.id.btn_delete_booking)
    void deleteBooking() {
        deleteBookingFromBarber(false);
    }

    @OnClick(R.id.btn_change_booking)
    void changeBooking() {
        changeBookingFromUser();

    }

    private void changeBookingFromUser() {
        //show dialog
        androidx.appcompat.app.AlertDialog.Builder confirmDialog = new androidx.appcompat.app.AlertDialog
                .Builder(getActivity())
                .setCancelable(false)
                .setTitle("Hey!")
                .setMessage("Do you really want to change booking information ? \n Because we will delete our old booking information\nJust confirm !")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBookingFromBarber(true);

                    }
                });

        confirmDialog.show();


    }

    private void deleteBookingFromBarber(final boolean isChange) {

        /* to deleet we need to delete from babrber collection
         * userbooking colletion
         * final event calandar*/
        //we need to load Common.currentBookg coz we need some data from booking infrmation

        if (Common.currentBooking != null) {
            if (!dialog.isShowing())
                dialog.show();

            //AllSalon/Ho/Branch/IW9io42puOugPB5do2rj/Barbers/H1fXpNpzFt0FH28mUpTr/29_03_2020
            //AllSalon/Ho/Branch/IW9io42puOugPB5do2rj/Barbers/H1fXpNpzFt0FH28mUpTr/21_05_2019
            DocumentReference barberBookingInfo = FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.currentBooking.getCityBook())
                    .collection("Branch")
                    .document(Common.currentBooking.getSalonId())
                    .collection("Barbers")
                    .document(Common.currentBooking.getBarberId())
                    .collection(Common.convertTimeStampToStringKey(Common.currentBooking.getTimestamp()))
                    .document(Common.currentBooking.getSlot().toString());

            //when we have document just delete it
            barberBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    //delete data freom user
                    deleteBookingFromUser(isChange);
                }
            });

        } else {
            Toasty.warning(getContext(), "Current Booking must not be null", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteBookingFromUser(final boolean isChange) {
        //get alll info from user object
        if (!TextUtils.isEmpty(Common.currentBookingId)) {
            DocumentReference userBookingInfo = FirebaseFirestore.getInstance()
                    .collection("Users")
//                    .document(mAuth.getUid())
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking")
                    .document(Common.currentBookingId);

            //delete
            userBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toasty.warning(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    //after we delete from user databse we delete from calander
                    Paper.init(getActivity());
                    if (Paper.book().read(Common.EVENT_URI_CACHE) != null) {
                        String eventString = Paper.book().read(Common.EVENT_URI_CACHE).toString();
                        Uri eventUri = null;
                        if (eventString != null && !TextUtils.isEmpty(eventString))
                            eventUri = Uri.parse(eventString);

                        if (eventUri != null)
                            getActivity().getContentResolver().delete(eventUri, null, null);

                    }
                    Toasty.success(getActivity(), "Success delete booking", Toast.LENGTH_SHORT).show();

                    //Refresh
                    loadUserBooking();

                    //check if ischange -> call from change button we will fire interface
                    if (isChange)
                        iBookingInformationChangeListener.onBookingInformationChange();
                    dialog.dismiss();

                }
            });
        } else {
            dialog.dismiss();
            Toasty.warning(getContext(), "Booking information Id ust not be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.card_ciew_booking)
    void booking() {
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }

    @OnClick(R.id.card_view_cart)
    void openCartActivity() {
        startActivity(new Intent(getActivity(), CartActivity.class));
    }


    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();
        countCartItem();
    }

    private void loadUserBooking() {
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        //Get current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Timestamp todayTimeStamp = new Timestamp(calendar.getTime());

        //select booking information from firebase with dome = false and timestamp greater today
        userBooking
                .whereGreaterThanOrEqualTo("timestamp", todayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {

                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    BookingInformation bookingInformation = queryDocumentSnapshot.toObject(BookingInformation.class);
                                    iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformation, queryDocumentSnapshot.getId());
                                    break;  //Exit loop as as
                                }
                            } else
                                iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage());

            }
        });


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Loading");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        cartDatabase = CartDatabase.getInstance(getContext());

        //init
        Slider.init(new PicassoImageLoadingService());
        iBannerLoadListener = this;
        iLookBookLoadlistener = this;
        iBookingInfoLoadListener = this;
        iBookingInformationChangeListener = this;


        //checked if logged
        if (mAuth.getCurrentUser() != null) {
            setUserInformation();
            loadBanner();
            loadLookBook();
            loadUserBooking();
            countCartItem();
        }// add feature to make non log view

        return view;
    }

    private void countCartItem() {
        DatabaseUtils.countItemCart(cartDatabase, this);
    }

    private void loadLookBook() {
        lookBookRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> lookbooks = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot bannerSnapshot : task.getResult()) {
                                Banner banner = bannerSnapshot.toObject(Banner.class);
                                lookbooks.add(banner);
                            }
                            iLookBookLoadlistener.onLookBookLoadSuccess(lookbooks);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iLookBookLoadlistener.onLookBooLoadFailed(e.getMessage());
                Toasty.error(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();//remove


            }
        });
    }

    private void loadBanner() {
        bannerRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> banners = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot bannerSnapshot : Objects.requireNonNull(task.getResult())) {
                                Banner banner = bannerSnapshot.toObject(Banner.class);
                                banners.add(banner);

                            }
                            iBannerLoadListener.onBannerLoadSuccess(banners);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBannerLoadListener.onBannerLoadFailed(e.getMessage());

            }
        });
    }

    private void setUserInformation() {
        layout_user_info.setVisibility(View.VISIBLE);
        txt_userName.setText(Common.currentUser.getName()); //save to sharedprerence or save instace to prevent crash
        txt_phone.setText(MessageFormat.format("phone :{0}", Common.currentUser.getPhoneNumber()));
        txt_membership_id_number.setText(MessageFormat.format("ID: {0}", Common.currentUser.getIdNumber()));

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("You are about to signOut !")
                        .setContentText("Click Ok to logout or cancel dismiss")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                mAuth.signOut();
                                getActivity().finish();
                            }
                        })
                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();


            }
        });
    }

    @Override
    public void onBannerLoadSuccess(List<Banner> banners) {

        banner_slidey.setAdapter(new HomeSliderAdapter(banners));
    }

    @Override
    public void onBannerLoadFailed(String message) {
        Toasty.error(Objects.requireNonNull(getActivity()), message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLookBookLoadSuccess(List<Banner> banners) {

        recyclerView_look_book.setHasFixedSize(true);
        recyclerView_look_book.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView_look_book.setItemAnimator(new DefaultItemAnimator());
        recyclerView_look_book.setAdapter(new LookBookAdapter(getActivity(), banners));

        //initailze adapter here
//        LookBookAdapter lookBookAdapter = new LookBookAdapter(getActivity(),banners);
//        recyclerView_look_book.setAdapter(lookBookAdapter);

    }

    @Override
    public void onLookBooLoadFailed(String message) {
        Toasty.error(Objects.requireNonNull(getActivity()), message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBookingInfoLoadEmpty() {
        card_booking_info.setVisibility(View.GONE);
        lotiie_animation.setVisibility(View.VISIBLE);
        txt_book_info.setText("Your Current Booking is Displayed Here ");
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInformation, String bookinId) {

        Common.currentBooking = bookingInformation;
        Common.currentBookingId = bookinId;


        txt_salon_address.setText(bookingInformation.getSalonAddress());
        txt_salon_barber.setText(bookingInformation.getBarberName());
        txt_time.setText(bookingInformation.getTime());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(bookingInformation.getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(), 0).toString();

        txt_time_remain.setText(dateRemain);

        txt_book_info.setText("You have as Appointment NB:A Reminder \nNotification As time approaches will be sent from your calendar");
        card_booking_info.setVisibility(View.VISIBLE);
        lotiie_animation.setVisibility(View.GONE);


        dialog.dismiss();
    }

    @Override
    public void onBookingInfoLoadFailed(String message) {

        Toasty.error(getContext(), message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBookingInformationChange() {
        //Here we will just start activity booking
        startActivity(new Intent(getActivity(), BookingActivity.class));

    }

    @Override
    public void onCartItemCountSuccess(int count) {
        notificationBadge.setText(String.valueOf(count));
    }
}
