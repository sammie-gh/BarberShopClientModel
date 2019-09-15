package com.sammie.barbershopclientmodel.Interface;

import com.sammie.barbershopclientmodel.Database.CartItem;

import java.util.List;

public interface ICartItemLoadListener {
    void onGetAllItemFromCartSuccess(List<CartItem> cartItemList);

}
