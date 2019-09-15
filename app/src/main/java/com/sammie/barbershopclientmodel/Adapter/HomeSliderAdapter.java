package com.sammie.barbershopclientmodel.Adapter;

import android.widget.Toast;

import com.sammie.barbershopclientmodel.Model.Banner;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class HomeSliderAdapter extends SliderAdapter {

    public HomeSliderAdapter(List<Banner> bannerList) {
        this.bannerList = bannerList;
    }

    private List<Banner> bannerList;
    @Override
    public int getItemCount() {
        return bannerList.size();

    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {

        imageSlideViewHolder.bindImageSlide(bannerList.get(position).getImage());
    }
}
