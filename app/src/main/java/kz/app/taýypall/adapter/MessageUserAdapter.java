package kz.app.taýypall.adapter;

import android.content.Context;
import android.content.Intent;
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
import kz.app.taýypall.model.UserGetItem;
import kz.app.taýypall.view.home.messages.MessagePage;

public class MessageUserAdapter extends RecyclerView.Adapter<MessageUserAdapter.ViewHolder> {

    private Context mContext;
    private List<UserGetItem> mUser;
    SharedPrefsHelper sharedPrefs ;
    private StorageReference str;


    public MessageUserAdapter(Context context, List<UserGetItem> mUser, StorageReference str) {
        this.mUser = mUser;
        this.mContext = context;
        this.str = str;
    }



    @NonNull
    @Override
    public MessageUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_message, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageUserAdapter.ViewHolder holder, int position) {

        UserGetItem user = mUser.get(position);
        sharedPrefs = new SharedPrefsHelper(mContext);

        holder.phone.setText(user.getInfo().getPhone());
        holder.user_name.setText(user.getInfo().getName());

        StorageReference str = FirebaseStorage.getInstance().getReference().child("profile images").child(user.getInfo().getPhone());
        str.getDownloadUrl().addOnSuccessListener(downloadUrl -> Picasso.get().load(downloadUrl).into(holder.profile_image));



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessagePage.class);
                intent.putExtra("phone", user.getInfo().getPhone());
                intent.putExtra("postName", user.getInfo().getPhone());

                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView phone,user_name;
        public ImageView profile_image;


        public ViewHolder(View itemView){
            super(itemView);

            phone = itemView.findViewById(R.id.username_message);
            user_name = itemView.findViewById(R.id.user_name);
            profile_image = itemView.findViewById(R.id.image_profile_message);

        }
    }


}
