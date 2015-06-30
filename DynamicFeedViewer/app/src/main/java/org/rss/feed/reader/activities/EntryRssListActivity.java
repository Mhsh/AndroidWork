package org.rss.feed.reader.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.rss.feed.reader.adapter.RssMainPageAdapter;
import org.rss.feed.reader.db.Entity;
import org.rss.feed.reader.db.RssFeedDbHandler;
import org.rss.feed.reader.db.RssMainPageEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 25-06-2015.
 */
public class EntryRssListActivity extends AppCompatActivity {
    ProgressDialog dialog = null;
    List<Entity> urlEntities = new ArrayList<>();
    RssMainPageAdapter adapter = null;
    RssFeedDbHandler dbHandler = new RssFeedDbHandler(this, null, null, 1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_rss_list);
        dialog = new ProgressDialog(EntryRssListActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait while your feed list is loading...");
        dialog.setCanceledOnTouchOutside(false);
        ListView view = (ListView) findViewById(R.id.rssMainPageListView);
        registerForContextMenu(view);
        MainPageBackGroundTask bgTask = new MainPageBackGroundTask();
        bgTask.execute();
    }


    private class MainPageBackGroundTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            urlEntities = dbHandler.getAllFeeds();
            return null;
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(Integer result) {
            updateRssList();
            dialog.dismiss();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        if(item.getTitle()=="Delete"){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Entity entity = (Entity)adapter.getItem(info.position);
            dbHandler.deleteProduct(entity.getId());
            adapter.remove(info.position);
            adapter.notifyDataSetChanged();
        }
        return true;
    }
    public void updateRssList() {
        ListView view = (ListView) findViewById(R.id.rssMainPageListView);
        adapter = new RssMainPageAdapter(this, urlEntities);
        view.setAdapter(adapter);
        view.setTextFilterEnabled(true);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent myIntent = new Intent(view.getContext(), RssContentListActivity.class);
                Entity entity = adapter.getItem(position);
                myIntent.putExtra("title", entity.getTitle());
                if (entity instanceof RssMainPageEntity) {
                    myIntent.putExtra("image", ((RssMainPageEntity) entity).getImage());
                    myIntent.putExtra("url", ((RssMainPageEntity) entity).getUrl());
                }
                myIntent.putExtra("date", entity.getDate());
                startActivityForResult(myIntent, 0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_button:
                Intent intent = new Intent(this, AddRssActivity.class);
                startActivityForResult(intent, 777);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 777) {
            if (resultCode == RESULT_OK) {
                String url = data.getStringExtra("url");
                String name = data.getStringExtra("urlName");
                RssMainPageEntity entity = new RssMainPageEntity();
                entity.setDate(new Date().toString());
                entity.setImage(null);
                entity.setUrl(url);
                entity.setTitle(name);
                dbHandler.addProduct(entity);
                adapter.addRssFeed(entity);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
