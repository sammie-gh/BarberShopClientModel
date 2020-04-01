package com.sammie.barbershopclientmodel.Database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(version = 1,entities = CartItem.class,exportSchema =  false)
public abstract class CartDatabase extends RoomDatabase {

    private static CartDatabase instance;

    public abstract CartDAO cartDAO();

    public static CartDatabase getInstance(Context context) {

        if (instance == null)
            instance = Room.databaseBuilder(context, CartDatabase.class, "MyBarberDB").build();
        return instance;

    }
}
