package com.canadiansolar.maple2monitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;


public class Start extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);

        int uiOptions=View.SYSTEM_UI_FLAG_IMMERSIVE;
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);

/*        // Delay for some time
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                jump();
            }
        }, 3000);*/
    }


    public void jump_to_bt(View view){
        Intent BT_Intent=new Intent(this,MainActivity.class);
        startActivity(BT_Intent);
    }

}
