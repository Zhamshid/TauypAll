package kz.app.taýypall.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kz.app.taýypall.R;
import kz.app.taýypall.common.BaseActivity;

public class ForgotPasswordActivity extends BaseActivity {
    EditText num;
    TextView register;
    private EditText numberEditText;
    Button btn_sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        btn_sign = (Button)findViewById(R.id.button_sgn_frg);
//        register = findViewById(R.id.textView4);
        numberEditText = findViewById(R.id.editText);


        btn_sign.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (numberEditText.length() < 18){
                        Toast.makeText(ForgotPasswordActivity.this,R.string.incorrect_number,Toast.LENGTH_SHORT).show(); }
                    else if(!isPhoneCorrect(numberEditText.getText().toString())){
                        showMessage(R.string.incorrect_operator);}

                    else {
                        PhoneAuthProvider.getInstance().verifyPhoneNumber( numberEditText.getText().toString(),
                            60, TimeUnit.SECONDS,
                            ForgotPasswordActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                        @Override public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) { }
                        @Override public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(getBaseContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                        } @Override public void onCodeSent(@NonNull String verificationId,
                                                           @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            Intent intent = new Intent(getBaseContext(),VerifyNumberActivity.class);
                            intent.putExtra("number", numberEditText.getText().toString());
                            intent.putExtra("verificationId", verificationId);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
        List<String> affineFormats = new ArrayList<>();
        affineFormats.add("+7 ([000]) [000]-[00]-[00]");
        MaskedTextChangedListener listener = MaskedTextChangedListener.Companion.installOn(
                numberEditText, "+7 ([000]) [000]-[00]-[00]", affineFormats,
                AffinityCalculationStrategy.WHOLE_STRING,
                (maskFilled, extractedValue, formattedText) -> { } );
                numberEditText.setHint(listener.placeholder());

    }

    public boolean isPhoneCorrect(String numberEditText){
        //+7 (707)
        String phoneCode = numberEditText.substring(4, 7);
        List<String> series = Arrays.asList("700", "701", "702", "705", "707", "708",
                "747", "775", "776", "778", "771", "777");

        return series.contains(phoneCode);
    }
}