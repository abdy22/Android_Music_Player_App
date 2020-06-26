package com.example.audioplayer;
/*
Author: Abdirahman hassan
created: 6/26/2020
Copyright 2020, Abdirahman Hassan, All right reserved
Description: Tracks/music  class
             displays all the music in the phone,
             lets user chose which music to play.

 */


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class tracks_fragment extends Fragment {
    ListView songs;
    ArrayAdapter<String> adapter;
    ArrayList<String> list;
    private fragmentAudioListner listner;





    public tracks_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        list=((MainActivity )getActivity()).tracksdata();
        View view=inflater.inflate(R.layout.fragment_tracks_fragment, container, false);
        songs=(ListView)view.findViewById(R.id.songInfo);
        adapter= new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item,list);
        songs.setAdapter(adapter);
        songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listner.positionData(i);

            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof fragmentAudioListner)
            listner=(fragmentAudioListner) context;
        else{
            throw new RuntimeException(context.toString()+"must implement fragmentAudioListner");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listner=null;
    }

    public interface fragmentAudioListner{
        void positionData(int position);
    }



}
