package org.rss.feed.reader.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DisPlayWebPageActivity extends Activity {

    WebView webview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Intent in = getIntent();
        String page_url = in.getStringExtra("page_url");

        webview = (WebView) findViewById(R.id.webpage);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(page_url);

        webview.setWebViewClient(new DisPlayWebPageActivityClient());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview != null && webview.canGoBack()) {
            webview.goBack();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        if (webview.canGoBack()) {
            webview.goBack();
            return;
        } else
            super.onBackPressed();
    }

    private class DisPlayWebPageActivityClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
