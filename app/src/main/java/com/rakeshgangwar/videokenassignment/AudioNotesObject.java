package com.rakeshgangwar.videokenassignment;

import io.realm.RealmObject;

/**
 * AudioNotesObject is a Realm database model representing a recorded audio note.
 * Each note is associated with a specific YouTube video and contains the transcribed
 * text along with the exact timestamp in the video when it was recorded.
 *
 * This class extends RealmObject to enable automatic persistence and querying
 * through the Realm database.
 *
 * Database Schema:
 * - noteId: Unique identifier (timestamp of creation)
 * - videoId: YouTube video ID (used for filtering notes by video)
 * - noteText: The transcribed text from speech recognition
 * - recordingTime: Video playback position in milliseconds when note was recorded
 *
 * @author Rakesh Gangwar
 * @version 1.0
 */
public class AudioNotesObject extends RealmObject {

    /** Unique identifier for the note, generated using System.currentTimeMillis() */
    public long noteId;

    /** YouTube video ID extracted from the video URL */
    public String videoId;

    /** The transcribed text content of the audio note */
    public String noteText;

    /** Video timestamp in milliseconds when the note was recorded */
    public int recordingTime;

    /**
     * Default constructor required by Realm.
     * Realm uses this constructor to create instances when reading from the database.
     */
    public AudioNotesObject() {
    }

    /**
     * Gets the unique note identifier.
     *
     * @return The note ID (timestamp when note was created)
     */
    public long getNoteId() {
        return noteId;
    }

    /**
     * Sets the unique note identifier.
     *
     * @param noteId The note ID to set (typically System.currentTimeMillis())
     */
    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    /**
     * Gets the YouTube video ID associated with this note.
     *
     * @return The YouTube video ID
     */
    public String getVideoId() {
        return videoId;
    }

    /**
     * Sets the YouTube video ID for this note.
     *
     * @param videoId The YouTube video ID extracted from the video URL
     */
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    /**
     * Gets the transcribed text content of the note.
     *
     * @return The note text transcribed from speech recognition
     */
    public String getNoteText() {
        return noteText;
    }

    /**
     * Sets the text content of the note.
     *
     * @param noteText The transcribed text from speech recognition
     */
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    /**
     * Gets the video timestamp when this note was recorded.
     *
     * @return The video playback position in milliseconds
     */
    public int getRecordingTime() {
        return recordingTime;
    }

    /**
     * Sets the video timestamp for this note.
     *
     * @param recordingTime The video playback position in milliseconds
     */
    public void setRecordingTime(int recordingTime) {
        this.recordingTime = recordingTime;
    }
}
