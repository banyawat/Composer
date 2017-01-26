package cpe.com.composer.viewmanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

import cpe.com.composer.FingerSetupFragment;

public class MovementPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private ArrayList<String> fragmentTitleArrayList = new ArrayList<>();

    public MovementPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title){
        fragmentArrayList.add(fragment);
        fragmentTitleArrayList.add(title);
    }

    public String getHandData(){
        return ((FingerSetupFragment)fragmentArrayList.get(0)).getDataHandPref();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    public CharSequence getPageTitle(int index){
        return fragmentTitleArrayList.get(index);
    }
}
