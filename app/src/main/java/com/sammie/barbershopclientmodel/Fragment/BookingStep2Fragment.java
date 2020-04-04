package com.sammie.barbershopclientmodel.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sammie.barbershopclientmodel.Adapter.MyBarberAdapter;
import com.sammie.barbershopclientmodel.Common.SpacesItemDecoration;
import com.sammie.barbershopclientmodel.EventBus.BarberDoneEvent;
import com.sammie.barbershopclientmodel.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookingStep2Fragment extends Fragment {
    MyBarberAdapter adapter;
    Unbinder unbinder;
//    LocalBroadcastManager localBroadcastManager;
    @BindView(R.id.recycler_barber)
    RecyclerView recyclerView;


//EVENT  bus start
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setBarberAdapter(BarberDoneEvent event)
    {
        adapter = new MyBarberAdapter(getContext(), event.getBarberList());
        recyclerView.setAdapter(adapter);
    }

    private static BookingStep2Fragment instance;


    public static BookingStep2Fragment getInstance() {

        if (instance == null)
            instance = new BookingStep2Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_step_two, container, false);
        unbinder = ButterKnife.bind(this, itemView);

        initView();

        return itemView;
    }

    private void initView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new SpacesItemDecoration(4));

    }
}
