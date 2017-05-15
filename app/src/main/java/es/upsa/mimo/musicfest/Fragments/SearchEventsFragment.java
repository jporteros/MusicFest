package es.upsa.mimo.musicfest.Fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.upsa.mimo.musicfest.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchEventsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public SearchEventsFragment() {
        // Required empty public constructor
    }

    public static SearchEventsFragment newInstance() {

        SearchEventsFragment fragment = new SearchEventsFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_search_events, container, false);

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
        SearchEventsFragment.ViewPagerAdapter adapter = new SearchEventsFragment.ViewPagerAdapter(getChildFragmentManager());

        Log.d("dddd","viewpager");
        adapter.addFrag(CityEventsFragment.newInstance(), "Por Ciudad");
        adapter.addFrag(ArtistEventsFragment.newInstance(), "Por Artista");
        viewPager.setAdapter(adapter);

    }

    /**
     * Clase ViewPagerAdapter en el que se crean las distintas pestañas
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {

            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
            //return null;
        }
    }

}
