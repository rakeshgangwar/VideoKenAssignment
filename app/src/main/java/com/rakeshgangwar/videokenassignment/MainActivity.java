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

/**
 * MainActivity is the primary activity for the VideoKen Assignment application.
 * It provides functionality to:
 * - Play YouTube videos from user-provided URLs
 * - Record voice notes with timestamps while watching videos
 * - Store notes in a Realm database
 * - Display and manage recorded notes
 * - Navigate to specific video timestamps by clicking notes
 *
 * This activity extends YouTubeBaseActivity to support embedded YouTube playback
 * and implements OnInitializedListener for YouTube player initialization callbacks.
 *
 * @author Rakesh Gangwar
 * @version 1.0
 */
public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    /** Request code for YouTube player recovery dialog */
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    /** Request code for speech recognition intent */
    private final int SPEECH_RECOGNITION_CODE = 2;

    /** YouTube player view component that displays videos */
    private YouTubePlayerView playerView;

    /** YouTube player instance for controlling video playback */
    private YouTubePlayer player;

    /** EditText field for entering YouTube video URLs */
    private EditText videoUrl;

    /** Realm database instance for storing and querying notes */
    private Realm realm;

    /** ListView for displaying recorded notes */
    private ListView notesList;

    /** Adapter that binds Realm data to the ListView */
    private MyListAdapter notesListAdapter;

    /** Current query results from Realm database */
    private RealmResults<AudioNotesObject> realmResults;

    /**
     * Called when the activity is first created.
     * Initializes the UI components, Realm database, YouTube player, and event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                          shut down, this Bundle contains the data it most recently
     *                          supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
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

    /**
     * Callback invoked when YouTube player is successfully initialized.
     * Stores the player instance for later use in controlling video playback.
     *
     * @param provider The provider that initialized the player
     * @param youTubePlayer The initialized YouTube player instance
     * @param wasRestored Whether the player was restored from a previous state
     */
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        this.player = youTubePlayer;
    }

    /**
     * Callback invoked when YouTube player initialization fails.
     * Shows an error dialog if the error is user-recoverable, otherwise displays a toast message.
     *
     * @param provider The provider that attempted initialization
     * @param youTubeInitializationResult The result containing error information
     */
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = youTubeInitializationResult.toString();
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Initiates speech recognition to record an audio note.
     * Launches Android's speech recognizer to convert spoken words to text.
     * The recognized text is then saved to the database with the current video timestamp.
     * This method is called when the "Record Note" button is clicked.
     *
     * @param v The view that was clicked (Record Note button)
     */
    public void takeAudioNote(View v) {
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

    /**
     * Deletes all notes from the Realm database.
     * This method is called when the "Delete All Notes" button is clicked.
     * Warning: This action cannot be undone.
     *
     * @param v The view that was clicked (Delete All Notes button)
     */
    public void clearEverything(View v) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
    }

    /**
     * Displays all notes from all videos in the notes list.
     * Updates the ListView to show notes across all recorded videos, not just the current one.
     * This method is called when the "Show All Notes" button is clicked.
     *
     * @param v The view that was clicked (Show All Notes button)
     */
    public void showAllNotes(View v) {
        realmResults=realm.where(AudioNotesObject.class).findAll();
        notesListAdapter=new MyListAdapter(realmResults);
        notesList.setAdapter(notesListAdapter);
    }

    /**
     * Displays only notes for the currently loaded video.
     * Filters the notes list to show only notes associated with the video ID
     * extracted from the URL field. This method is called when the "Show Video Notes" button is clicked.
     *
     * @param v The view that was clicked (Show Video Notes button)
     */
    public void showVideoNotes(View v) {
        realmResults=realm.where(AudioNotesObject.class).equalTo("videoId",extractYTId(videoUrl.getText().toString())).findAll();
        notesListAdapter=new MyListAdapter(realmResults);
        notesList.setAdapter(notesListAdapter);
    }

    /**
     * Handles results from launched activities, specifically speech recognition results.
     * When speech recognition completes successfully, creates a new AudioNotesObject
     * with the recognized text, current video ID, and video timestamp.
     *
     * @param requestCode The request code passed to startActivityForResult()
     * @param resultCode The result code returned by the child activity
     * @param data An Intent containing result data
     */
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

    /**
     * Extracts the YouTube video ID from various YouTube URL formats.
     * Supports multiple URL patterns including:
     * - https://www.youtube.com/watch?v=VIDEO_ID
     * - https://youtu.be/VIDEO_ID
     * - https://www.youtube.com/embed/VIDEO_ID
     * - https://www.youtube.com/v/VIDEO_ID
     *
     * Uses regex pattern matching to handle different URL structures.
     *
     * @param ytUrl The YouTube URL to parse
     * @return The extracted video ID, or null if the URL format is not recognized
     */
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

    /**
     * Called when the activity is being destroyed.
     * Performs cleanup operations including:
     * - Closing the Realm database instance to prevent memory leaks
     * - Releasing the YouTube player resources
     *
     * It's critical to close Realm and release the player to free up system resources.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        player.release();
    }
}
