package kz.app.taýypall.view.home.homefragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kz.app.taýypall.R;
import kz.app.taýypall.adapter.HomeReadPosts;
import kz.app.taýypall.model.ProductGetItem;
import kz.app.taýypall.model.ProductItem;

public class HomeFragment extends Fragment  {
    private RecyclerView recyclerView,recyclerviewSearch;
    private HomeReadPosts homeReadPosts;
    private List<ProductGetItem> post;
    private List<ProductItem> post1;


    int m = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container,false);
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        post = new ArrayList<>();
        post1 = new ArrayList<>();


        TextView search_bar = view.findViewById(R.id.search_bar);
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchUsers(String s){
        Query query = FirebaseDatabase.getInstance().getReference().child("PRODUCT")
                .orderByKey().startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post1.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren() ){
                    ProductItem post2 = snapshot1.getValue(ProductItem.class);
                    post1.add(post2);
                }
                homeReadPosts.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                readPosts();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        recyclerView = getView().findViewById(R.id.recycler_view_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        readPosts();
    }

    private void readPosts(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PRODUCT");
        StorageReference str = FirebaseStorage.getInstance().getReference().child("Images");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post.clear();

                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    ProductGetItem posts = snapshot1.getValue(ProductGetItem.class);
                    post.add(posts);
                }
                Collections.reverse(post);
                homeReadPosts = new HomeReadPosts(getContext(),post,str,m);
                recyclerView.setAdapter(homeReadPosts);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}