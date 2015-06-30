package org.rss.feed.reader.activities;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.rss.feed.reader.db.NasaFeedEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class RssContentActivity extends AppCompatActivity {

    private Bitmap image = null;
    NasaFeedEntity nasaFeedEntity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_feed_content_layout);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String url = intent.getStringExtra("url");
        String date = intent.getStringExtra("date");
        String pageUrl = intent.getStringExtra("page_url");
        nasaFeedEntity = new NasaFeedEntity(title, date, url, description);
        nasaFeedEntity.setFeedUrl(pageUrl);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resetDisplay(nasaFeedEntity);
            }
        });
    }

    public void makeButtonsVisible(int buttonId) {
        Button downloadButton = (Button) findViewById(buttonId);
        downloadButton.setVisibility(View.VISIBLE);
    }

    public void resetDisplay(NasaFeedEntity feedParser) {
        TextView name = (TextView) findViewById(R.id.title);
        name.setText(feedParser.getTitle());
        TextView date = (TextView) findViewById(R.id.date);
        date.setText(feedParser.getDate());
        TextView description = (TextView) findViewById(R.id.description);
        String htmltext = feedParser.getDescription();
        Spanned sp = Html.fromHtml(htmltext);
        description.setText(sp);
    }



    private Bitmap getBitImage(String url) {
        Bitmap image = null;
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            image = BitmapFactory.decodeStream(input,null,options);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return image;
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, InputStream resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(resId);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(resId);
    }


    public void downloadImage(View view) {
        ProgressDialog dialog = new ProgressDialog(RssContentActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Kindly be patient since image is large....");
        dialog.setCanceledOnTouchOutside(false);
        ImageBackGroundTask backGroundTask = new ImageBackGroundTask(dialog);
        backGroundTask.execute();
    }

    private class ImageBackGroundTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = null;

        private ImageBackGroundTask(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (image == null) {
                if (nasaFeedEntity.getImageUrl() != null)
                    image = getBitImage(nasaFeedEntity.getImageUrl());

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageView imageView = (ImageView) findViewById(R.id.image);
            if (image != null) {
                imageView.setImageBitmap(image);
                makeButtonsVisible(R.id.wallpaper);
            } else {
                Toast.makeText(getApplicationContext(), "This feed does not provide image, you can go to original feed for details...", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }

    public void onCancel(View view) {
        finish();
    }

    public void webViewDisplay(View view) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(nasaFeedEntity.getFeedUrl()));
        startActivity(myIntent);
        startActivityForResult(myIntent, 888);
    }

    public void setWallPaper(View view) {
        final Handler handler = new Handler();
        Thread th = new Thread() {
            public void run() {
                WallpaperManager wallpaperManager =
                        WallpaperManager.getInstance(RssContentActivity.this);
                try {
                    wallpaperManager.setBitmap(image);
                    handler.post(
                            new Runnable() {
                                public void run() {
                                    Toast.makeText(RssContentActivity.this,
                                            "Wallpaper set",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (Exception e) {
                    handler.post(
                            new Runnable() {
                                public void run() {
                                    Toast.makeText(RssContentActivity.this,
                                            "Error setting wallpaper",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        };
        th.start();
    }
}
