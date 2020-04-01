package com.apps.norris.paywithslydepay.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.apps.norris.paywithslydepay.R;

public class ConfirmPaymentFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private String title,message;

    public static ConfirmPaymentFragment newInstance(String title,String message) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE,title);
        args.putString(ARG_MESSAGE,message);
        ConfirmPaymentFragment fragment = new ConfirmPaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ConfirmPaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            message = getArguments().getString(ARG_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_payment, container, false);

        TextView titleTV =  view.findViewById(R.id.title);
        TextView messageTV =view.findViewById(R.id.message);

        titleTV.setText(title);
        messageTV.setText(message);

        return view;
    }

}
