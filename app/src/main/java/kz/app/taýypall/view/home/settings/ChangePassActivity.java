package kz.app.taýypall.view.home.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import kz.app.taýypall.R;
import kz.app.taýypall.common.BaseActivity;
import kz.app.taýypall.data.AppConstants;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.view.home.HomeActivity;

public class ChangePassActivity extends BaseActivity {
    TextView iz;
    DatabaseReference ref;
    EditText pass1, pass2, verifyCode;
    Button btn;
    SharedPrefsHelper sharedPrefs;
    FirebaseAuth auth;
    ImageView back_left_arrow;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        pass1 = findViewById(R.id.editTextTextPassword2);
        pass2 = findViewById(R.id.editTextTextPassword3);
        btn = findViewById(R.id.savebtn);
        iz = findViewById(R.id.toolbar_title);
        ref = FirebaseDatabase.getInstance().getReference();
        verifyCode = findViewById(R.id.verifyCode);
        back_left_arrow = findViewById(R.id.back_arrow_left);



        iz.setText(R.string.change_psw);
        sharedPrefs = new SharedPrefsHelper(getBaseContext());
        String number = sharedPrefs.getPhoneNumber();

        auth = FirebaseAuth.getInstance();

        back_left_arrow.setVisibility(View.VISIBLE);

        back_left_arrow.setOnClickListener(v -> {
            onBackPressed();
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pass1.length() < 6) {
                    Toast.makeText(getBaseContext(), R.string.password_length, Toast.LENGTH_LONG).show();
                } else if (pass1.getText().toString().equals("123456")) {
                    Toast.makeText(getBaseContext(), R.string.psw_simple, Toast.LENGTH_LONG).show();
                } else if (!pass1.getText().toString().equals(pass2.getText().toString())) {
                    Toast.makeText(getBaseContext(), R.string.psw_doesnt_match, Toast.LENGTH_LONG).show();
                } else {
                    ref.child(AppConstants.USERS).
                            child(sharedPrefs.getPhoneNumber()).
                            child(AppConstants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String upd_psw = snapshot.child("password").getValue().toString();
                            upd_psw = pass1.getText().toString();


                            ref.child("USERS")
                                    .child(sharedPrefs.getPhoneNumber())
                                    .child(AppConstants.INFO)
                                    .child("password")
                                    .setValue(upd_psw);

                            openActivity(HomeActivity.class);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });

    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }
}