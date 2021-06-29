package kz.app.taýypall.view.home.favorites;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class FavortiresFragment extends Fragment {
    private TextView toolbar;
    int m = 0;
    SharedPrefsHelper sharedPrefs;
    private List<ProductGetItem> post;
    private RecyclerView recyclerView;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favortires, container, false);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        }

    @Override
    public void onResume() {
        super.onResume();
        toolbar = getView().findViewById(R.id.toolbar_title);
        toolbar.setText(R.string.favorite);
        sharedPrefs = new SharedPrefsHelper(getContext());
        recyclerView = getView().findViewById(R.id.recycler_view_like);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        post = new ArrayList<>();
        readLikedPosts();
    }

    private void readLikedPosts(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("USERS").child(sharedPrefs.getPhoneNumber()).child("LikedPosts");
        StorageReference str = FirebaseStorage.getInstance().getReference().child("Images");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapshot1:snapshot.getChildren()){

                    ProductGetItem posts = snapshot1.getValue(ProductGetItem.class);
                    post.add(posts);
                }
                Collections.reverse(post);
                HomeReadPosts homeReadPosts = new HomeReadPosts(getContext(),post,str,m);
                recyclerView.setAdapter(homeReadPosts);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}