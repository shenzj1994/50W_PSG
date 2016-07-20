package com.canadiansolar.maple2monitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class Start extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ImageView mImageView = (ImageView) findViewById(R.id.imageView);
        Animation mAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        mImageView.startAnimation(mAnimation);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                jumpToMain(null);
            }
        }, 3000);
    }

    public void jumpToMain(View view) {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
