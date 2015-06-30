package org.rss.feed.reader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.rss.feed.reader.db.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 25-06-2015.
 */
public abstract class AbstractAdapter extends BaseAdapter {
    protected List<Entity> data = new ArrayList<>();
    Context context = null;
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Entity getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addRssFeed(Entity entity){
        data.add(entity);
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);
}
