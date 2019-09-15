package com.sammie.barbershopclientmodel.Database;

import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;

import com.sammie.barbershopclientmodel.CartActivity;
import com.sammie.barbershopclientmodel.Common.Common;
import com.sammie.barbershopclientmodel.Interface.ICartItemLoadListener;
import com.sammie.barbershopclientmodel.Interface.ICountItemCartListener;
import com.sammie.barbershopclientmodel.Interface.ISumListener;

import java.util.List;

public class DatabaseUtils {


    public static  void sumCart(CartDatabase db, ISumListener iSumListener)
    {
        SumCartAsync task = new SumCartAsync(db,iSumListener);
        task.execute();
    }

    public static void getAllCart(CartDatabase db, ICartItemLoadListener cartItemLoadListener)
    {
        GetAllCartAsync task = new GetAllCartAsync(db, cartItemLoadListener);
        task.execute();

    }

    public static void updateCart(CartDatabase db, CartItem cartItem)
    {
        UpdateCartAsync task = new UpdateCartAsync(db);
        task.execute(cartItem);
    }


    public static void insertToCart (CartDatabase db,CartItem...cartItems)
    {
        InsertToCartAsync task = new InsertToCartAsync(db);
        task.execute(cartItems);

    }


    public static void countItemCart(CartDatabase db, ICountItemCartListener iCountItemCartListener) {

        CountItemInCartAsync task = new CountItemInCartAsync(db,iCountItemCartListener);
        task.execute();
    }


    /*
    ========================
    ASYN TASK DEFINE
    ========================
    * */
    private static class SumCartAsync extends AsyncTask<Void,Void,Long>
    {
        private final CartDatabase db;
        private final ISumListener listener;

        public SumCartAsync(CartDatabase db, ISumListener listener) {
            this.db = db;
            this.listener = listener;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            return db.cartDAO().sumPrice(Common.currentUser.getPhoneNumber());
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            listener.onSumCartSuccess(aLong);
        }
    }

    private static  class UpdateCartAsync extends AsyncTask<CartItem,Void,Void>{
        private final CartDatabase db;

        public UpdateCartAsync(CartDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(CartItem... cartItems) {
            db.cartDAO().update(cartItems[0]);
            return null;
        }
    }

    private static  class GetAllCartAsync extends AsyncTask<String,Void,List<CartItem>>{

        CartDatabase db;
        ICartItemLoadListener listener;
        public GetAllCartAsync(CartDatabase cartDatabase, ICartItemLoadListener iCartItemLoadListener) {
            db = cartDatabase;
            listener= iCartItemLoadListener;

        }

        @Override
        protected List<CartItem> doInBackground(String... strings) {
            return db.cartDAO().getAllItemFromCart(Common.currentUser.getPhoneNumber());
        }

        @Override
        protected void onPostExecute(List<CartItem> cartItemList) {
            super.onPostExecute(cartItemList);
            listener.onGetAllItemFromCartSuccess(cartItemList);
        }
    }

    private static  class InsertToCartAsync extends AsyncTask<CartItem,Void,Void>{

        CartDatabase db;
        public InsertToCartAsync(CartDatabase cartDatabase) {
            db = cartDatabase;
        }

        @Override
        protected Void doInBackground(CartItem... cartItems) {
            insertToCart(db,cartItems[0]);
            return null;
        }

        private void insertToCart(CartDatabase db, CartItem cartItem) {
// if item already exist just increase quantity
            try {
                db.cartDAO().insert(cartItem);

            }catch ( SQLiteConstraintException e)
            {
                CartItem updateCartItem = db.cartDAO().getProductInCart(cartItem.getProductId(),
                        Common.currentUser.getPhoneNumber());

                updateCartItem.setProductQuantity(updateCartItem.getProductQuantity()+1);
                db.cartDAO().update(updateCartItem);
            }
        }



    }

    private static  class CountItemInCartAsync extends AsyncTask<Void,Void,Integer>{

        CartDatabase db;
        ICountItemCartListener listener;

        public CountItemInCartAsync(CartDatabase cartDatabase,ICountItemCartListener iCountItemCartListener) {
            db = cartDatabase;
            listener = iCountItemCartListener;
        }


        @Override
        protected Integer doInBackground(Void... voids) {
            
            countItemCartRun(db);
            return Integer.parseInt(String.valueOf(countItemCartRun(db)));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            listener.onCartItemCountSuccess(integer.intValue());
        }

        private int countItemCartRun(CartDatabase db) {

            return db.cartDAO().countItemInCart(Common.currentUser.getPhoneNumber());

        }

    }
}
