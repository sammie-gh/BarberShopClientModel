package com.sammie.barbershopclientmodel.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
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
