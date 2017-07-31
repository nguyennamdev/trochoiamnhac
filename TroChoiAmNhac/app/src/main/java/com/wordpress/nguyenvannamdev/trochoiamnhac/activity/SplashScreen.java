package com.wordpress.nguyenvannamdev.trochoiamnhac.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.felipecsl.gifimageview.library.GifImageView;
import com.wordpress.nguyenvannamdev.trochoiamnhac.R;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class SplashScreen extends AppCompatActivity {

    private ImageView imageView;
    private Animation animation;
    private GifImageView gifImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        setContentView(R.layout.activity_splash_screen);


        imageView = (ImageView) findViewById(R.id.circleImageView);
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_clockwise);
        imageView.setAnimation(animation);
        gifImageView  = (GifImageView)findViewById(R.id.gifimage);



        try {
            InputStream inputStream  = getAssets().open("backgroundanim.gif");
            byte[] bytes = IOUtils.toByteArray(inputStream);
            gifImageView.setBytes(bytes);
            gifImageView.startAnimation();
        } catch (IOException e) {
            e.printStackTrace();
        }

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
