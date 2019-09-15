package com.sammie.barbershopclientmodel.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sammie.barbershopclientmodel.Database.CartDatabase;
import com.sammie.barbershopclientmodel.Database.CartItem;
import com.sammie.barbershopclientmodel.Database.DatabaseUtils;
import com.sammie.barbershopclientmodel.Interface.ICartItemUpdateListener;
import com.sammie.barbershopclientmodel.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {


    Context context;
    List<CartItem>cartItemList;
    CartDatabase cartDatabase;
    ICartItemUpdateListener iCartItemUpdateListener;


    public MyCartAdapter(Context context, List<CartItem> cartItemList, ICartItemUpdateListener iCartItemUpdateListener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.iCartItemUpdateListener = iCartItemUpdateListener;
        this.cartDatabase = CartDatabase.getInstance(context);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout
        .layout_cart_item,viewGroup,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        Picasso.get().load(cartItemList.get(i).getProductImage()).into(myViewHolder.img_product);
        myViewHolder.txt_cart_name.setText(new StringBuilder(cartItemList.get(i).getProductName()));
        myViewHolder.txt_cart_price.setText(new StringBuilder("$").append(cartItemList.get(i).getProductPrice()));
        myViewHolder.txt_cart_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(i).getProductQuantity())));

        //eevent
        myViewHolder.setiImageButtonListener(new IImageButtonListener() {
            @Override
            public void onImageButtonClick(View view, int pos, boolean isdecrease) {

                if (isdecrease){
                    if (cartItemList.get(pos).getProductQuantity() > 0)
                    {
                        cartItemList.get(pos)
                                .setProductQuantity(cartItemList
                                        .get(pos).getProductQuantity()-1);
                        DatabaseUtils.updateCart(cartDatabase,cartItemList.get(pos));
                    }

                }
                else
                {
                    if (cartItemList.get(pos).getProductQuantity() < 99)
                    {
                        cartItemList.get(pos)
                                .setProductQuantity(cartItemList
                                        .get(pos).getProductQuantity()+1);
                        DatabaseUtils.updateCart(cartDatabase,cartItemList.get(pos));
                    }
                }
                myViewHolder.txt_cart_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(i).getProductQuantity())));
                iCartItemUpdateListener.onCartItemUpdateSuccess();

            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    interface IImageButtonListener{
        void onImageButtonClick(View view,int pos, boolean isdecrease);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_cart_name, txt_cart_price, txt_cart_quantity;
        ImageView img_decrease,img_increase,img_product;

        IImageButtonListener iImageButtonListener;

        public void setiImageButtonListener(IImageButtonListener iImageButtonListener) {
            this.iImageButtonListener = iImageButtonListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_cart_price = itemView.findViewById(R.id.txt_cart_price);
            txt_cart_name = itemView.findViewById(R.id.txt_cart_name);
            txt_cart_quantity = itemView.findViewById(R.id.txt_cart_quantity);


            img_decrease = itemView.findViewById(R.id.img_decrease);
            img_increase = itemView.findViewById(R.id.img_increase);
            img_product = itemView.findViewById(R.id.cart_img);

            //event
            img_decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iImageButtonListener.onImageButtonClick(v,getAdapterPosition(),true);
                }
            });

            img_increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iImageButtonListener.onImageButtonClick(v,getAdapterPosition(), false);

                }
            });




        }
    }
}
