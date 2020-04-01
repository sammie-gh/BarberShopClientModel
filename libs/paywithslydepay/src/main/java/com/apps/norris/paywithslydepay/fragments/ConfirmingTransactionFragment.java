package com.apps.norris.paywithslydepay.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.apps.norris.paywithslydepay.R;

public class ConfirmingTransactionFragment extends Fragment {

    private static final String ARG_MESSAGE = "message";
    private String message;

    public ConfirmingTransactionFragment() {
        // Required empty public constructor
    }

    public static ConfirmingTransactionFragment newInstance(String message) {
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        ConfirmingTransactionFragment fragment = new ConfirmingTransactionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(ARG_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirming_transaction, container, false);

        TextView msg = view.findViewById(R.id.message);
        msg.setText(message);

        return view;
    }

}
