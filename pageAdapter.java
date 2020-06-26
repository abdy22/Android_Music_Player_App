package com.example.audioplayer;
/*
Author: Abdirahman hassan
created: 6/26/2020
Copyright 2020, Abdirahman Hassan, All right reserved
Description: page adapter  of the music player app.
             lets user flip righr  or left the page.
             Displays data/Music  to user

 */

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class pageAdapter extends FragmentPagerAdapter {
    private int tabCount=0,songPosition;
    private Uri uri;
    ArrayList<String> songlist;
    private FragmentManager fragmentManager;
    private Map<Integer,String> frag_Tags;

    public pageAdapter(FragmentManager fm, int tabCount, int position, ArrayList<String>songlist) {
        super(fm);
        fragmentManager=fm;
        this.tabCount=tabCount;
        this.uri=uri;
        this.songlist=songlist;
        this.songPosition=position;
        frag_Tags=new HashMap<Integer, String>();



    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Object object=super.instantiateItem(container, position);
        if(object instanceof Fragment){
            Fragment newFragment=(Fragment)object;
            String tag=newFragment.getTag();
            frag_Tags.put(position,tag);
        }
        return object;
    }
    public Fragment getFragment(int position){
        String tag=frag_Tags.get(position);
        if(tag==null)return null;
        return fragmentManager.findFragmentByTag(tag);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case (0):
                return new tracks_fragment();
            case (1):

                favouritesTracks fv=new favouritesTracks();
                 return fv;
            default:
                    return null;

        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Tracks";
            case 1:
                return "favourite";
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
