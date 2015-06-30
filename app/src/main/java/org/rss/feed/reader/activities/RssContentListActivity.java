package org.rss.feed.reader.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.rss.feed.reader.adapter.RssFeedAdapter;
import org.rss.feed.reader.db.Entity;
import org.rss.feed.reader.db.NasaFeedEntity;
import org.rss.feed.reader.services.AsynchronousResponseCatcher;
import org.rss.feed.reader.services.AsyncProcessingLogic;

import java.util.List;

/**
 * Created by Admin on 17-06-2015.
 */
public class RssContentListActivity extends AppCompatActivity implements AsynchronousResponseCatcher {
    ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rss_list_layout);
        if (!isNetworkAvailable(this)) {
            Toast.makeText(this, "Sorry but this application requires Internet connection", Toast.LENGTH_LONG).show();
            finish(); //Calling this method to close this activity when internet is not available.
        }
        dialog = new ProgressDialog(RssContentListActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait while your feed list is loading...");
        dialog.setCanceledOnTouchOutside(false);
        String url = getIntent().getStringExtra("url");
        AsyncProcessingLogic asyncTask = new AsyncProcessingLogic(this, dialog, url);
        asyncTask.execute();

    }

    @Override
    public void processResponse(List<Entity> feedParser) {
        final ListView view = (ListView) findViewById(R.id.rsslist);
        final RssFeedAdapter adapter = new RssFeedAdapter(this, view.getId(), feedParser);
        view.setAdapter(adapter);
        view.setTextFilterEnabled(true);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                // Intent in = new Intent(getApplicationContext(), DisPlayWebPageActivity.class);

                // getting page url
                /*Entity entity = adapter.getItem(position);
                String page_url = ((NasaFeedEntity) entity).getImageUrl();
                Toast.makeText(getApplicationContext(), page_url, Toast.LENGTH_SHORT).show();
                in.putExtra("page_url", "http://feedproxy.google.com/~r/NdtvNews-TopStories/~3/NFN5TM7Tvyc/story01.htm");
                startActivityForResult(in,0);
                */
                // When clicked, show a toast with the TextView text
                Entity entity = adapter.getItem(position);
                String page_url = ((NasaFeedEntity) entity).getFeedUrl();
                Intent myIntent = new Intent(view.getContext(), RssContentActivity.class);
                myIntent.putExtra("title", entity.getTitle());
                myIntent.putExtra("page_url", page_url);
                if (entity instanceof NasaFeedEntity) {

                    myIntent.putExtra("description", ((NasaFeedEntity) entity).getDescription());
                    myIntent.putExtra("url", ((NasaFeedEntity) entity).getImageUrl());
                }
                myIntent.putExtra("date", entity.getDate());
                startActivityForResult(myIntent, 0);
            }
        });

    }

    /**
     * This method check mobile is connected to network.
     *
     * @param context
     * @return true if connected otherwise false.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
