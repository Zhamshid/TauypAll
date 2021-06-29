package kz.app.taýypall.view.home.homefragment.PoatDetails;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import kz.app.taýypall.R;
import kz.app.taýypall.adapter.RatingCommentViewAdapter;
import kz.app.taýypall.common.BaseActivity;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.model.ProductGetItem;
import kz.app.taýypall.model.ProductItem;
import kz.app.taýypall.model.RatingItem;
import kz.app.taýypall.model.UserItem;
import kz.app.taýypall.view.home.messages.MessagePage;

public class PostDetailsActivity extends BaseActivity {
    String mPostName, z1, b1, c1, o1;
    ImageView imageFoto, like, liked;
    String isLiked;
    TextView Name, Cost, City, Opisanie, Username, statusAvg, commetTotal,yourRt;
    String phone;
    CircularImageView profile_photo, sts_on, sts_off;
    SharedPrefsHelper sharedPrefs;
    Button calls, messages, edit, delete;
    ProductItem helperClass;
    LinearLayout del_edit, call_msg, rating, commentView;
    RatingBar ratingBar;
    EditText comment;
    private RecyclerView recyclerView;

    private List<RatingItem> rat1;

    private static final int REQUEST_CALL = 1;
    private BottomSheetDialog bottomSheetDialog;

    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_details);

        Intent intent = getIntent();
        mPostName = intent.getStringExtra("postName");
        phone = intent.getStringExtra("phone");
        z1 = intent.getStringExtra("zogolovok");
        b1 = intent.getStringExtra("cost");
        o1 = intent.getStringExtra("opisanie");
        c1 = intent.getStringExtra("city");
        imageFoto = findViewById(R.id.backdrop);
        Cost = findViewById(R.id.Costs);
        City = findViewById(R.id.city);
        profile_photo = findViewById(R.id.image_profile);
        Opisanie = findViewById(R.id.postOpisanie);
        Name = findViewById(R.id.NamePosts);
        Username = findViewById(R.id.Username);
        calls = findViewById(R.id.call);
        messages = findViewById(R.id.message1);
        edit = findViewById(R.id.edit);
        like = findViewById(R.id.like);
        sts_on = findViewById(R.id.on);
        sts_off = findViewById(R.id.off);
        rating = findViewById(R.id.rating);
        del_edit = findViewById(R.id.del_edit);
        call_msg = findViewById(R.id.call_msg);
        commentView = findViewById(R.id.commentView);
        statusAvg = findViewById(R.id.status_avg);
        commetTotal = findViewById(R.id.status_count);
        yourRt = findViewById(R.id.yourRt);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        sharedPrefs = new SharedPrefsHelper(getApplicationContext());

        rat1 = new ArrayList<>();
        //getSharedPrefPhone
        //String number = sharedPrefs.getPhoneNumber();
        storageReference.child("profile images")
                .child(phone)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(profile_photo);
                });


        if (phone.equals(sharedPrefs.getPhoneNumber())) {
            del_edit.setVisibility(View.VISIBLE);
            call_msg.setVisibility(View.GONE);
        }

        DatabaseReference status = FirebaseDatabase.getInstance().getReference("PRODUCT").child(mPostName).child("Rating");
        status.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int e = 0;
                float t = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    RatingItem rt = snapshot1.getValue(RatingItem.class);
                    t = t + Float.parseFloat(rt.getRating());
                    e = e + 1;
                    if(rt.getUser().equals(sharedPrefs.getPhoneNumber())){
                        yourRt.setText(rt.getRating());
                    }
                }
                if (e > 0) {
                    commetTotal.setText(String.valueOf(e));
                    float i = t / e;
                    statusAvg.setText(String.valueOf(i));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference checkLike = FirebaseDatabase.getInstance().getReference("USERS").child(sharedPrefs.getPhoneNumber()).child("LikedPosts").child(mPostName);
        checkLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    ImageView switch_likes = (ImageView) findViewById(R.id.like);
                    Drawable myDrawable = getResources().getDrawable(R.drawable.ic_fav_not_liked);
                    switch_likes.setImageDrawable(myDrawable);
                } else {
                    ImageView switch_likes = (ImageView) findViewById(R.id.like);
                    Drawable myDrawable = getResources().getDrawable(R.drawable.ic_fav_liked);
                    switch_likes.setImageDrawable(myDrawable);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        calls.setOnClickListener(v -> {
            if (phone.equals(sharedPrefs.getPhoneNumber())) {
                Toast.makeText(getApplicationContext(), "You can't call for you", Toast.LENGTH_LONG).show();
            } else {
                makePhoneCall();
            }
        });


        messages.setOnClickListener(v -> {
            if (phone.equals(sharedPrefs.getPhoneNumber())) {
                Toast.makeText(PostDetailsActivity.this, "Вы не можете себе отправить сообщение!", Toast.LENGTH_LONG).show();
            } else {
                Intent intent1 = new Intent(PostDetailsActivity.this, MessagePage.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("phone", phone);
                intent1.putExtra("postName", mPostName);
                startActivity(intent1);
                finish();
            }
        });
        helperClass = new ProductItem(z1, b1, o1, phone, c1, sharedPrefs.getPhoneNumber(), mPostName);

        like.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("USERS").child(sharedPrefs.getPhoneNumber()).child("LikedPosts").child(mPostName).child("ProductInfo");
            DatabaseReference refere = FirebaseDatabase.getInstance().getReference("USERS").child(sharedPrefs.getPhoneNumber()).child("LikedPosts");
//            ImageView switch_likes = (ImageView)findViewById(R.id.like);
//            Drawable myDrawable = getResources().getDrawable(ic_fav_liked);
//            switch_likes.setImageDrawable(myDrawable);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        ImageView switch_likes = (ImageView) findViewById(R.id.like);
                        Drawable myDrawable = getResources().getDrawable(R.drawable.ic_fav_liked);
                        switch_likes.setImageDrawable(myDrawable);
                        reference.setValue(helperClass);

                    } else {
                        ImageView switch_likes = (ImageView) findViewById(R.id.like);
                        Drawable myDrawable = getResources().getDrawable(R.drawable.ic_fav_not_liked);
                        switch_likes.setImageDrawable(myDrawable);
                        reference.setValue(null);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("USERS").child(phone).child("Status").child("status");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue(String.class).toString().equals("online")) {
                    sts_on.setVisibility(View.VISIBLE);
                    sts_off.setVisibility(View.GONE);
                } else {
                    sts_on.setVisibility(View.GONE);
                    sts_off.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("USERS").child(phone);
        ref1.child("INFO").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserItem n1 = snapshot.getValue(UserItem.class);
                Username.setText(n1.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        StorageReference str = FirebaseStorage.getInstance().getReference().child("Images");


        str.child(mPostName).getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(imageFoto);
        });


        rating.setOnClickListener(v -> {
            BottomSheet();
        });

        commentView.setOnClickListener(v -> {
            CommentView();
        });


    }

    private void BottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(PostDetailsActivity.this, R.style.BottomSheetTheme);

        View sheetview = LayoutInflater.from(getApplicationContext()).inflate(R.layout.rating_bar, null);
        bottomSheetDialog.setContentView(sheetview);

        comment = sheetview.findViewById(R.id.comment);
        ratingBar = sheetview.findViewById(R.id.ratingBar);

        sheetview.findViewById(R.id.cancel).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });


        sheetview.findViewById(R.id.save).setOnClickListener(v -> {
            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("PRODUCT").child(mPostName).child("Rating").child(sharedPrefs.getPhoneNumber());
            RatingItem helperClass = new RatingItem(String.valueOf(ratingBar.getRating()), comment.getText().toString(), sharedPrefs.getPhoneNumber().trim());
            reference2.setValue(helperClass);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(sheetview);
        bottomSheetDialog.show();
    }

    private void CommentView() {
        bottomSheetDialog = new BottomSheetDialog(PostDetailsActivity.this, R.style.BottomSheetTheme);

        View sheetview1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.comment_view_bottomsheet, null);
        bottomSheetDialog.setContentView(sheetview1);
        TextView t = sheetview1.findViewById(R.id.pustoi);

        recyclerView = sheetview1.findViewById(R.id.comment_view_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        DatabaseReference ref8 = FirebaseDatabase.getInstance().getReference("PRODUCT").child(mPostName).child("Rating");
        ref8.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                rat1.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    RatingItem ratingItem = snapshot1.getValue(RatingItem.class);
                    rat1.add(ratingItem);
                    if (rat1.size() == 0) {
                        t.setVisibility(View.VISIBLE);
                    } else {
                        t.setVisibility(View.GONE);
                        Collections.reverse(rat1);
                        RatingCommentViewAdapter r = new RatingCommentViewAdapter(getApplicationContext(), rat1);
                        recyclerView.setAdapter(r);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        bottomSheetDialog.setContentView(sheetview1);
        bottomSheetDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("PRODUCT").
                        child(mPostName).setValue(null);
                showMessage(R.string.post_deleted);
            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("PRODUCT").child(mPostName);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductGetItem pos = snapshot.getValue(ProductGetItem.class);
                Name.setText(pos.getProductInfo().getZogolovok());
                Cost.setText(pos.getProductInfo().getBaga());
                City.setText(pos.getProductInfo().getCity());
                Opisanie.setText(pos.getProductInfo().getOpisanie());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //Call Phone - start
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Call Phone - start


    //Make call - start
    private void makePhoneCall() {
        String number = phone;
        if (number.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(PostDetailsActivity.this, Manifest.permission.CALL_PHONE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PostDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_CALL);

            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }


        } else {
            //phone.equals(sharedPrefs.getPhoneNumber())
            Toast.makeText(getApplicationContext(), "Phone number is incorrect!", Toast.LENGTH_LONG).show();
        }
    }

    //Make call - end
    private void status(String status) {
        DatabaseReference reference5 = FirebaseDatabase.getInstance().getReference("USERS").child(sharedPrefs.getPhoneNumber()).child("Status");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference5.updateChildren(hashMap);
    }


    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
