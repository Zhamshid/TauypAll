package kz.app.taýypall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import kz.app.taýypall.R;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.model.RatingItem;


public class RatingCommentViewAdapter extends RecyclerView.Adapter<RatingCommentViewAdapter.ViewHolder> {

    private Context mContext;
    private List<RatingItem> rat1 ;
    SharedPrefsHelper sharedPrefs ;


    public RatingCommentViewAdapter(Context context, List<RatingItem> rat1 ) {
        this.rat1 = rat1;
        this.mContext = context;
    }



    @NonNull
    @Override
    public RatingCommentViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_rating_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingCommentViewAdapter.ViewHolder holder, int position) {


        RatingItem rat = rat1.get(position);
        sharedPrefs = new SharedPrefsHelper(mContext);

        holder.user.setText(rat.getUser());
        holder.star.setText(rat.getRating());
        holder.comment.setText(rat.getComment());
        StorageReference str = FirebaseStorage.getInstance().getReference().child("profile images").child(rat.getUser());
        str.getDownloadUrl().addOnSuccessListener(downloadUrl -> Picasso.get().load(downloadUrl).into(holder.commentProfImg));




    }

    @Override
    public int getItemCount() {
        return rat1.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView star,comment,user;
        public ImageView commentProfImg;


        public ViewHolder(View itemView){
            super(itemView);

            user = itemView.findViewById(R.id.comment_user);
            star = itemView.findViewById(R.id.view_star);
            comment = itemView.findViewById(R.id.comment_rat);
            commentProfImg = itemView.findViewById(R.id.comment_prof_img);

        }
    }


}
