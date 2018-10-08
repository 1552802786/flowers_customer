package com.yuangee.flower.customer.adapter;

/**
 * 作者：Rookie on 2018/10/8 11:14
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * view pager adapter
 */
public class VpAdapter extends FragmentPagerAdapter {
    private List<Fragment> data;

    public VpAdapter(FragmentManager fm, List<Fragment> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }
}
