package com.sammie.barbershopclientmodel.Database;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Cart")
public class CartItem {

    @PrimaryKey
    @NonNull@ColumnInfo(name = "productId")
    private String productId;


    @ColumnInfo (name = "productName")
    private  String productName;

    @ColumnInfo (name = "productImage")
    private  String productImage;

    @ColumnInfo (name = "productPrice")
    private  Long productPrice;

    @ColumnInfo (name = "productQuantity")
    private  int  productQuantity;

    @ColumnInfo (name = "userPhone")
    private  String  userPhone;

// getter and s

    @NonNull
    public String getProductId() {
        return productId;
    }

    public void setProductId(@NonNull String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Long productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
