package kz.app.taýypall.view.home.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import kz.app.taýypall.data.AppConstants;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.view.home.HomeActivity;

public class ChangePassFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forget_password, container, false);

    }

    TextView iz;
    DatabaseReference ref;
    EditText pass1, pass2, verifyCode;
    Button btn;
    SharedPrefsHelper sharedPrefs;
    FirebaseAuth auth;
    String code;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pass1 = getView().findViewById(R.id.editTextTextPassword2);
        pass2 = getView().findViewById(R.id.editTextTextPassword3);
        btn = getView().findViewById(R.id.savebtn);
        iz = getView().findViewById(R.id.toolbar_title);
        ref = FirebaseDatabase.getInstance().getReference();
        verifyCode = getView().findViewById(R.id.verifyCode);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        iz.setText("Change password");
        sharedPrefs = new SharedPrefsHelper(getContext());
        String number = sharedPrefs.getPhoneNumber();
//        String first_psw = pass1.getText().toString();
//        String second_psw = pass2.getText().toString();

        auth = FirebaseAuth.getInstance();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pass1.length() < 6) {
                    Toast.makeText(getContext(), "Password is very simple!", Toast.LENGTH_LONG).show();
                } else if (pass1.getText().toString().equals("123456")) {
                    Toast.makeText(getContext(), "Password is very simple!", Toast.LENGTH_LONG).show();
                } else if (!pass1.getText().toString().equals(pass2.getText().toString())) {
                    Toast.makeText(getContext(), "Password doesn't match", Toast.LENGTH_LONG).show();
                } else {

                    if (btn.getText().toString().equals(R.string.save)) {
                        getSms();
                    } else if (btn.getText().toString().equals("Подтвердить код"))
                        if (btn.getText().toString().equals("Сохранить"))
                            getSms();
                        else if (btn.getText().toString().equals("Подтверждить код"))
                            init();
                    Toast.makeText(getContext(), "Everything is fine", Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    public void getSms() {

        Log.e("YYYYYYYYYYY", "getsms");

        String phone = "+77719731717";

        auth.setLanguageCode("kz");


        PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new
                PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.e("YYYYYYYYYYY", "onVerificationCompleted");
                        btn.setText("Подтвердить код");
                        verifyCode.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e("YYYYYYYYYYY", "onVerificationFailed");
                        Log.e("YYYYYYYYYYY", e.getMessage());


                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                        super.onCodeSent(s, forceResendingToken);
                        Log.e("YYYYYYYYYYY", "onCodeSent: " + s);
                        code = s;
                    }
                };


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(requireActivity())                 // Activity (for callback binding)
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

//        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, requireActivity(), callbacks);

    }

    private void init() {
        String code = verifyCode.getText().toString().trim();


        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(this.code, code));
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    ref.child(AppConstants.USERS).
                            child(sharedPrefs.getPhoneNumber()).
                            child(AppConstants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String upd_psw = snapshot.child("password").getValue().toString();
                            upd_psw = pass1.getText().toString();

                            //UserItem item = new UserItem(upd_name)


                            ref.child("USERS")
                                    .child(sharedPrefs.getPhoneNumber())
                                    .child(AppConstants.INFO)
                                    .child("password")
                                    .setValue(upd_psw);

                            ((HomeActivity) getActivity()).openFragment(new SettingsFragment());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });


    }

}
