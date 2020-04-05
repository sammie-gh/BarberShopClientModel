package com.sammie.barbershopclientmodel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sammie.barbershopclientmodel.Common.Common;
import com.sammie.barbershopclientmodel.EventBus.EnableNextButton;
import com.sammie.barbershopclientmodel.Interface.IRecyclerItemSelectedListener;
import com.sammie.barbershopclientmodel.Model.TimeSlot;
import com.sammie.barbershopclientmodel.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {

    private Context context;
    private List<TimeSlot> timeSlotList;
    private List<CardView> cardViewList;
    //LocalBroadcastManager localBroadcastManager;

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;

        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        myViewHolder.txt_time_slot.setText(new StringBuilder(Common.convertTimeSlotToString(i)).toString());
        if (timeSlotList.size() == 0) //if all position is avail just show list
        {
            //if all time slot is empty all card is enable
            myViewHolder.card_time_slot.setEnabled(true);
            myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            myViewHolder.txt_time_slot_description.setText("Available");
            myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
            myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));


        } else { // if pos is booked
            for (TimeSlot slotValue : timeSlotList) {
                //loop all time  slot from server and set different color
                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if (slot == i) //if slot == position
                {
                    myViewHolder.card_time_slot.setEnabled(false);
                    myViewHolder.card_time_slot.setTag(Common.DISABLE_TAG);
                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                    myViewHolder.txt_time_slot_description.setText("BOOKED");
                    myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                    myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));

                }
            }
        }

        if (!cardViewList.contains(myViewHolder.card_time_slot))
            cardViewList.add(myViewHolder.card_time_slot);

        //check if card time slot is available
        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                // loop all card in card list
                for (CardView cardView : cardViewList) {
                    if (cardView.getTag() == null)  //only available card time slot be changed
                        cardView.setCardBackgroundColor(context.getResources()
                                .getColor(android.R.color.white));
                }

                //our selected card will change color
                myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
//                myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));
//                myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                //Event Buss
                EventBus.getDefault().postSticky(new EnableNextButton(3,i));

            }
        });

    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_time_slot, txt_time_slot_description;
        CardView card_time_slot;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_time_slot = itemView.findViewById(R.id.card_time_slot);
            txt_time_slot = itemView.findViewById(R.id.txt_time_slot);

            txt_time_slot_description = itemView.findViewById(R.id.txt_time_slot_description);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
