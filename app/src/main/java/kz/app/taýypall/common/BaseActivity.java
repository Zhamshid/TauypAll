package kz.app.taýypall.common;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import kz.app.taýypall.R;

public class BaseActivity extends AppCompatActivity {


    DatabaseReference ref;

    PhoneAuthProvider ver_phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        ver_phone = PhoneAuthProvider.getInstance();

    }



    public DatabaseReference getRef() {
        return ref;
    }
    public PhoneAuthProvider getVer_phone() {
        return ver_phone;
    }


    public void showMessage(int s){
        Toast.makeText(this, s ,Toast.LENGTH_SHORT).show();
    }


    public void openActivity(Class className){

        Intent intent = new Intent(this ,className);
        startActivity(intent);finish();

    }

    public void openFragment(Fragment fragment){

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout,fragment)
                .commit();
    }



}

