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
 * Created by dell on 5/28/2017.
 */

public class MyListAdapter extends RealmBaseAdapter<AudioNotesObject> implements ListAdapter {
    private static class ViewHolder {
        TextView note;
        TextView duration;
    }

    public MyListAdapter(@Nullable OrderedRealmCollection<AudioNotesObject> data) {
        super(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.note= (TextView) convertView.findViewById(R.id.audioNote);
            viewHolder.duration= (TextView) convertView.findViewById(R.id.duration);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(adapterData != null){
            AudioNotesObject item = adapterData.get(position);
            viewHolder.note.setText(item.getNoteText());
            Long millis= Long.valueOf(item.getRecordingTime());

            viewHolder.duration.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            ));
        }
        return convertView;
    }
}
