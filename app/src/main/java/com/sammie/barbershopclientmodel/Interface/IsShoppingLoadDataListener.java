package com.sammie.barbershopclientmodel.Interface;

import com.sammie.barbershopclientmodel.Model.ShoppingItem;

import java.util.List;

public interface IsShoppingLoadDataListener {

    void onShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList);
    void  onShoppingLoadDataFailed(String message);

}
