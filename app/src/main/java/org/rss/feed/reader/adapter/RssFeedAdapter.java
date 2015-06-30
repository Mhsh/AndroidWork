package org.rss.feed.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.rss.feed.reader.activities.R;
import org.rss.feed.reader.db.Entity;

import java.util.List;

/**
 * Created by Admin on 16-06-2015.
 */
public class RssFeedAdapter extends AbstractAdapter {

    public RssFeedAdapter(Context context,int textViewResourceId, List<Entity> addresses) {
        this.context = context;
        this.data=addresses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView== null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.final_list_display_data,null);
        }
        resetDisplay(convertView, position);
        return convertView;
    }

    private void resetDisplay(View convertView, int position) {
        TextView name = (TextView) convertView.findViewById(R.id.feedTitle);
        name.setText(((Entity)data.get(position)).getTitle());
        TextView date = (TextView) convertView.findViewById(R.id.feedDate);
        date.setText(((Entity) data.get(position)).getDate());
    }

}
