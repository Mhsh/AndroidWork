package org.rss.feed.reader.services;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.rss.feed.reader.db.Entity;
import org.rss.feed.reader.db.NasaFeedEntity;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 12-06-2015.
 */
public class AsyncProcessingLogic extends  AsyncTask<Void, Void, Integer>  {
    ProgressDialog dialog = null;
    private String url;
    @Override
    protected Integer doInBackground(Void... params) {
        getNasaFeeds();
        return null;
    }

    AsynchronousResponseCatcher asynchronousResponseCatcher = null;
    public AsyncProcessingLogic(AsynchronousResponseCatcher asynchronousResponseCatcher,ProgressDialog dialog, String url) {
        this.asynchronousResponseCatcher = asynchronousResponseCatcher;
        this.dialog= dialog;
        this.url = url;
    }

    @Override
    protected void onPreExecute()
    {
        dialog.show();
    }

    @Override
    protected void onPostExecute(Integer result)
    {
        dialog.dismiss();
        asynchronousResponseCatcher.processResponse(nasaFeeds);
    }

    private List<Entity> nasaFeeds = new ArrayList<Entity>();


    private void getNasaFeeds(){
        try {
           InputStream inputStream =  downloadUrl(url);
            try {
                nasaFeeds = new NasaFeedParser().analyzeFeed(inputStream);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getDummyNasaFeeds(){
        NasaFeedEntity entity;
        for(int i=0;i<5;i++){
            entity = new NasaFeedEntity("Nasa Title "+i,"Nasa Date"+i,"Nasa Image Url"+i,"NAsa description"+i);
            nasaFeeds.add(entity);
        }
    }

    public static void main(String args[]) throws IOException, XmlPullParserException {

        new NasaFeedParser().analyzeFeed(downloadUrl("http://feeds.feedburner.com/NdtvNews-TopStories"));
    }


    private static InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        return conn.getInputStream();
    }
}
