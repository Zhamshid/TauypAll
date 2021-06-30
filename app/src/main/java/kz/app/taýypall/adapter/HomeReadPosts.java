package kz.app.taýypall.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import kz.app.taýypall.R;
import kz.app.taýypall.model.ProductGetItem;
import kz.app.taýypall.view.home.homefragment.PoatDetails.PostDetailsActivity;

public class HomeReadPosts  extends RecyclerView.Adapter<HomeReadPosts.ViewHolder> {
    private Context mContex;
    private List<ProductGetItem>  post;
    private StorageReference str;
    Integer m ;
    public HomeReadPosts(Context context, List<ProductGetItem> post, StorageReference str, Integer m){
        this.post = post;
        this.mContex = context;
        this.str = str;
        this.m = m;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (m ==1){ View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
            return new HomeReadPosts.ViewHolder(view);}
        else{View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_post_item, parent, false);
            return new HomeReadPosts.ViewHolder(view);}
    }

    @Override
    public void onBindViewHolder(@NonNull HomeReadPosts.ViewHolder holder, int position){
        ProductGetItem post1 = post.get(position);
        holder.postName.setText(post1.getProductInfo().getZogolovok());
        holder.cost.setText(post1.getProductInfo().getBaga());
        holder.City.setText(post1.getProductInfo().getCity());

        StorageReference str1 = FirebaseStorage.getInstance().getReference().child("Images");
        str.child(post1.getProductInfo().getStorageName()).getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(holder.postImage);
        });

        holder.clickCardView.setOnClickListener(v -> {
            Intent intent = new Intent(mContex, PostDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("postName", post1.getProductInfo().getStorageName());
            intent.putExtra("phone",post1.getProductInfo().getSharedPrefPhone());
            intent.putExtra("city",post1.getProductInfo().getCity());
            intent.putExtra("cost",post1.getProductInfo().getBaga());
            intent.putExtra("opisanie",post1.getProductInfo().getOpisanie());
            intent.putExtra("zogolovok",post1.getProductInfo().getZogolovok());
            mContex.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return post.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView postName;
        public TextView City;
        public TextView cost;
        public ImageView postImage;
        public CardView clickCardView;
        RelativeLayout parentLayot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.image_post);
            postName = itemView.findViewById(R.id.image_name);
            cost = itemView.findViewById(R.id.image_cost);
            City = itemView.findViewById(R.id.image_city);
            clickCardView = itemView.findViewById(R.id.cardViewClick);
            parentLayot = itemView.findViewById(R.id.parent_layot);





        }
    }

}
