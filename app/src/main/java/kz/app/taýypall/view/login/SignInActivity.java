package kz.app.taýypall.view.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import kz.app.taýypall.R;
import kz.app.taýypall.common.BaseActivity;
import kz.app.taýypall.data.AppConstants;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.view.home.HomeActivity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class SignInActivity extends BaseActivity implements View.OnClickListener {
    DatabaseReference ref;
    PhoneAuthProvider ver_phone;
    private long backPressedTime;
    private Toast backToast;
    Button Signin;
    ImageButton settings_sign;
    EditText number, passwordET;
    TextView register;
    SharedPrefsHelper sharedPrefs;


    TextView forget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        LoadLocale();

        ref = FirebaseDatabase.getInstance().getReference();
        ver_phone = PhoneAuthProvider.getInstance();


        //Forget - start
        Signin = findViewById(R.id.SigninBT);
        Signin.setOnClickListener(this);
        forget = findViewById(R.id.ForgetTV);
        forget.setOnClickListener(this);
        //Forget  - end

        settings_sign = findViewById(R.id.setting_sign_in);

        settings_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });


        //SignIn -- start
        number = findViewById(R.id.numberET);
        passwordET = findViewById(R.id.passwordET);
        register = findViewById(R.id.registerTV);
        Signin = findViewById(R.id.SigninBT);
        Signin.setOnClickListener(this);
        //SignIn -- end
        sharedPrefs = new SharedPrefsHelper(this);
        if (sharedPrefs.getIslogged()) {
            Intent intent = new Intent(kz.app.taýypall.view.login.SignInActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }


        //Register  - start
        register.setOnClickListener(this);
        //Register  - end

        List<String> affineFormats = new ArrayList<>();
        affineFormats.add("+7 ([000]) [000]-[00]-[00]");

        MaskedTextChangedListener listener = MaskedTextChangedListener.Companion.installOn(
                number,
                "+7 ([000]) [000]-[00]-[00]",
                affineFormats,
                AffinityCalculationStrategy.WHOLE_STRING,
                (maskFilled, extractedValue, formattedText) -> {

                }
        );

        number.setHint(listener.placeholder());

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SigninBT: {
                signIn();
                break;
            }
            case R.id.registerTV: {

                SignUpBottomSheet bottomSheet = new SignUpBottomSheet();
                bottomSheet.show(getSupportFragmentManager(), "bot");
                break;
            }
            case R.id.ForgetTV: {
                Intent intent = new Intent(SignInActivity.this,
                        ForgotPasswordActivity.class);
                startActivity(intent);
                break;

            }
        }
    }

    //Button back system -- start


    @Override
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


    //Button back system -- end


    private void signIn() {
        if (number.length() < 18) {
            showMessage(R.string.incorrect_number);
        } else if (!isPhoneCorrect(number.getText().toString())) {
            showMessage(R.string.incorrect_operator);


        } else if (passwordET.length() < 6) {
            showMessage(R.string.password_length);
        } else {
            checkToSignIn();
        }
    }

    private void checkToSignIn() {
        getRef().child(AppConstants.USERS).
                child(number.getText().toString()).
                child(AppConstants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    showMessage(R.string.num_psw_inc);
                    return;
                }
                //String phone_number = snapshot.child("phone").getValue().toString();
                String password = snapshot.child("password").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                if (password.equals(passwordET.getText().toString())) {
                    sharedPrefs.setPhoneNumber(number.getText().toString().trim());
                    sharedPrefs.setIslogged(true);
                    //UserItem userItem = snapshot.getValue(UserItem.class);
                    FirebaseDatabase.getInstance().getReference("USERS")
                            .child(number.getText().toString()).child("INFO").child("token").setValue(sharedPrefs.getToken());
                    Intent intent = new Intent(kz.app.taýypall.view.login.SignInActivity.this, HomeActivity.class);
                    intent.putExtra("name_of_user", name);
                    intent.putExtra("number", number.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    showMessage(R.string.num_psw_inc);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public boolean isPhoneCorrect(String number) {
        //+7 (707)
        String phoneCode = number.substring(4, 7);
        List<String> series = Arrays.asList("700", "701", "702", "705", "707", "708",
                "747", "775", "776", "778", "771", "777");

        return series.contains(phoneCode);
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"Қазақша", "Русский", "English"};
        AlertDialog.Builder Builder = new AlertDialog.Builder(SignInActivity.this,
                R.style.AlertDialog_AppCompat_Light_NoActionBar);
        Builder.setTitle(R.string.lan_change_title);
        SharedPreferences preferences = getBaseContext().getSharedPreferences("Settings", MODE_PRIVATE);
        int item_selected = preferences.getInt("CheckedItem", 2);
        Builder.setSingleChoiceItems(listItems, item_selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setLocale("kk", 0);
                    recreate();
                } else if (which == 1) {
                    setLocale("ru", 1);
                    recreate();
                } else if (which == 2) {
                    setLocale("en", 2);
                    recreate();
                }
                dialog.dismiss();
            }
        });
        AlertDialog Dialog = Builder.create();
        Dialog.show();
        Dialog.getWindow().setLayout(WRAP_CONTENT,
                500);

        Dialog.getWindow().getAttributes().windowAnimations = R.style.animation_dial;
        Dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void setLocale(String lang, int item_selected) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
        //Save data to shared preferences
        SharedPreferences.Editor editor = getBaseContext().getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My Languages", lang);
        editor.putInt("CheckedItem", item_selected);
        editor.apply();
    }

    public void LoadLocale() {
        SharedPreferences preferences = getBaseContext().getSharedPreferences("Settings", MODE_PRIVATE);
        String language = preferences.getString("My Languages", "");
        int item_selected = preferences.getInt("CheckedItem", 2);
        setLocale(language, item_selected);
    }

//    public void showMessage(int s) {
//        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
//    }


//    public void openActivity(Class className) {
//
//        Intent intent = new Intent(this, className);
//        startActivity(intent);
//        finish();
//
////        getFragmentManager()
////                .beginTransaction()
////                .replace(R.id.fragment_layout,fragment)
////                .commit();
//    }

    public DatabaseReference getRef() {
        return ref;
    }

    public PhoneAuthProvider getVer_phone() {
        return ver_phone;
    }

}