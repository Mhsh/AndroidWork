package org.rss.feed.reader.db;

import android.graphics.Bitmap;

import org.rss.feed.reader.db.Entity;

import java.io.Serializable;

/**
 * Created by Admin on 13-06-2015.
 */
public class NasaFeedEntity extends Entity implements Serializable{

    public NasaFeedEntity(String title, String date, String imageUrl, String description) {
        super.setTitle(title);
        super.setDate(date);
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    private String description;
    private String imageUrl;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    private Bitmap image;

}
