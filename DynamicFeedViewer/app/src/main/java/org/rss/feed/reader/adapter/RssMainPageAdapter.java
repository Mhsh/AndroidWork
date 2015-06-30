package org.rss.feed.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.rss.feed.reader.activities.R;
import org.rss.feed.reader.db.Entity;

import java.util.List;

/**
 * Created by Admin on 25-06-2015.
 */
public class RssMainPageAdapter extends AbstractAdapter {
    public RssMainPageAdapter(Context context, List<Entity> addresses) {
        this.context = context;
        this.data=addresses;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView== null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.welcome_rss_list_data,null);
        }
        resetDisplay(convertView,position);
        return convertView;
    }

    public void remove(int id){
        this.data.remove(id);
    }
    private void resetDisplay(View convertView, int position) {
        TextView name = (TextView) convertView.findViewById(R.id.mainPageTitle);
        name.setText(data.get(position).getTitle());
        TextView date = (TextView) convertView.findViewById(R.id.mainPageDate);
        date.setText(data.get(position).getDate());
        ImageView image = (ImageView) convertView.findViewById(R.id.mainPageImage);
        image.setImageResource(R.drawable.galaxy);
    }
}
