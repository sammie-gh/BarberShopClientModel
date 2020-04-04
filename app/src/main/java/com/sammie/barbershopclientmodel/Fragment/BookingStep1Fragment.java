package com.sammie.barbershopclientmodel.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.sammie.barbershopclientmodel.Adapter.MySalonAdapter;
import com.sammie.barbershopclientmodel.Common.Common;
import com.sammie.barbershopclientmodel.Common.SpacesItemDecoration;
import com.sammie.barbershopclientmodel.Interface.IAllSalonLoadListener;
import com.sammie.barbershopclientmodel.Interface.IBranchLoadListener;
import com.sammie.barbershopclientmodel.Model.Salon;
import com.sammie.barbershopclientmodel.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookingStep1Fragment extends Fragment implements IAllSalonLoadListener, IBranchLoadListener {

    //variable
    private CollectionReference allSalonRef;
    private CollectionReference branchRef;

    private IAllSalonLoadListener iAllSalonLoadListener;
    private IBranchLoadListener iBranchLoadListener;

    @BindView(R.id.spinner)
    MaterialSpinner spinner;

    @BindView(R.id.recycler_salon)
    RecyclerView recycler_salon;

    private SweetAlertDialog alertDialog;
    Unbinder unbinder;


    private static BookingStep1Fragment instance;

    public static BookingStep1Fragment getInstance() {

        if (instance == null)
            instance = new BookingStep1Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allSalonRef = FirebaseFirestore.getInstance().collection("AllSalon");
        iAllSalonLoadListener = this;
        iBranchLoadListener = this;

        alertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        alertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        alertDialog.setTitleText("Loading");
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_step_one, container, false);
        unbinder = ButterKnife.bind(this, itemView);

        initView();
        loadAllSalon();

        return itemView;

    }

    private void initView() {
        recycler_salon.setHasFixedSize(true);
        recycler_salon.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_salon.addItemDecoration(new SpacesItemDecoration(4));
    }

    private void loadAllSalon() {

        allSalonRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> list = new ArrayList<>();
                            list.add("Please choose a Department");
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult())
                                list.add(documentSnapshot.getId());
                            iAllSalonLoadListener.onAllSalonLoadSuccess(list);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iAllSalonLoadListener.onAllSalonLoadFailed(e.getMessage());

            }
        });


    }

    @Override
    public void onAllSalonLoadSuccess(List<String> areaNameList) {

        spinner.setItems(areaNameList);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

                if (position > 0) {
                    loadBranchOfCity(item.toString());
                } else
                    recycler_salon.setVisibility(View.GONE);
            }
        });
    }

    private void loadBranchOfCity(String cityName) {
        alertDialog.show();

        Common.city = cityName;

        branchRef = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(cityName)
                .collection("Branch");

        branchRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Salon> list = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult())
                    {
                        Salon salon = documentSnapshot.toObject(Salon.class);
                        salon.setSalonId(documentSnapshot.getId());
                        list.add(salon);
                    }
                    iBranchLoadListener.onBranchLoadSuccess(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                iBranchLoadListener.onBranchLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllSalonLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBranchLoadSuccess(List<Salon> salonList) {

        MySalonAdapter adapter = new MySalonAdapter(getActivity(), salonList);
        recycler_salon.setAdapter(adapter);
        recycler_salon.setVisibility(View.VISIBLE);
        alertDialog.dismiss();
    }

    @Override
    public void onBranchLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
    }
}
