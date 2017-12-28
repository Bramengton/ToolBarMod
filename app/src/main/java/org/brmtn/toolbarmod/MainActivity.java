package org.brmtn.toolbarmod;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import org.brmtn.toolbarmod.adapters.SmartFragmentPagerAdapter;
import org.brmtn.toolbarmod.views.TabBarView;


public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SmartFragmentPagerAdapter pagerAdapter;
    private TabBarView tabBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(12.0f);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.tab_bar);
            tabBarView = (TabBarView) getSupportActionBar().getCustomView();
        }

        viewPager = (ViewPager) findViewById(R.id.fragment_pager);
        pagerAdapter = new SmartFragmentPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (tabBarView != null) {
                    tabBarView.setOffset(positionOffset);
                    tabBarView.setSelectedTab(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (pagerAdapter != null && pagerAdapter.getRegisteredFragment(position) != null) {
                    for (int i = 0; i < pagerAdapter.getCount(); i++) {
                        if (pagerAdapter.getRegisteredFragment(i) != null) {
                            pagerAdapter.getRegisteredFragment(i).setUserVisibleHint(position == i);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (tabBarView != null) {
            tabBarView.setSelectedTab(0);
        }

        tabBarView.setOnTabClickedListener(new TabBarView.OnTabClickedListener() {
            @Override
            public void onTabClicked(int index) {
                viewPager.setCurrentItem(index);
            }
        });
    }
}
