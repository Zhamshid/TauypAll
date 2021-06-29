package kz.app.taýypall.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kz.app.taýypall.R;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.model.Chat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private  String imageurl;
    SharedPrefsHelper sharedPrefs ;


    public MessageAdapter(Context context, List<Chat> mChat, String imageurl) {
        this.mChat = mChat;
        this.mContext = context;
        this.imageurl = imageurl;
        sharedPrefs = new SharedPrefsHelper(context);
    }



    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);}
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());



    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;

        public ViewHolder(View itemView){
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);

        }
    }



    @Override
    public int getItemViewType(int position) {
        Log.d("******", "getItemViewType: " + sharedPrefs.getPhoneNumber() );
        if(mChat.get(position).getSender().equals(sharedPrefs.getPhoneNumber())){
            return MSG_TYPE_RIGHT;
        }else
        {return MSG_TYPE_LEFT;}
    }
}

