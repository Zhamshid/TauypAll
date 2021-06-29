package kz.app.taýypall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;

import kz.app.taýypall.view.login.SignInActivity;

public class MainActivity extends AppCompatActivity {
    LottieAnimationView lottieAnimationView;
    private static int SPLASH_TIME_OUT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lottieAnimationView = findViewById(R.id.splash_anim);
        lottieAnimationView.animate().translationY(2000).setDuration(1000).setStartDelay(2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);finish();

            }
        },SPLASH_TIME_OUT);


    }
}