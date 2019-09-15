package com.sammie.barbershopclientmodel.Interface;

import com.sammie.barbershopclientmodel.Model.Salon;

import java.util.List;

public interface IBranchLoadListener {

    void onBranchLoadSuccess(List<Salon> salonList);
    void onBranchLoadFailed(String message);
}
