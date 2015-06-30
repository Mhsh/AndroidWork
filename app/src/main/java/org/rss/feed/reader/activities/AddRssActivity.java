package org.rss.feed.reader.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class AddRssActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_rss_feed_layout);
    }

    public void add(View view) {
        Intent intent =  getIntent();
        TextView url =  (TextView) findViewById(R.id.url);
        intent.putExtra("url",url.getText().toString());
        TextView description =  (TextView) findViewById(R.id.urlName);
        intent.putExtra("urlName",description.getText().toString());
        this.setResult(RESULT_OK, intent);
        finish();
    }

    public void cancel(View view) {
        finish();
    }
}
