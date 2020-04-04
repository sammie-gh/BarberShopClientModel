package com.sammie.barbershopclientmodel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sammie.barbershopclientmodel.EventBus.EnableNextButton;
import com.sammie.barbershopclientmodel.Interface.IRecyclerItemSelectedListener;
import com.sammie.barbershopclientmodel.Model.Salon;
import com.sammie.barbershopclientmodel.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MySalonAdapter extends RecyclerView.Adapter<MySalonAdapter.MyViewHolder> {

    private Context context;
    private List<Salon> salonList;
    private List<CardView> cardViewList;
//    LocalBroadcastManager localBroadcastManager;

    public MySalonAdapter(Context context, List<Salon> salonList) {
        this.context = context;
        this.salonList = salonList;
        cardViewList = new ArrayList<>();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(context)
        .inflate(R.layout.layout_salon,viewGroup,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {

        myViewHolder.txt_salon_name.setText(salonList.get(i).getName());
        myViewHolder.txt_salon_address.setText(salonList.get(i).getAddress());

        if (!cardViewList.contains(myViewHolder.card_salon))
            cardViewList.add(myViewHolder.card_salon);

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                // set white bchdround  for all card not selected

                for (CardView cardView:cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

                //set selected BG for only selected items
                myViewHolder.card_salon.setCardBackgroundColor(context.getResources()
                        .getColor(android.R.color.holo_green_light));

                //Eventbus
                EventBus.getDefault().postSticky(new EnableNextButton(1,salonList.get(i)));


            }
        });
    }

    @Override
    public int getItemCount() {
        return salonList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_salon_name,txt_salon_address;
        CardView card_salon;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_salon = itemView.findViewById(R.id.card_salon);
            txt_salon_name  = itemView.findViewById(R.id.txt_salon_name);
            txt_salon_address  = itemView.findViewById(R.id.txt_salon_address);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }
}
