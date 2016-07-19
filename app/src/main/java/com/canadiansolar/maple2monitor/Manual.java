package com.canadiansolar.maple2monitor;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Manual extends AppCompatActivity {
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Manual");
        setContentView(R.layout.activity_manual);
        WebView wv=(WebView)findViewById(R.id.webView);
        wv.clearCache(true);

        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl("http://docs.google.com/gview?embedded=true&url="+"http://zhongjieshen.com/50 W Spec.pdf");
    }
}
