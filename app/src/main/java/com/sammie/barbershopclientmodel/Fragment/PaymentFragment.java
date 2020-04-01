package com.sammie.barbershopclientmodel.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sammie.barbershopclientmodel.Adapter.MyBarberAdapter;
import com.sammie.barbershopclientmodel.Common.Common;
import com.sammie.barbershopclientmodel.Model.Barber;
import com.sammie.barbershopclientmodel.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PaymentFragment extends Fragment {
    MyBarberAdapter adapter;
    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;


    private BroadcastReceiver baberDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Barber> barberArrayList = intent.getParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE);
              adapter = new MyBarberAdapter(getContext(), barberArrayList);


        }
    };


    private static PaymentFragment instance;


    public static PaymentFragment getInstance() {

        if (instance == null)
            instance = new PaymentFragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(baberDoneReceiver, new IntentFilter(Common.KEY_BARBER_LOAD_DONE));

    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(baberDoneReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View itemView = inflater.inflate(R.layout.activity_payment, container, false);
        unbinder = ButterKnife.bind(this, itemView);

        initView();

        return itemView;
    }

    private void initView() {

    }
}
