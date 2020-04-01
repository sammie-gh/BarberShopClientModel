package com.sammie.barbershopclientmodel.Fragment;


import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sammie.barbershopclientmodel.Adapter.MyShoppingItemAdapter;
import com.sammie.barbershopclientmodel.Common.SpacesItemDecoration;
import com.sammie.barbershopclientmodel.Interface.IsShoppingLoadDataListener;
import com.sammie.barbershopclientmodel.Model.ShoppingItem;
import com.sammie.barbershopclientmodel.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingFragment extends Fragment implements IsShoppingLoadDataListener {

    CollectionReference shoppingItemRef;
    IsShoppingLoadDataListener isShoppingLoadDataListener;
    Unbinder unbinder;
    @BindView(R.id.chip_group)
    ChipGroup chipGroup;

    @BindView(R.id.recycler_items)
    RecyclerView recyclerView;
    @BindView(R.id.chip_wax)
    Chip chip_wax;
    @BindView(R.id.chip_spray)
    Chip chip_spray;
    @BindView(R.id.chip_hair_care)
    Chip chip_hair_care;
    @BindView(R.id.chip_body_care)
    Chip chip_body_care;

    public ShoppingFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.chip_wax)
    void waxChipClicked() {
        setSelectedChip(chip_wax);
        loadShoppingItem("Wax");

    }

    @OnClick(R.id.chip_spray)
    void sprayChipClicked() {
        setSelectedChip(chip_spray);
        loadShoppingItem("Spray");
    }

    @OnClick(R.id.chip_hair_care)
    void HairChipClicked() {
        setSelectedChip(chip_hair_care);
        loadShoppingItem("HairCare");
    }

    @OnClick(R.id.chip_body_care)
    void BodyChipClicked() {
        setSelectedChip(chip_body_care);
        loadShoppingItem("BodyCare");
    }

    private void loadShoppingItem(String itemMenu) {
        shoppingItemRef = FirebaseFirestore.getInstance().collection("Shopping")
                .document(itemMenu)
                .collection("Items");
        //get data
        shoppingItemRef.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        isShoppingLoadDataListener.onShoppingLoadDataFailed(e.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<ShoppingItem> shoppingItems = new ArrayList<>();
                    for (DocumentSnapshot itemSnapShot : task.getResult()) {
                        ShoppingItem shoppingItem = itemSnapShot.toObject(ShoppingItem.class);
                        shoppingItem.setId(itemSnapShot.getId());
                        shoppingItems.add(shoppingItem);
                        isShoppingLoadDataListener.onShoppingDataLoadSuccess(shoppingItems);
                    }
                }
            }
        });

    }

    private void setSelectedChip(Chip chip) {
        //set color
        for (int i = 0;
             i < chipGroup.getChildCount();
             i++) {
            Chip chipItem = (Chip) chipGroup.getChildAt(i);
            if (chipItem.getId() != chip.getId()) //if not selected
            {
                chipItem.setChipBackgroundColorResource(android.R.color.darker_gray);
                chipItem.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                chipItem.setChipBackgroundColorResource(android.R.color.holo_orange_dark);
                chipItem.setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_shopping, container, false);
        unbinder = ButterKnife.bind(this, itemView);
        isShoppingLoadDataListener = this;


        //Default load item
        loadShoppingItem("Spray");
        initView();
        return itemView;

    }

    private void initView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList) {
        MyShoppingItemAdapter adapter = new MyShoppingItemAdapter(getContext(), shoppingItemList);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onShoppingLoadDataFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }
}
