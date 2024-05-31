package com.example.final_project.Splash;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.final_project.R;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView imageView = findViewById(R.id.imageView);

        // animation
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 360f);
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimator.setDuration(1000);
        rotateAnimator.start();

        Thread mSplashThread;
        mSplashThread = new Thread() {
            @Override public  void run() {
                try {
                    synchronized (this) {
                        wait(3000);
                    }
                } catch (InterruptedException ignored) {
                }
                finally {
                    startActivity(new Intent(getApplicationContext(), getStarted.class));
                    finish();
                }
            }
        };
        mSplashThread.start();

    }

}