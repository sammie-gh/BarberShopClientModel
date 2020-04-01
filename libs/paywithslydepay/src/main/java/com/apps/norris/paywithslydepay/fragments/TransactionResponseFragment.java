package com.apps.norris.paywithslydepay.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.apps.norris.paywithslydepay.R;

public class TransactionResponseFragment extends Fragment {

    private static final String ARG_MESSAGE = "message";
    private static final String ARG_SUCCESS = "success";
    private boolean success;
    private String message;

    public TransactionResponseFragment() {
        // Required empty public constructor
    }

    public static TransactionResponseFragment newInstance(boolean success, String message) {

        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putBoolean(ARG_SUCCESS, success);
        TransactionResponseFragment fragment = new TransactionResponseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            success = getArguments().getBoolean(ARG_SUCCESS);
            message = getArguments().getString(ARG_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_response, container, false);

        TextView titleTV = view.findViewById(R.id.title);
        TextView messageTV = view.findViewById(R.id.message);
        ImageView status = view.findViewById(R.id.status);

        String title;
        if (success) {
            title = "Success";
            status.setImageResource(R.drawable.success);
        } else {
            title = "Error!! \n Contact your Admin if Your wallet was debited Thank You";
            status.setImageResource(R.drawable.error);
        }

        titleTV.setText(title);
        messageTV.setText(message);

        return view;
    }

}
