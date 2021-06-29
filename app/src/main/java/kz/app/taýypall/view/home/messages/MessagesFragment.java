package kz.app.taýypall.view.home.messages;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import kz.app.taýypall.R;
import kz.app.taýypall.adapter.MessageUserAdapter;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.model.Chat;
import kz.app.taýypall.model.UserGetItem;

public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;

    private MessageUserAdapter userAdapter;
    private List<UserGetItem> mUser;

    DatabaseReference reference;

    private List<String> userslist;

    SharedPrefsHelper sharedPrefs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userslist = new ArrayList<>();
        sharedPrefs = new SharedPrefsHelper(getContext());

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userslist.clear();
                for (DataSnapshot snapshot1 :snapshot.getChildren()) {
                    Chat chat = snapshot1.getValue(Chat.class);

                    if(chat.getSender().equals(sharedPrefs.getPhoneNumber())){
                        userslist.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(sharedPrefs.getPhoneNumber())){
                        userslist.add(chat.getSender());
                    }
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }


    private void readChats(){
        mUser = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("USERS");
        StorageReference str = FirebaseStorage.getInstance().getReference().child("profile images");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    UserGetItem user = snapshot1.getValue(UserGetItem.class);
                    try {

                    if (userslist.contains(user.getInfo().getPhone())){
                        mUser.add(user);
                    }}catch (Exception e){}


                }
                Log.d("++++++",mUser.size()+"");
                userAdapter = new MessageUserAdapter(getContext(),mUser,str);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}