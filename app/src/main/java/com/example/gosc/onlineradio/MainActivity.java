package com.example.gosc.onlineradio;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btncontrol;
    Button btnstop;
    Button btnfav;

    private SeekBar volumebar = null;
    private AudioManager audioManager = null;

    MediaPlayer mediaPlayer;

    boolean prepared = false;
    boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Map<String,String> stations = new HashMap<String,String>();
        stations.put("CapitalDisco","http://sr2.inmystream.info:8040/stream/;stream/1");
        stations.put("Classic Radio","http://audioartsradio.com:6006/;stream/1");
        stations.put("Chilltrax","http://server1.chilltrax.com:9010/;stream/1");
        stations.put("90's Era","http://streams.radio.co:80/sdc9cfaf77/listen");
        stations.put("BluesTox","http://85.186.254.205:50390/;stream/1");

        setContentView(R.layout.activity_main);

        btncontrol = findViewById(R.id.btncontrol);
        btnstop = findViewById(R.id.btnstop);

        btncontrol.setEnabled(false);
        btncontrol.setText("Loading");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initControls();


        List<String> keylist = new ArrayList<String>(stations.keySet());
        final List<String> valueslist = new ArrayList<String>(stations.values());

        final ListView listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,keylist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                Long i = listView.getItemIdAtPosition(position);
                String text = valueslist.get(position);
                //System.out.println(text);
                new PlayerTask().execute(text);
            }
        });

        //new PlayerTask().execute("http://sr2.inmystream.info:8040/stream/;stream/1");

        btncontrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(started){
                    started = false;
                    mediaPlayer.pause();
                    btncontrol.setText("Play");
                } else {
                    started = true;
                    mediaPlayer.start();
                    btncontrol.setText("Pause");
                }

            }
        });
        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                recreate();
            }
        });
    }

    private class PlayerTask extends AsyncTask<String,Void,Boolean> {


        protected Boolean doInBackground(String... strings){
            try{
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                prepared = false;
            } catch (IOException e){
                e.printStackTrace();
            }
            return prepared;
        }
        protected void onPostExecute(Boolean aBoolean){
            super.onPostExecute(aBoolean);

            btncontrol.setEnabled(true);
            btncontrol.setText("Play");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(started){
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(started){
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(prepared){
            mediaPlayer.release();
        }
    }
    private void initControls()
    {
        try
        {
            volumebar = (SeekBar)findViewById(R.id.volumebar);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumebar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumebar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
