package kz.app.taýypall.view.home.messages;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.HashMap;
import java.util.List;

//import de.hdodenhof.circleimageview.CircleImageView;
import kz.app.taýypall.R;
import kz.app.taýypall.adapter.MessageAdapter;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.model.Chat;
import kz.app.taýypall.notification.Client;
import kz.app.taýypall.notification.Date;
import kz.app.taýypall.notification.MyResposnse;
import kz.app.taýypall.notification.Sender;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagePage extends AppCompatActivity {

    CircularImageView profile_image_toolbar;
    TextView username_toolbar;
    ImageButton btn_send;
    EditText text_send;
    DatabaseReference ref;
    SharedPrefsHelper sharedPrefs ;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;
    String imageurl;
    String mPostName, phone;

    APIService apiService;
    String userid;

    boolean notify = false;

    Intent intent;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_page);

        NotificationManager mManager = (NotificationManager)
                MessagePage.this.getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.cancelAll();

        String userName = getIntent().getStringExtra("user");
        Intent intent = getIntent();
        mPostName = intent.getStringExtra("postName");
        phone = intent.getStringExtra("phone");
        sharedPrefs = new SharedPrefsHelper(this);

        Toolbar toolbar_m = findViewById(R.id.toolbar_m);
        setSupportActionBar(toolbar_m);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_m.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);


        recyclerView = findViewById(R.id.send_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image_toolbar = findViewById(R.id.image_profile_toolbar);
        username_toolbar = findViewById(R.id.username_toolbar);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        userid = intent.getStringExtra("userid");


        username_toolbar.setText(phone);
        StorageReference str = FirebaseStorage.getInstance().getReference().child("profile images").child(phone);
        str.getDownloadUrl().addOnSuccessListener(downloadUrl -> Picasso.get().load(downloadUrl).into(profile_image_toolbar));

        readMessage(sharedPrefs.getPhoneNumber(),phone,imageurl);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(sharedPrefs.getPhoneNumber(), phone ,msg,mPostName);
                }else{
                    Toast.makeText(MessagePage.this,"You cannot enter message!",Toast.LENGTH_LONG).show();
                }
                text_send.setText("");
            }
        });

    }

    private void sendMessage (String sende, String receiver, String message,String mPostName){

      /*  Sender sender = new Sender(new Date("user", "body" , "title" , "sender"), "faeI6jrtQvWQnv23wXSql0:APA91bEVKKh4hrpRBBusH2n05rj6UUm5yA98V-aLTgU8Jx64dsmG06E2rJp4wPIL0ru3N1syolkurezTF0W1EIkXML78WSTdGmplxMIBuflOw_fupnyJwfaw-NzhJWxGxV13Ql9lLFQ9");

        apiService.sendNotification(sender)
                .enqueue(new Callback<MyResposnse>() {
                    @Override
                    public void onResponse(Call<MyResposnse> call, Response<MyResposnse> response) {
                        if (response.code() == 200){
                            if (response.body().success !=1){
                                Toast.makeText(MessagePage.this, "failed",Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResposnse> call, Throwable t) {

                    }
                });*/



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sende);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("postName", mPostName);

        reference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("USERS")
                .child(receiver).child("INFO").child("token");

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    Date data = new Date(sende,message,sende,sende);

                    Log.d("++++++++++++++++", snapshot.getValue(String.class));
                    Sender sender = new Sender(data, snapshot.getValue(String.class));
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResposnse>() {
                                @Override
                                public void onResponse(Call<MyResposnse> call, Response<MyResposnse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success !=1){
                                            Toast.makeText(MessagePage.this, "failed",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResposnse> call, Throwable t) {

                                }
                            });


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }


    private void readMessage(final String myid, final String userid, final String imageurl){
        mChat = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mChat.add(chat);
                    }
                    NotificationManager mManager = (NotificationManager)
                            MessagePage.this.getSystemService(Context.NOTIFICATION_SERVICE);
                    mManager.cancelAll();
                    messageAdapter = new MessageAdapter(MessagePage.this,mChat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void status(String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("USERS").child(sharedPrefs.getPhoneNumber()).child("Status");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }


    @Override
    protected void onResume(){
        super.onResume();

        status("online");
    }

    @Override
    protected void onPause(){
        super.onPause();
        status("offline");
    }

}
