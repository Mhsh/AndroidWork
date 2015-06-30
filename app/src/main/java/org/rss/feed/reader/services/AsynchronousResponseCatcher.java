package org.rss.feed.reader.services;

import org.rss.feed.reader.db.Entity;

import java.util.List;

/**
 * Created by Admin on 12-06-2015.
 */
public interface AsynchronousResponseCatcher {

    public void processResponse(List<Entity> feedParser);
}
