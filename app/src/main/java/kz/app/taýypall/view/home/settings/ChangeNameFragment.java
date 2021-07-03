package kz.app.taýypall.view.home.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import kz.app.taýypall.R;
import kz.app.taýypall.data.AppConstants;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.view.home.HomeActivity;

public class ChangeNameFragment extends Fragment {

    public final static String ARG_NAME = "ARG_NAME";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_name, container, false);
    }

    EditText name, surname, birth;
    TextView iz,change_photo_txt;
    DatabaseReference ref, reference2;
    String edited_full_name;
    Button btn;
    ImageView back_left_arrow;
    CircularImageView profile_image;
    public Uri imageUri;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private String edited_name, edited_name2;
    SharedPrefsHelper sharedPrefs;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = getView().findViewById(R.id.editName);
        surname = getView().findViewById(R.id.editSurn);
        iz = getView().findViewById(R.id.toolbar_title);
        btn = getView().findViewById(R.id.button);
        profile_image = getView().findViewById(R.id.profile_photo);
        back_left_arrow = getView().findViewById(R.id.back_arrow_left);


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        birth = getView().findViewById(R.id.editBirth);

        ref = FirebaseDatabase.getInstance().getReference();
        sharedPrefs = new SharedPrefsHelper(getContext());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        back_left_arrow.setVisibility(View.VISIBLE);

        back_left_arrow.setOnClickListener(v -> {
            onBackPressed();
        });


        iz.setText(R.string.change_name);
        reference2 = FirebaseDatabase.getInstance().getReference("USERS");
        String number = sharedPrefs.getPhoneNumber();


        reference2.child(number).child(AppConstants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name_in_db = snapshot.child("name").getValue().toString();
                String surname_in_edit = name_in_db.split(" ")[name_in_db.split(" ").length - 1];
                String name_in_edit = name_in_db.substring(0, name_in_db.length() - surname_in_edit.length());
                name.setText(name_in_edit);
                surname.setText(surname_in_edit);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storageReference.child("profile images")
                .child(number)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(profile_image);
                });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });


        change_photo_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });



        btn.setOnClickListener(v -> {

            name = getView().findViewById(R.id.editName);
            surname = getView().findViewById(R.id.editSurn);
            if (name.length() == 0 || name.length() < 2) {
                Toast.makeText(getContext(), R.string.name_length, Toast.LENGTH_LONG).show();
            } else if (surname.length() == 0 || surname.length() < 2) {
                Toast.makeText(getContext(), R.string.name_length, Toast.LENGTH_LONG).show();
            } else {

                ref.child(AppConstants.USERS).
                        child(number).
                        child(AppConstants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String upd_name = snapshot.child("name").getValue().toString();
                        edited_name = name.getText().toString();
                        edited_name2 = surname.getText().toString();
                        edited_full_name = edited_name + " " + edited_name2;
                        upd_name = (edited_full_name);


                        //ProfileItem
                        //UserItem item = new UserItem(upd_name)


                        ref.child("USERS")
                                .child(number)
                                .child(AppConstants.INFO)
                                .child("name")
                                .setValue(upd_name);

                        ((HomeActivity) getActivity()).openFragment(new SettingsFragment());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
    });
}


    public static ChangeNameFragment newInstance(String s) {

        Bundle args = new Bundle();
        args.putString(ARG_NAME, s);

        ChangeNameFragment fragment = new ChangeNameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onBackPressed() {
        super.getActivity().onBackPressed();
        ((HomeActivity) getActivity()).openFragment(new SettingsFragment());


    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profile_image.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Uploading Image...");
        pd.show();
        String number = sharedPrefs.getPhoneNumber();
        StorageReference riversRef = storageReference.child("profile images/" + number);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Image Uploaded",
                                Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(getContext(), "Failed to Upload", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Uploading " + (int) progressPercent + "%");
            }
        });

    }


}
