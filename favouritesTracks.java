package com.example.audioplayer;
/*
Author: Abdirahman hassan
created: 6/26/2020
Copyright 2020, Abdirahman Hassan, All right reserved.
Description: favourite traks/music fragment class.
             displays favourite music,
             lets user play music from the favourite music section

 */


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.URI;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class favouritesTracks extends Fragment {
    ListView songs;
    ArrayAdapter<String> adapter;
    ArrayList<String> songlist;
    private favouriteAudioListner listner;
    private View view;


    public favouritesTracks() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //songlist=((MainActivity )getActivity()).getFavouriteList();
        songlist=new ArrayList<>();
        view=inflater.inflate(R.layout.fragment_favourites_tracks2, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        songlist=((MainActivity)getActivity()).getFavouriteList();
        songs = (ListView) view.findViewById(R.id.favourite_songs_list);
        adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item,songlist);
        songs.setAdapter(adapter);
        songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listner.favourite_position(i);

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof tracks_fragment.fragmentAudioListner)
            listner=(favouriteAudioListner) context;
        else{
            throw new RuntimeException(context.toString()+"must implement fragmentAudioListner");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listner=null;
    }

    public interface favouriteAudioListner{
        void favourite_position(int position);
    }
}
