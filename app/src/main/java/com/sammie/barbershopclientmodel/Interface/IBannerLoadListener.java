package com.sammie.barbershopclientmodel.Interface;

import com.sammie.barbershopclientmodel.Model.Banner;

import java.util.List;

public interface IBannerLoadListener {
    void onBannerLoadSuccess(List<Banner>banners);
    void onBannerLoadFailed(String message);
}
