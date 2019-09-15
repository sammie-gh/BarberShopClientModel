package com.sammie.barbershopclientmodel.Interface;

import com.sammie.barbershopclientmodel.Model.Banner;

import java.util.List;

public interface ILookBookLoadlistener {
    void onLookBookLoadSuccess(List<Banner> banners);
    void onLookBooLoadFailed(String message);
}
