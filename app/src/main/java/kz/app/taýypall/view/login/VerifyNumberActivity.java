package kz.app.taýypall.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import kz.app.taýypall.R;
import kz.app.taýypall.common.BaseActivity;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.view.home.HomeActivity;

public class VerifyNumberActivity extends BaseActivity {

    private EditText code1, code2, code3, code4, code5, code6;
    private String verificationId;
    SharedPrefsHelper sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_number);

        TextView textMobile = findViewById(R.id.textMobile);
        Intent intent = getIntent();
        sharedPrefs = new SharedPrefsHelper(getApplicationContext());

        String number = sharedPrefs.getPhoneNumber();


        String fin_num = getIntent().getStringExtra("number");

        textMobile.setText(fin_num);

        System.out.println(textMobile);
        System.out.println(fin_num);
        System.out.println(number);


        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);
        code5 = findViewById(R.id.code5);
        code6 = findViewById(R.id.code6);


        setupVerCodeInput();

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final Button btn_verify = findViewById(R.id.btn_verify_number);

        verificationId = getIntent().getStringExtra("verificationId");

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code1.getText().toString().trim().isEmpty() || code2.getText().toString().trim().isEmpty() ||
                        code3.getText().toString().trim().isEmpty() || code4.getText().toString().trim().isEmpty() ||
                        code5.getText().toString().trim().isEmpty() || code6.getText().toString().trim().isEmpty()) {
                    Toast.makeText(kz.app.taýypall.view.login.VerifyNumberActivity.this, "Please enter valid verification code", Toast.LENGTH_LONG).show();
                    return;
                }
                String code = code1.getText().toString() + code2.getText().toString() + code3.getText().toString() +
                        code4.getText().toString() + code5.getText().toString() + code6.getText().toString();

                if (verificationId != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    btn_verify.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId,
                            code
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    btn_verify.setVisibility(View.VISIBLE);
                                    if (task.isSuccessful()) {
                                        Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent1.putExtra("number", number);
                                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent1);

                                    } else {

                                        Toast.makeText(kz.app.taýypall.view.login.VerifyNumberActivity.this, "Verification code is invalid", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        findViewById(R.id.text_resend_verify_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        getIntent().getStringExtra("number"),
                        60, TimeUnit.SECONDS,
                        kz.app.taýypall.view.login.VerifyNumberActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(kz.app.taýypall.view.login.VerifyNumberActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = newVerificationId;
                                Toast.makeText(kz.app.taýypall.view.login.VerifyNumberActivity.this, "New Verification code sent", Toast.LENGTH_LONG).show();

                            }


                        });

            }
        });

    }

    private void setupVerCodeInput() {
        code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    code2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    code3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    code4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    code5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    code6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
