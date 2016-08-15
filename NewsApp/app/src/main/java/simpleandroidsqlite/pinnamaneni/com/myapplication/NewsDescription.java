package simpleandroidsqlite.pinnamaneni.com.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by pinnamak on 8/12/16.
 */

public class NewsDescription extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
       String newsDetailsString =  getIntent().getStringExtra("newsDetailsUrl");
        WebView webView = (WebView) findViewById(R.id.newsDetails);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(newsDetailsString);

    }
}
