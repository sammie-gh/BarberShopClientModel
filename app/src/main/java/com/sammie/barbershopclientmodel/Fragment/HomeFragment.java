package com.sammie.barbershopclientmodel.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.AccountKit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
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
    AlertDialog dialog;
    //Firetsore
    CollectionReference bannerRef, lookBookRef;
    //interface
    IBannerLoadListener iBannerLoadListener;
    ILookBookLoadlistener iLookBookLoadlistener;
    IBookingInfoLoadListener iBookingInfoLoadListener;
    IBookingInformationChangeListener iBookingInformationChangeListener;
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
        android.support.v7.app.AlertDialog.Builder confirmDialog = new android.support.v7.app.AlertDialog
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
        /* to deleet we need to delete from babrber collectio
         * userbooking colletion
         * final event calandar*/
        //we need to load Common.currentBookg coz we need some data from booking infrmation

        if (Common.currentBooking != null) {

            dialog.show();

            DocumentReference barberBookingInfo = FirebaseFirestore.getInstance()

                    .collection("AllSalon")
                    .document(Common.currentBooking.getCityBook())
                    .collection("Branch")
                    .document(Common.currentBooking.getSalonId())
                    .collection("Barber")
                    .document(Common.currentBooking.getBarberId())
                    .collection(Common.convertTimeStampToStringKey(Common.currentBooking.getTimestamp()))
                    .document(Common.currentBooking.getSalonId().toString());

            //when we have document justr delete it
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
                    .collection("User")
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
                    Uri eventUri = Uri.parse(Paper.book().read(Common.EVENT_URI_CACHE).toString());
                    getActivity().getContentResolver().delete(eventUri, null, null);

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
    void openCartActivity(){
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
                .collection("User")
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

        dialog = new SpotsDialog.Builder().setContext(getContext()).setTheme(R.style.Custom)
                .setCancelable(false).build();
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
        if (AccountKit.getCurrentAccessToken() != null) {
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
        txt_userName.setText(Common.currentUser.getName());
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

        card_booking_info.setVisibility(View.VISIBLE);


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
