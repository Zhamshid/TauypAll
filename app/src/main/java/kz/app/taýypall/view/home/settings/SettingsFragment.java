package kz.app.taýypall.view.home.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import kz.app.taýypall.R;
import kz.app.taýypall.adapter.ProfileAdapter;
import kz.app.taýypall.common.BaseFragment;
import kz.app.taýypall.data.AppConstants;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.listener.SettingsListener;
import kz.app.taýypall.model.ProfileItem;
import kz.app.taýypall.view.login.SignInActivity;

import static android.content.Context.MODE_PRIVATE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends BaseFragment {

    RecyclerView profile_rec;
    Button btn_sign_out;
    DatabaseReference ref;
    TextView iz;
    ProfileAdapter profileAdapter;
    SharedPrefsHelper sharedPrefs;

    private FirebaseStorage storage;
    private StorageReference storageReference;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<ProfileItem> list;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);


    }


    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = new ArrayList<>();
        list.add(new ProfileItem(R.drawable.ic_profile, "", "", R.drawable.ic_edit));
        list.add(new ProfileItem(R.drawable.ic_key, (R.string.change_psw)));
        list.add(new ProfileItem(R.drawable.ic_change_language_2, R.string.change_language));
        list.add(new ProfileItem(R.drawable.ic_my_posts, R.string.my_publish));
        list.add(new ProfileItem(R.drawable.ic_credit_card, R.string.support_project));

        // Inflate the layout for this fragment
        sharedPrefs = new SharedPrefsHelper(getContext());
        LoadLocale();
        profile_rec = view.findViewById(R.id.profile_recyclerview);
        ref = FirebaseDatabase.getInstance().getReference();
        iz = view.findViewById(R.id.toolbar_title);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        iz.setText(R.string.profile);
        String number = sharedPrefs.getPhoneNumber();



        showLoading();

        Log.e(">>>>>>>>>>>>>",number);

        ref.child(AppConstants.USERS).
                child(number).
                child(AppConstants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e(">>>>>>>>>>>>>","Done");

                String name_in_database = snapshot.child("name").getValue().toString();
                list.get(0).setTextView(name_in_database);
                list.get(0).setNumber(number);




                profileAdapter.notifyDataSetChanged();
//
                hideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideLoading();
            }
        });


        SettingsListener listener = new SettingsListener() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        openFragment(new ChangeNameFragment());
                        break;
                    case 1:
                        openFragment(new ChangePassFragment());
                        break;
                    case 2:
                        showChangeLanguageDialog();
                        break;
                    case 3:
                        openActivity(CurrentUserPostsActivity.class);
                        break;
                    case 4:
                        showDonate();
                        break;
                }
            }
        };

        profileAdapter = new ProfileAdapter(list, listener, getContext());
        profile_rec.setAdapter(profileAdapter);
        profile_rec.setLayoutManager(new LinearLayoutManager(getContext()));


        //Sign out - start
        btn_sign_out = (Button) view.findViewById(R.id.sign_out_btn);
        btn_sign_out.setOnClickListener(v -> {
            String s = sharedPrefs.getToken();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("USERS").child(sharedPrefs.getPhoneNumber()).child("Status");

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", "offline");
            sharedPrefs.clear();
            sharedPrefs.setToken(s);
            reference.updateChildren(hashMap);
            Intent intent = new Intent(getContext(), SignInActivity.class);
            startActivity(intent);
            getActivity().finish();

        });

        //Sign out - end


    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"Қазақша", "Русский", "English"};
        AlertDialog.Builder Builder = new AlertDialog.Builder(requireContext());
        Builder.setTitle(R.string.lan_change_title);
        SharedPreferences preferences = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
        int item_selected = preferences.getInt("CheckedItem", 2);
        Builder.setSingleChoiceItems(listItems, item_selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setLocale("kk", 0);
                    getActivity().recreate();
                } else if (which == 1) {
                    setLocale("ru", 1);
                    getActivity().recreate();
                } else if (which == 2) {
                    setLocale("en", 2);
                    getActivity().recreate();
                }
                dialog.dismiss();
            }
        });
        AlertDialog Dialog = Builder.create();
        Dialog.show();

        Dialog.getWindow().setLayout(MATCH_PARENT,
                1000);
        Dialog.getWindow().getAttributes().windowAnimations = R.style.animation_dial;
        Dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void setLocale(String lang, int item_selected) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getResources().updateConfiguration(configuration,
                getActivity().getResources().getDisplayMetrics());
        //Save data to shared preferences
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My Languages", lang);
        editor.putInt("CheckedItem", item_selected);
        editor.apply();
    }

    public void LoadLocale() {
        SharedPreferences preferences = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
        String language = preferences.getString("My Languages", "");
        int item_selected = preferences.getInt("CheckedItem", 2);
        setLocale(language, item_selected);
    }

    public void showDonate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setView(R.layout.donate_layout);
        AlertDialog alert=builder.create();
        alert.getWindow().setLayout(WRAP_CONTENT,
                WRAP_CONTENT);
//        alert.getWindow().setLayout(MATCH_PARENT,
//                500);
        alert.show();
    }

}