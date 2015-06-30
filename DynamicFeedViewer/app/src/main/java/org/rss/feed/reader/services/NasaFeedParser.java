package org.rss.feed.reader.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Xml;

import org.rss.feed.reader.db.Entity;
import org.rss.feed.reader.db.NasaFeedEntity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 13-06-2015.
 */
public class NasaFeedParser {

    public List<Entity> analyzeFeed(InputStream inputStream) throws XmlPullParserException, IOException {
        List<Entity> entries = new ArrayList<>();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            entries = readFeed(parser);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
        return entries;
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<NasaFeedEntity> entries = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "rss");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("item")) {
                entries.add(readEntry(parser));
            } else {
                parser.next();
            }
        }
        return entries;
    }


    private NasaFeedEntity readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "item");
        String title = null;
        String description = null;
        String imageUrl = null;
        String feedUrl = null;
        String date = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("description")) {
                description = readSummary(parser);
            } else if (name.equals("pubDate")) {
                date = readDate(parser);
            } else if (name.equals("enclosure")) {
                imageUrl = readImageUrl(parser);
            } else if (name.equals("link")) {
                feedUrl = readLink(parser);
            } else {
                skip(parser);
            }
        }
        NasaFeedEntity entity = new NasaFeedEntity(title, date, imageUrl, description);
        entity.setFeedUrl(feedUrl);
        return entity;
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");
        return title;
    }

    // Processes link tags in the feed.
    private String readImageUrl(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "enclosure");
        String url = parser.getAttributeValue(null, "url");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, "enclosure");
        return url;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "link");
        String url = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "link");
        return url;
    }

    // Processes summary tags in the feed.
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "description");
        String summary = android.text.Html.fromHtml(readText(parser)).toString();
        parser.require(XmlPullParser.END_TAG, null, "description");
        return summary;
    }

    // Processes summary tags in the feed.
    private String readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "pubDate");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "pubDate");
        return summary;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private Bitmap getBitmap(String url) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            return BitmapFactory.decodeStream(new URL(url).openStream());
        } catch (IOException ioe) {
            return null;
        }
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
