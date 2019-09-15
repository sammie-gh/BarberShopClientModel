package com.sammie.barbershopclientmodel.Database;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CartDAO {

    @Query("SELECT SUM(productPrice*productQuantity) from Cart where userPhone=:userPhone")
    long sumPrice(String userPhone);


    @Query("SELECT * FROM Cart WHERE userPhone =:userPhone")
    List<CartItem> getAllItemFromCart(String userPhone);

    @Query("SELECT COUNT(*) FROM Cart WHERE userPhone =:userPhone")
    int countItemInCart(String userPhone);

    @Query("SELECT * FROM Cart WHERE productId =:productId AND userPhone=:userPhone")
    CartItem getProductInCart(String productId, String userPhone);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insert(CartItem... carts);

    @Update(onConflict = OnConflictStrategy.FAIL)
    void update(CartItem... cart);

    @Delete
    void delete(CartItem cartItem);
    @Query("DELETE FROM Cart WHERE userPhone=:userPhone")
    void clearCart(String userPhone);


}
