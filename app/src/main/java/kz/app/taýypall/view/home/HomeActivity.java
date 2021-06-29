package kz.app.taýypall.view.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import kz.app.taýypall.R;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.view.home.create.CreateFragment;
import kz.app.taýypall.view.home.favorites.FavortiresFragment;
import kz.app.taýypall.view.home.homefragment.HomeFragment;
import kz.app.taýypall.view.home.messages.MessagesFragment;
import kz.app.taýypall.view.home.settings.SettingsFragment;

public class HomeActivity extends AppCompatActivity {
    private long backPressedTime;
    private Toast backToast;
    HomeFragment homeFragment;
    FavortiresFragment favortiresFragment;
    CreateFragment createFragment;
    MessagesFragment messagesFragment;
    SettingsFragment settingsFragment;

    SharedPrefsHelper sharedPrefs ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPrefs = new SharedPrefsHelper(this);


        //Bottom nav - start
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        //Bottom nav - end

        //Setting Home Fragment as a main fragment - start
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout,new HomeFragment()).commit();
        //Setting Home Fragment as a main fragment - start

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new
            BottomNavigationView.OnNavigationItemSelectedListener(){
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.nav_home:

                            if(homeFragment == null)
                                homeFragment = new HomeFragment();
                            selectedFragment = homeFragment;
                            break;

                        case R.id.nav_favorite:
                            if(favortiresFragment == null)
                                favortiresFragment = new FavortiresFragment();
                            selectedFragment = favortiresFragment;
                            break;

                        case R.id.nav_create:
                            if(createFragment == null)
                                createFragment = new CreateFragment();
                            selectedFragment = createFragment;
                            break;

                        case R.id.nav_message:
                            if(messagesFragment == null)
                                messagesFragment = new MessagesFragment();
                            selectedFragment = messagesFragment;
                            break;

                        case R.id.nav_settings:
                            Log.e("+++++++9+++++",settingsFragment+"");
                            if(settingsFragment == null)
                                settingsFragment = new SettingsFragment();

                            selectedFragment = settingsFragment;
                            break;
                    }

                    openFragment(selectedFragment);

                    return true;
                }
            };

    public void openFragment(Fragment fragment){

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout,fragment)
                .commit();
    }

    private void status(String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("USERS").child(sharedPrefs.getPhoneNumber()).child("Status");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume(){
        super.onResume();

        status("online");
    }

    @Override
    protected void onPause(){
        super.onPause();
        status("offline");
    }

    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        }else{
            backToast = Toast.makeText(getBaseContext(),R.string.double_clk_to_exit, Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }


}
