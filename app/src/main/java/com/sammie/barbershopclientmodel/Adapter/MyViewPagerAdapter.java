package com.sammie.barbershopclientmodel.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentContainer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.sammie.barbershopclientmodel.Fragment.BookingStep1Fragment;
import com.sammie.barbershopclientmodel.Fragment.BookingStep2Fragment;
import com.sammie.barbershopclientmodel.Fragment.BookingStep3Fragment;
import com.sammie.barbershopclientmodel.Fragment.BookingStep4Fragment;

public class MyViewPagerAdapter extends FragmentPagerAdapter {

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i)
        {
            case 0:
              return BookingStep1Fragment.getInstance();
            case 1:
                return BookingStep2Fragment.getInstance();
            case 2:
                return BookingStep3Fragment.getInstance();
            case 3:
                return BookingStep4Fragment.getInstance();

        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
