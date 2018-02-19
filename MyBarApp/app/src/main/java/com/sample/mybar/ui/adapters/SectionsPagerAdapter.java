package com.sample.mybar.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sample.mybar.R;
import com.sample.mybar.ui.fragments.BarListFragment;
import com.sample.mybar.ui.fragments.MapFragment;
import com.sample.mybar.ui.fragments.PlaceholderFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final int FIRST_PAGE = 0;
    private static final int SECOND_PAGE = 1;
    private static final int MAX_PAGE = 2;
    private final Context mContext;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case FIRST_PAGE:
                return new BarListFragment();

            case SECOND_PAGE:
                return new MapFragment();

            default:
                return PlaceholderFragment.newInstance(position + 1);
        }
    }

    @Override
    public int getCount() {
        return MAX_PAGE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case FIRST_PAGE:
                return mContext.getString(R.string.bar_list);

            case SECOND_PAGE:
                return mContext.getString(R.string.map);

            default:
                return null;
        }
    }
}
