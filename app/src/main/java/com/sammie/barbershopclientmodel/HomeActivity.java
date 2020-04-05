package com.sammie.barbershopclientmodel;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sammie.barbershopclientmodel.Common.Common;
import com.sammie.barbershopclientmodel.Fragment.HomeFragment;
import com.sammie.barbershopclientmodel.Model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {
    //    @BindView(R.id.bottom_navigation)
//    BottomNavigationView bottomNavigationView;
    BottomSheetDialog bottomSheetDialog;
    CollectionReference userRef;
    SweetAlertDialog dialog;
    private FirebaseAuth mAuth;
    Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(HomeActivity.this);
        mAuth = FirebaseAuth.getInstance();

        //init
        userRef = FirebaseFirestore.getInstance().collection("Users");

        dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        //Check intent if is login = true ,enable full access
        //if is login = false ,just let around shopping
        if (getIntent() != null) {
            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN, false);

            if (isLogin) {
                if (!isFinishing() && !isDestroyed()) {
                    //copy from Babrber Booking app
                    if (!dialog.isShowing())
                        dialog.show();
                }
                //Check if user is exist
//                mAuth.getCurrentUser(new AccountKitCallback<Account>() {
//                    @Override
//                    public void onSuccess(final Account account) {
//                        if (account != null) {
//
//                            DocumentReference currentUser = userRef.document(mAuth.getPhoneNumber().toString());
//                            currentUser.get()
//                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                            if (task.isSuccessful()) {
//
//                                                DocumentSnapshot userSnapShot = task.getResult();
//                                                if (!userSnapShot.exists()) {
//                                                    showUpdateDialog(account.getPhoneNumber().toString());
//                                                    bottomNavigationView.setEnabled(false);
//                                                }
//                                                else {
//                                                    //user already loggged
//                                                    Common.currentUser = userSnapShot.toObject(User.class);
//                                                    bottomNavigationView.setSelectedItemId(R.id.home_action);
//                                                }
//
//                                                if (dialog.isShowing()) {
//                                                    dialog.dismiss();
//                                                }
//
//                                            }
//                                        }
//                                    });
//                        }
//
//                    }
//
//                    @Override
//                    public void onError(AccountKitError accountKitError) {
//                        Toasty.error(HomeActivity.this, "" + accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
                //Check if user is exist
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    DocumentReference currentUser = userRef.document(user.getUid());
                    Paper.init(HomeActivity.this);
                    Paper.book().write(Common.LOGGED_KEY,user.getUid()).toString(); //change for id login

                    currentUser.get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot userSnapShot = task.getResult();
                                        if (!userSnapShot.exists()) {
                                            showUpdateDialog(user.getUid());
//                                                    bottomNavigationView.setEnabled(false);
                                        } else {
                                            //user already logged
                                            Common.currentUser = userSnapShot.toObject(User.class);
//                                                    bottomNavigationView.setSelectedItemId(R.id.home_action);
                                            fragment = new HomeFragment();
                                            loafFragment(fragment);
                                        }

                                        if (dialog.isShowing()) {
                                            dialog.dismiss();
                                        }

                                    }
                                }
                            });


                }


            }
        }

        //btmView
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//
//
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                if (menuItem.getItemId() == R.id.home_action)
//                    fragment = new HomeFragment();
//                else if (menuItem.getItemId() == R.id.action_shopping)
//                    fragment = new ShoppingFragment();
//
//                return loafFragment(fragment);
//            }
//        });


    }

    private boolean loafFragment(Fragment fragment) {
        if (!isFinishing() && !isDestroyed()) {
            if (fragment != null) {

                try {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit(); //fix crash
                } catch (IllegalStateException ignored) {
//               relaunchApp();
                    // There's no way to avoid getting this if saveInstanceState has already been called.
                }


                return true;

            }
        }

        return false;
    }

    private void showUpdateDialog(final String uid) {

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setTitle("One last stop 😁 !");
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_update_info, null);


        Button btn_update = sheetView.findViewById(R.id.btn_update);
        final Calendar myCalendar = Calendar.getInstance();

        final TextInputEditText edt_name = sheetView.findViewById(R.id.edt_name);
        final TextInputEditText edt_address = sheetView.findViewById(R.id.edt_address);
        final TextInputEditText edt_gender = sheetView.findViewById(R.id.edt_gender);
        final TextInputEditText edt_next_kin = sheetView.findViewById(R.id.edt_next_kin);
        final TextInputEditText edt_phone = sheetView.findViewById(R.id.edt_phone);
        final TextInputEditText edt_date_birth = sheetView.findViewById(R.id.edt_date_birth);
        final TextInputEditText txtAge = sheetView.findViewById(R.id.txt_age);

        //Date picker dialog
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //update textView
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                edt_date_birth.setText(sdf.format(myCalendar.getTime()));
                edt_date_birth.setTextColor(getResources().getColor(R.color.gray_btn_bg_color));

                txtAge.setText("Your age is " + getAge(year, monthOfYear, dayOfMonth));


            }

        };

        edt_date_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(HomeActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();


            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
                Random r = new Random();
                int randomNumber = r.nextInt();

                final User user = new User(edt_name.getText().toString().trim(),
                        edt_phone.getText().toString().trim(),// must change to uid in database and user class and other places since token is given here
                        edt_address.getText().toString(),
                        edt_next_kin.getText().toString().trim(),
                        edt_gender.getText().toString().trim(),
                        txtAge.getText().toString().trim()
                                .replace("Your age is ", ""),
                        edt_date_birth.getText().toString().trim(),
                        "GHS" + randomNumber
                );


                userRef.document(uid) //was previously uid as phone
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                bottomSheetDialog.dismiss();
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                Toasty.success(HomeActivity.this, "Success Thank you ", Toast.LENGTH_SHORT).show();
                                //load new
                                fragment = new HomeFragment();
                                loafFragment(fragment);

                            }
                        }).addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        bottomSheetDialog.dismiss();
                        if (dialog.isShowing())
                            dialog.dismiss();

                        Common.currentUser = user;
                        fragment = new HomeFragment();
                        loafFragment(fragment);
//                        bottomNavigationView.setSelectedItemId(R.id.home_action);
                        Toasty.error(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }


    private void relaunchApp() {
        Intent mStartActivity = new Intent(HomeActivity.this, HomeActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(HomeActivity.this, mPendingIntentId, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) HomeActivity.this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}
