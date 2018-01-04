package ir.app.ensan.component.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by k.monem on 7/3/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    protected final List<Fragment> mFragmentList = new ArrayList<>();
    protected final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add("");
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public void addFragmentInPosition(Fragment fragment,int position) {
        addFragmentInPosition(fragment,"",position);
    }

    public void addFragmentInPosition(Fragment fragment, String title,int position) {
        mFragmentList.add(position,fragment);
        mFragmentTitleList.add(position,title);
    }

    public List<Fragment> getFragmentList() {
        return mFragmentList;
    }

    public void removeFragment(int position){
        mFragmentList.remove(position);
        mFragmentTitleList.remove(position);
    }

    public void clearAdapter(){
        mFragmentList.clear();
        mFragmentTitleList.clear();
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
