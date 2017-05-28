package com.rakeshgangwar.videokenassignment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private final int SPEECH_RECOGNITION_CODE = 2;
    private YouTubePlayerView playerView;
    private YouTubePlayer player;
    private EditText videoUrl;
    private Realm realm;
    private ListView notesList;
    private MyListAdapter notesListAdapter;
    private RealmResults<AudioNotesObject> realmResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm=Realm.getDefaultInstance();
        playerView = (YouTubePlayerView) findViewById(R.id.player);
        videoUrl = (EditText) findViewById(R.id.text_video_url);
        notesList = (ListView) findViewById(R.id.listOfNotes);

        playerView.setVisibility(View.INVISIBLE);


        realmResults=realm.where(AudioNotesObject.class).equalTo("videoId",extractYTId(videoUrl.getText().toString())).findAll();
        notesListAdapter=new MyListAdapter(realmResults);
        notesList.setAdapter(notesListAdapter);
        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playerView.setVisibility(View.VISIBLE);
                player.loadVideo(realmResults.get(position).getVideoId(),realmResults.get(position).getRecordingTime());
            }
        });






        playerView.initialize(DeveloperKey.DEVELOPER_KEY, this);

        videoUrl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    playerView.setVisibility(View.VISIBLE);
                    player.loadVideo(extractYTId(videoUrl.getText().toString()));
                    realmResults=realm.where(AudioNotesObject.class).equalTo("videoId",extractYTId(videoUrl.getText().toString())).findAll();
                    notesListAdapter=new MyListAdapter(realmResults);
                    notesList.setAdapter(notesListAdapter);
                }
                return false;
            }
        });

    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        this.player=youTubePlayer;
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = youTubeInitializationResult.toString();
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    public void takeAudioNote(View v){
        if(!videoUrl.getText().toString().equals("")){
            Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");
            try {
                startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Not supported on this device.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Load some video.", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearEverything(View v){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
    }

    public void showAllNotes(View v){
        realmResults=realm.where(AudioNotesObject.class).findAll();
        notesListAdapter=new MyListAdapter(realmResults);
        notesList.setAdapter(notesListAdapter);
    }

    public void showVideoNotes(View v){
        realmResults=realm.where(AudioNotesObject.class).equalTo("videoId",extractYTId(videoUrl.getText().toString())).findAll();
        notesListAdapter=new MyListAdapter(realmResults);
        notesList.setAdapter(notesListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if(resultCode == RESULT_OK && null != data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);

                    realm.beginTransaction();
                    AudioNotesObject audioNotesObject=realm.createObject(AudioNotesObject.class);
                    audioNotesObject.setNoteId(System.currentTimeMillis());
                    audioNotesObject.setNoteText(text);
                    audioNotesObject.setVideoId(extractYTId(videoUrl.getText().toString()));
                    audioNotesObject.setRecordingTime(player.getCurrentTimeMillis());
                    realm.commitTransaction();

                }
                break;
            }
        }
    }

    public static String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()){
            vId = matcher.group(1);
        }
        return vId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        player.release();
    }
}
