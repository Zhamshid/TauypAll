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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kz.app.taýypall.R;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.listener.SettingsListener;
import kz.app.taýypall.model.ProfileItem;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ProfileItem> list;
    SettingsListener listener;
    SharedPrefsHelper sharedPrefs;
    private Context context;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    public ProfileAdapter(ArrayList<ProfileItem> list, SettingsListener listener, Context context) {
        this.list = list;
        this.listener = listener;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_rec_items,
                    parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_rec_item_2,
                    parent, false);

            return new ViewHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        sharedPrefs = new SharedPrefsHelper(context);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        String number = sharedPrefs.getPhoneNumber();
        if (position == 0) {
            ViewHolder holder1 = (ViewHolder) holder;
            storageReference.child("profile images")
                    .child(number)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Picasso.get().load(uri).into(holder1.icons);
                    });
            ProfileItem item = list.get(position);
            holder1.icons.setImageResource(item.getIcons());
            holder1.textView.setText(item.getTextView());
            holder1.phone_num.setText(item.getNumber());
            holder1.edit_icon.setImageResource(item.getEdit_icon());
            ((ViewHolder) holder).edit_icon.setOnClickListener(v -> listener.onClick(position));
            ((ViewHolder) holder).icons.setOnClickListener(v -> listener.onClick(position));

        } else {
            ViewHolder2 holder2 = (ViewHolder2) holder;
            ProfileItem item = list.get(position);
            holder2.icon_2.setImageResource(item.getIcon_2());
            holder2.textView2.setText(Integer.parseInt(""+item.getTextView2()));
            holder2.itemView.setOnClickListener(v -> listener.onClick(position));
        }

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //ImageView icons;
        CircularImageView icons;
        ImageView edit_icon;
        TextView textView;
        TextView phone_num;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            icons = (CircularImageView) itemView.findViewById(R.id.icons);
            edit_icon = itemView.findViewById(R.id.icon_edit);
            textView = itemView.findViewById(R.id.text1);
            phone_num = itemView.findViewById(R.id.phone_number);


        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {
        ImageView icon_2;
        TextView textView2;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);

            icon_2 = (ImageView) itemView.findViewById(R.id.icon_in_item_2);
            textView2 = itemView.findViewById(R.id.text1_1);


        }
    }

}
