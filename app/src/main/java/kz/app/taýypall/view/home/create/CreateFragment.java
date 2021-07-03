package kz.app.taýypall.view.home.create;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import kz.app.taýypall.R;
import kz.app.taýypall.common.BaseFragment;
import kz.app.taýypall.data.AppConstants;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.model.ProductItem;
import kz.app.taýypall.view.home.homefragment.HomeFragment;

import static android.app.Activity.RESULT_OK;


public class CreateFragment extends BaseFragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText zogolovok;
    private EditText baga;
    private EditText opisanie;
    private EditText Phone;
    private EditText City;
    private TextView toolbar;
    private Button saveButton;
    private ImageButton imgBtn;
    SharedPrefsHelper sharedPrefs;
    private Uri imageUri;

    FirebaseDatabase rootNode;
    DatabaseReference reference, reference2;
    StorageReference storageReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        zogolovok = getView().findViewById(R.id.zagalovok);
        baga = getView().findViewById(R.id.baga);
        opisanie = getView().findViewById(R.id.opisanie);
        Phone = getView().findViewById(R.id.Phone);
        saveButton = getView().findViewById(R.id.saveButton);
        City = getView().findViewById(R.id.City);
        imgBtn = getView().findViewById(R.id.imageButton);
        toolbar = getView().findViewById(R.id.toolbar_title);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        toolbar.setText(R.string.create_pub);

        sharedPrefs = new SharedPrefsHelper(getContext());

        String number = sharedPrefs.getPhoneNumber();
        reference2 = FirebaseDatabase.getInstance().getReference("USERS");

        reference2.child(number).child(AppConstants.INFO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //String phone_number = snapshot.child("phone").getValue().toString();
                // String name = snapshot.child("name").getValue().toString();
                String city_name = snapshot.child("city").getValue().toString();
                City.setText(city_name);
                Phone.setText(number);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Zogolovok = zogolovok.getText().toString();
                String Baga = baga.getText().toString();
                String Opisanie = opisanie.getText().toString();
                String phone = Phone.getText().toString();
                if (TextUtils.isEmpty(Zogolovok) || TextUtils.isEmpty(Baga) ||
                        TextUtils.isEmpty(Opisanie) ||
                        TextUtils.isEmpty(phone)) {
                    Toast.makeText(getContext(), R.string.put_all_lines, Toast.LENGTH_SHORT).show();
                } else if (opisanie.length() < 25) {
                    showMessage(R.string.desc_lenght);
                } else if (imageUri == null) {
                    showMessage(R.string.please_upload_image);
                } else {
                    uploadImage();
                }
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        //startActivityForResult(Intent.createChooser(intent, "Select Image(s)"),PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            //Toast.makeText(getContext(), "image alindi", Toast.LENGTH_LONG).show();
            imageUri = data.getData();
            Picasso.get().load(imageUri);
        }
    }

    private void uploadImage() {
        String Zogolovok = zogolovok.getText().toString();
        String Baga = baga.getText().toString();
        String Opisanie = opisanie.getText().toString();
        String phone = Phone.getText().toString();
        String city = City.getText().toString();
        String phoneSharedP = sharedPrefs.getPhoneNumber();
        reference = FirebaseDatabase.getInstance().getReference("PRODUCT");

        String time = Zogolovok + "-" + System.currentTimeMillis();

        ProductItem helperClass = new ProductItem(Zogolovok, Baga, Opisanie, phone, city, phoneSharedP, time);
        reference.child(time).child("ProductInfo").setValue(helperClass);

        zogolovok.setText("");
        baga.setText("");
        opisanie.setText("");
        Phone.setText("");
        City.setText("");


        storageReference = FirebaseStorage.getInstance().getReference().child("Images");
        ProgressDialog progressDialog
                = new ProgressDialog(getContext());
        progressDialog.setTitle(R.string.uploading);
        progressDialog.show();

        // adding listeners on upload
        // or failure of image
        storageReference.child(time).putFile(imageUri)
                .addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(
                                    UploadTask.TaskSnapshot taskSnapshot) {
                                // Image uploaded successfully
                                // Dismiss dialog



                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Post Salindi!!", Toast.LENGTH_SHORT)
                                        .show();
                                try {
                                    openFragment(new HomeFragment());
                                }catch (Exception e){}


                            }
                        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast
                                .makeText(getContext(),
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .addOnProgressListener(
                        new OnProgressListener<UploadTask.TaskSnapshot>() {

                            // Progress Listener for loading
                            // percentage on the dialog box
                            @Override
                            public void onProgress(
                                    UploadTask.TaskSnapshot taskSnapshot) {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(
                                        "Uploaded "
                                                + (int) progress + "%");
                            }
                        });
    }
}
