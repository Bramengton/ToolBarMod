package org.brmtn.toolbarmod.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import org.brmtn.toolbarmod.fragments.*;

public class SmartFragmentPagerAdapter extends SmartFragmentStatePagerAdapter {
    private static int NUM_ITEMS = 5;

    public SmartFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return First.newInstance();
            case 1:
                return Second.newInstance();
            case 2:
                return Third.newInstance();
            case 3:
                return Four.newInstance();
            case 4:
                return Five.newInstance();
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }
}