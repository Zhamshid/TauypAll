package kz.app.taýypall.view.home.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kz.app.taýypall.R;
import kz.app.taýypall.adapter.HomeReadPosts;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.model.ProductGetItem;

public class CurrentUserPostsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HomeReadPosts homeReadPosts;
    String mPostName;
    private List<ProductGetItem> post;
    int m = 1;
    SharedPrefsHelper sharedPrefs;
    String phone_number;
    NestedScrollView nestedScrollView;
    TextView toolbar_title;
    ImageView back_left_arrow;

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView = findViewById(R.id.recycler_view_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
        post = new ArrayList<>();
        nestedScrollView = findViewById(R.id.nes_scr_view);
        readPosts();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_posts);
        toolbar_title = findViewById(R.id.toolbar_title);
        sharedPrefs = new SharedPrefsHelper(getBaseContext());
        //String name_of_user = getActivity().getIntent().getExtras().getString("name_of_user");
        String number = sharedPrefs.getPhoneNumber();
        toolbar_title.setText(R.string.my_publish);


        back_left_arrow = findViewById(R.id.back_arrow_left);
        back_left_arrow.setVisibility(View.VISIBLE);

        back_left_arrow.setOnClickListener(v -> {
            onBackPressed();
        });


    }

    private void readPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PRODUCT");
        StorageReference str = FirebaseStorage.getInstance().getReference().child("Images");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String number = sharedPrefs.getPhoneNumber();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ProductGetItem posts = snapshot1.getValue(ProductGetItem.class);
                    if (posts.getProductInfo().getSharedPrefPhone().equals(sharedPrefs.getPhoneNumber())) {
                        post.add(posts);
                    }

                }
                if (post.size() == 0) {
                    nestedScrollView.setVisibility(View.GONE);
                } else {
                    Collections.reverse(post);
                    homeReadPosts = new HomeReadPosts(getBaseContext(), post, str, m);
                    recyclerView.setAdapter(homeReadPosts);

                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();

    }

}