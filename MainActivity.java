package com.example.audioplayer;
/*
Author: Abdirahman hassan
created: 6/26/2020
Copyright 2020, Abdirahman Hassan, All right reserved
Description: main class of the music player app
             controls how fragment conmmunicate,
             lets user play music.

 */

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements tracks_fragment.fragmentAudioListner,favouritesTracks.favouriteAudioListner {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<String> songlist,favouriteList;
    private Cursor songCursor;
    private Uri songsData;
    private final int Request_CODE=1;
    private int position,songTile;
    private SeekBar motion;
    MediaPlayer player;
    Runnable runnable;
    Handler handler;
    private Button play,PreviousSongButton,nextSongButton,favouriteButton;
    TextView songTitle,durration;
    private String songName;
    private pageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        viewPager=(ViewPager)findViewById(R.id.view_page);
        motion=(SeekBar)findViewById(R.id.motion);
        songTitle=(TextView)findViewById(R.id.songTitle);
        player=new MediaPlayer();
        durration=(TextView)findViewById(R.id.durration);
        favouriteList=new ArrayList<>();
        favouriteButton=(Button)findViewById(R.id.favouritebutton);
        position=getPosition();
        play=(Button)findViewById(R.id.playButton);
        PreviousSongButton=(Button)findViewById(R.id.PreviousButton);
        nextSongButton=(Button)findViewById(R.id.nextSongButton);
        Log.d("Checking", "onCreate: Position: "+position);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player.isPlaying()) {
                    player.pause();
                    onChangeSeek(player);
                    play.setBackgroundResource(R.drawable.play_icon);
                }
                else {
                    player.start();
                    onChangeSeek(player);
                    play.setBackgroundResource(R.drawable.pause_icon);

                }

            }
        });
        nextSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (songCursor.getCount() > position + 1) {
                        position += 1;
                        positionData(position);
                    } else
                        Toast.makeText(getApplicationContext(), "No more songs", Toast.LENGTH_LONG).show();
                }
        });
        PreviousSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (position - 1 >= 0) {
                        position -= 1;
                        positionData(position);

                    }

                }
        });
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(songName!=null) {
                    Log.d("Songname", "onClick: "+position);
                    if(!favouriteList.contains(songlist.get(getPosition())))
                        favouriteList.add(songlist.get(getPosition()));
                    //adapter.update(songName);


                }

                //Log.d("list", "onClick: "+favouriteList.get(0));
            }
        });

        adapter =new pageAdapter(getSupportFragmentManager(),tabLayout.getTabCount(),position,favouriteList);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragment=((pageAdapter)viewPager.getAdapter()).getFragment(position);
                if(position==1&&fragment!=null)
                    fragment.onResume();


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        motion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b&&player!=null) {
                    player.seekTo(i);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        requestAccess();


    }

    //reguest the access to the user external storoge

    private void requestAccess() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("The access is needed to do the neccassary work")
                        .setPositiveButton("I grant access", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Request_CODE);


                            }
                        })
                        .setNegativeButton("I deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();
            } else
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE}, Request_CODE);
        } else
            getMusic();

    }
    //fetching music from the storage after user permits access
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==Request_CODE){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getMusic();

                Toast.makeText(this,"Permission granted",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();


            }

        }
    }
    //getting music from the storage

    private void getMusic() {
        ContentResolver resolver=getContentResolver();
        songlist=new ArrayList<>();

        songsData= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        songCursor=resolver.query(songsData,null,null,null,null);
        if(songCursor!=null&&songCursor.moveToFirst()){
            songTile=songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int Artist=songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do{

                String title=songCursor.getString(songTile);
                String artistname=songCursor.getString(Artist);
                Log.d("getMusic", "Music: "+songCursor.getString(songTile));
                songlist.add(title);


            }while(songCursor.moveToNext());
        }

    }
    //playing music player with the specified location/index
    public void playMusic(int index){

        ContentResolver resolver=getContentResolver();
        Cursor cursor=resolver.query(songsData,null,null,null,null);
        cursor.moveToPosition(index);
        int sonfData=cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int sonTitle=cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        //Log.d("checking ", "playMusic: "+cursor.getString(sonfData));
        songName=cursor.getString(sonfData);

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            songTitle.setText(cursor.getString(sonTitle));

            //player.reset();
            player.setDataSource(songName);
            player.prepare();
            player.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                motion.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                onChangeSeek(mediaPlayer);
            }
        });

    }
    private void onChangeSeek(final MediaPlayer player1 ) {
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                int positionIndicator=msg.what;
                String timeLapsed=createTime(positionIndicator);
                durration.setText(timeLapsed);




            }
        };
        motion.setProgress(player.getCurrentPosition());
        if(player1.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    onChangeSeek(player1);
                    Message msg=new Message();
                    msg.what=player1.getCurrentPosition();
                    handler.sendMessage(msg);
                }
            };
            handler.postDelayed(runnable,1000);


        }


    }

    private String createTime(int positionIndicator) {
        int minutes = (int) ((positionIndicator % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((positionIndicator % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        String time_lapsed=minutes+":";
        //if(sec<10)timelapsed+="0";
        time_lapsed+=seconds;
        return time_lapsed;
    }

    private int getPosition(){
        return position;
    }
  /*
     gets the position of the song in the traks list and plays that song
  */


    @Override
    public void positionData(int position) {
        this.position=position;

        if(player.isPlaying()){
            player.stop();
            player=new MediaPlayer();
            play.setBackgroundResource(R.drawable.pause_icon);
        }else {
            player = new MediaPlayer();
            play.setBackgroundResource(R.drawable.pause_icon);
        }


        playMusic(position);




    }
    public ArrayList<String> tracksdata(){

        return songlist;
    }
    public ArrayList<String> getFavouriteList(){

        return favouriteList;
    }
  /*
  gets the position of the song in the favourite tracks and plays that song. 
  */

    @Override
    public void favourite_position(int position) {
        if(player.isPlaying()){
            player.stop();
            player=new MediaPlayer();
        }else {
            player = new MediaPlayer();
            play.setBackgroundResource(R.drawable.pause_icon);

        }

        playMusic(position);

    }
}
