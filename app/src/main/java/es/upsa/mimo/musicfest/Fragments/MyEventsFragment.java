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

import butterknife.BindView;
import butterknife.ButterKnife;
import es.upsa.mimo.musicfest.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsFragment extends Fragment {


    /*@BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;*/
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static int viewPagerSelection=0;

    public MyEventsFragment() {
        // Required empty public constructor
    }

    public static MyEventsFragment newInstance() {
        MyEventsFragment fragment = new MyEventsFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_events, container, false);

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        setupViewPager(viewPager,savedInstanceState);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        if (savedInstanceState != null) { //when the user rotates the screen

            viewPagerSelection = savedInstanceState.getInt("viewPagerSelected");
            Log.d("VIEWPAGER",""+viewPager.getCurrentItem());
        }
        //Select the first item
        viewPager.setCurrentItem(viewPagerSelection);
        return v;

    }

    private void setupViewPager(ViewPager viewPager,Bundle savedInstanceState) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.addFrag(FollowEventsFragment.newInstance(), "Seguidos");
        adapter.addFrag(EventsFragment.newInstance(), "Próximos 30 días");
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("VIEWPAGER","onsave"+viewPager.getCurrentItem());
        outState.putInt("viewPagerSelected", viewPager.getCurrentItem());
    }
}
