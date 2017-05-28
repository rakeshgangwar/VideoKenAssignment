package com.rakeshgangwar.videokenassignment;

import io.realm.RealmObject;

/**
 * Created by dell on 5/28/2017.
 */

public class AudioNotesObject extends RealmObject {
    public long noteId;
    public String videoId;
    public String noteText;
    public int recordingTime;

    public AudioNotesObject() {
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public int getRecordingTime() {
        return recordingTime;
    }

    public void setRecordingTime(int recordingTime) {
        this.recordingTime = recordingTime;
    }
}
