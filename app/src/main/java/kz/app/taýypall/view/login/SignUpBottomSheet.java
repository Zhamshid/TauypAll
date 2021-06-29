package kz.app.taýypall.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kz.app.taýypall.R;
import kz.app.taýypall.data.AppConstants;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.model.UserItem;


public class SignUpBottomSheet extends BottomSheetDialogFragment{

    EditText num;
    DatabaseReference ref;
    private EditText numberEditText, nameEdiText, nameEdiText5, editEdiText3;
    private Button smsButton;
    SharedPrefsHelper sharedPrefs;


    String[] courses = { "Almaty", "Nur-Sultan",
            "Shymkent", "Turkistan",
            "Aktobe", "Atyrau","Aktau","Oskemen","Oral","Kostanay","Karag'andy","Pavlodar","Petropavl","Semei","Kyzylorda"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ProgressBar progressBar = getView().findViewById(R.id.progressBar);

        ref  = FirebaseDatabase.getInstance().getReference();

        Spinner spino = getView().findViewById(R.id.spinner);


        ArrayAdapter<String> ad = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, courses);

        spino.setAdapter(ad);

        sharedPrefs = new SharedPrefsHelper(getContext());
        num = getView().findViewById(R.id.numberET);
        ref   = FirebaseDatabase.getInstance().getReference();
        numberEditText = view.findViewById(R.id.editText2);
        nameEdiText = view.findViewById(R.id.editText3);
        nameEdiText5 = view.findViewById(R.id.editText5);
        editEdiText3=view.findViewById(R.id.editText6);
        smsButton = view.findViewById(R.id.buttonBN);
        smsButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);


            if (numberEditText.length() < 18){
                Toast.makeText(getContext(),R.string.incorrect_number,Toast.LENGTH_SHORT).show();
            }
            else if (nameEdiText.getText().length() < 2){
                Toast.makeText(getContext(),R.string.name_length,Toast.LENGTH_SHORT).show();
            }
            else if (nameEdiText5.length() < 6){
                Toast.makeText(getContext(),R.string.password_length,Toast.LENGTH_SHORT).show();
            }
            else if (!nameEdiText5.getText().toString().equals(editEdiText3.getText().toString())){
                Toast.makeText(getContext(), R.string.psw_doesnt_match,Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(),"Minimum password length 6",Toast.LENGTH_SHORT).show();
            }
            else if (!nameEdiText5.getText().toString().equals(editEdiText3.getText().toString())){
                Toast.makeText(getContext(),"Passwords don't match",Toast.LENGTH_SHORT).show();
            }
            else{
                UserItem item = new UserItem(nameEdiText.getText().toString() , numberEditText.getText().toString(), spino.getSelectedItem().toString(), nameEdiText5.getText().toString() );
                ref.child("USERS")
                        .child(numberEditText.getText().toString())
                        .child("INFO")
                        .setValue(item);

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        numberEditText.getText().toString(),
                        60, TimeUnit.SECONDS,
                        getActivity(),

                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.GONE);
                                smsButton.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                smsButton.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                super.onCodeSent(verificationId, forceResendingToken);

                                progressBar.setVisibility(View.GONE);
                                smsButton.setVisibility(View.GONE);
                                smsButton.setVisibility(View.VISIBLE);
                                ref.child(AppConstants.USERS).
                                        child(numberEditText.getText().toString()).
                                        child(AppConstants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String phone_number = snapshot.child("phone").getValue().toString();
                                        String password = snapshot.child("password").getValue().toString();
                                        String name = snapshot.child("name").getValue().toString();

                                        //UserItem userItem = snapshot.getValue(UserItem.class);
                                        //Intent intent = new Intent();
                                        //intent.putExtra("name_of_user",name);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                sharedPrefs.setPhoneNumber(numberEditText.getText().toString().trim());
                                Intent intent = new Intent(getActivity(),VerifyNumberActivity.class);
                                intent.putExtra("verificationId", verificationId);
                                intent.putExtra("number",numberEditText.getText().toString());
                                intent.putExtra("name_of_user",nameEdiText.getText().toString());
                                startActivity(intent);

                            }


                        });
            }
        });
        List<String> affineFormats = new ArrayList<>();
        affineFormats.add("+7 ([000]) [000]-[00]-[00]");

        MaskedTextChangedListener listener = MaskedTextChangedListener.Companion.installOn(
                numberEditText,
                "+7 ([000]) [000]-[00]-[00]",
                affineFormats,
                AffinityCalculationStrategy.WHOLE_STRING,
                (maskFilled, extractedValue, formattedText) -> {

                }
        );

        numberEditText.setHint(listener.placeholder());

    }


}
