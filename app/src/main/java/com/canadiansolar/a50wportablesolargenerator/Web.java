package com.canadiansolar.a50wportablesolargenerator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Web extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Manual");
        setContentView(R.layout.activity_web);
        WebView wv=(WebView)findViewById(R.id.webView);

        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl("http://docs.google.com/gview?embedded=true&url="+"http://unpackfuture.com/50 W Spec.pdf");
    }


}
