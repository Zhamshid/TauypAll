package kz.app.taýypall.view.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.datatransport.cct.internal.NetworkConnectionInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import kz.app.taýypall.R;
import kz.app.taýypall.common.BaseActivity;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.view.home.create.CreateFragment;
import kz.app.taýypall.view.home.favorites.FavortiresFragment;
import kz.app.taýypall.view.home.homefragment.HomeFragment;
import kz.app.taýypall.view.home.messages.MessagesFragment;
import kz.app.taýypall.view.home.settings.SettingsFragment;
import kz.app.taýypall.view.login.SignInActivity;

public class HomeActivity extends BaseActivity {
    LottieAnimationView lottieAnimationView,lottieAnimationView1;
    private long backPressedTime;
    private Toast backToast;
    ConnectivityManager connectivityManager;
    BottomNavigationView bottomNavigationView;
    Context context;
    FrameLayout frameLayout;
    HomeFragment homeFragment;
    FavortiresFragment favortiresFragment;
    CreateFragment createFragment;
    MessagesFragment messagesFragment;
    SettingsFragment settingsFragment;

    SharedPrefsHelper sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        frameLayout = findViewById(R.id.fragment_layout);
        sharedPrefs = new SharedPrefsHelper(this);

        lottieAnimationView = findViewById(R.id.no_connection_anim);
        lottieAnimationView1 = findViewById(R.id.no_internet_anim);





        //Bottom nav - start
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        //Bottom nav - end

        //Setting Home Fragment as a main fragment - start
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, new HomeFragment()).commit();
        //Setting Home Fragment as a main fragment - start

        bottomNavigationView.setSelectedItemId(R.id.nav_home);


        frameLayout.invalidate();
        bottomNavigationView.invalidate();


        //Check network connection - start

        if (!isConnected()){
            showMessage(R.string.no_connection);
            frameLayout.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);
            lottieAnimationView1.setSpeed(1.1f);
            lottieAnimationView.setSpeed(2.5f);
            lottieAnimationView1.animate().translationY(-200);
            //lottieAnimationView.animate().translationY(-45).setDuration(1000).setStartDelay(2500);
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView1.setVisibility(View.VISIBLE);
        }
        else{
            frameLayout.setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.VISIBLE);
            bottomNavigationView.invalidate();
            frameLayout.invalidate();
            lottieAnimationView1.cancelAnimation();
            lottieAnimationView.cancelAnimation();
            lottieAnimationView.setVisibility(View.GONE);
            lottieAnimationView1.setVisibility(View.GONE);
        }





        //Check network connection - end



    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:

                            if (homeFragment == null)
                                homeFragment = new HomeFragment();
                            selectedFragment = homeFragment;
                            break;

//                        case R.id.nav_favorite:
//                            if(favortiresFragment == null)
//                                favortiresFragment = new FavortiresFragment();
//                            selectedFragment = favortiresFragment;
//                            break;

                        case R.id.nav_create:
                            if (createFragment == null)
                                createFragment = new CreateFragment();
                            selectedFragment = createFragment;
                            break;

                        case R.id.nav_message:
                            if (messagesFragment == null)
                                messagesFragment = new MessagesFragment();
                            selectedFragment = messagesFragment;
                            break;

                        case R.id.nav_settings:
                            Log.e("+++++++9+++++", settingsFragment + "");
                            if (settingsFragment == null)
                                settingsFragment = new SettingsFragment();

                            selectedFragment = settingsFragment;
                            break;
                    }

                    openFragment(selectedFragment);

                    return true;
                }
            };


    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("USERS").child(sharedPrefs.getPhoneNumber()).child("Status");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();


        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    protected void onStart() {
        super.onStart();
        isConnected();
    }

    private boolean isConnected() {
         connectivityManager = (ConnectivityManager) getApplicationContext()
                .getSystemService(context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();

    }

    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), R.string.double_clk_to_exit, Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }


}
