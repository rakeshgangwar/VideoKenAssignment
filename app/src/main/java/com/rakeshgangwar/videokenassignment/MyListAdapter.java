package com.rakeshgangwar.videokenassignment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * MyListAdapter is a custom adapter for displaying audio notes in a ListView.
 * Extends RealmBaseAdapter to automatically update the UI when the underlying
 * Realm database changes.
 *
 * This adapter implements the ViewHolder pattern for optimal performance,
 * caching view references to reduce the number of findViewById() calls during scrolling.
 *
 * Each list item displays:
 * - The note text content
 * - The video timestamp formatted as MM:SS
 *
 * @author Rakesh Gangwar
 * @version 1.0
 */
public class MyListAdapter extends RealmBaseAdapter<AudioNotesObject> implements ListAdapter {

    /**
     * ViewHolder pattern for caching view references.
     * This improves ListView scrolling performance by avoiding repeated findViewById() calls.
     */
    private static class ViewHolder {
        /** TextView displaying the note text */
        TextView note;

        /** TextView displaying the formatted timestamp */
        TextView duration;
    }

    /**
     * Constructs a new MyListAdapter with Realm data.
     *
     * @param data The Realm collection of AudioNotesObject to display.
     *            Can be null if no data is available yet.
     */
    public MyListAdapter(@Nullable OrderedRealmCollection<AudioNotesObject> data) {
        super(data);
    }

    /**
     * Gets a View that displays the data at the specified position.
     * Implements the ViewHolder pattern for performance optimization.
     *
     * @param position The position of the item within the adapter's data set
     * @param convertView The old view to reuse, if possible
     * @param parent The parent ViewGroup that this view will be attached to
     * @return A View corresponding to the data at the specified position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        // Create new view if we don't have a recycled one
        if (convertView == null) {
            // Inflate the row layout
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item, parent, false);

            // Create and populate ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.note = (TextView) convertView.findViewById(R.id.audioNote);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.duration);

            // Store the ViewHolder in the view's tag for later reuse
            convertView.setTag(viewHolder);
        } else {
            // Reuse the ViewHolder from a recycled view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the view with data
        if (adapterData != null) {
            AudioNotesObject item = adapterData.get(position);

            // Set the note text
            viewHolder.note.setText(item.getNoteText());

            // Convert timestamp from milliseconds to MM:SS format
            Long millis = Long.valueOf(item.getRecordingTime());
            viewHolder.duration.setText(String.format("%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            ));
        }
        return convertView;
    }
}
