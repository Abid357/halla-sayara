package com.example.hallasayara.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.hallasayara.fragment.Journeys.JourneyHistoryFragment;
import com.example.hallasayara.fragment.Journeys.JourneyScheduleFragment;

public class PageAdapter extends FragmentPagerAdapter {

    private int tabCount;

    public PageAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new JourneyScheduleFragment();
            case 1:
                return new JourneyHistoryFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public int getItemPosition(@NonNull Object object){
        return POSITION_NONE;
    }
}
