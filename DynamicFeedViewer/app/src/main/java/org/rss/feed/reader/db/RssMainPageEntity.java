package org.rss.feed.reader.db;

import android.graphics.Bitmap;

import org.rss.feed.reader.db.Entity;

/**
 * Created by Admin on 25-06-2015.
 */
public class RssMainPageEntity extends Entity {

    private String image;
    private String url;
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
