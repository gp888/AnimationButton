package com.gp.draw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AnimationButtonMUTA ab = (AnimationButtonMUTA) findViewById(R.id.ab);
        ab.setAnimationButtonListener(new AnimationButtonMUTA.AnimationButtonListener() {
            @Override
            public void onClickListener() {
                ab.start();
            }

            @Override
            public void animationFinish() {
                ab.reset();
            }
        });
    }
}
