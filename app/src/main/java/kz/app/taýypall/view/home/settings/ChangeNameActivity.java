package kz.app.taýypall.view.home.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import kz.app.taýypall.common.BaseActivity;
import kz.app.taýypall.data.AppConstants;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.view.home.HomeActivity;

public class ChangeNameActivity extends BaseActivity {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        name = findViewById(R.id.editName);
        surname = findViewById(R.id.editSurn);
        iz = findViewById(R.id.toolbar_title);
        btn = findViewById(R.id.button);
        profile_image = findViewById(R.id.profile_photo);
        change_photo_txt = findViewById(R.id.change_photo);
        back_left_arrow = findViewById(R.id.back_arrow_left);


        birth = findViewById(R.id.editBirth);

        ref = FirebaseDatabase.getInstance().getReference();
        sharedPrefs = new SharedPrefsHelper(getBaseContext());
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

        try{
            change_photo_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choosePicture();
                }
            });
        }catch (Exception e){}



        btn.setOnClickListener(v -> {

            name = findViewById(R.id.editName);
            surname = findViewById(R.id.editSurn);
            if (name.length() == 0 || name.length() < 2) {
                Toast.makeText(getBaseContext(), R.string.name_length, Toast.LENGTH_LONG).show();
            } else if (surname.length() == 0 || surname.length() < 2) {
                Toast.makeText(getBaseContext(), R.string.name_length, Toast.LENGTH_LONG).show();
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


                        ref.child("USERS")
                                .child(number)
                                .child(AppConstants.INFO)
                                .child("name")
                                .setValue(upd_name);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            openActivity(HomeActivity.class);
        });


    }


    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();


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
        final ProgressDialog pd = new ProgressDialog(getBaseContext());
        pd.setTitle("Uploading Image...");
        pd.show();
        String number = sharedPrefs.getPhoneNumber();
        StorageReference riversRef = storageReference.child("profile images/" + number);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded",
                                Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(getBaseContext(), "Failed to Upload", Toast.LENGTH_SHORT).show();
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