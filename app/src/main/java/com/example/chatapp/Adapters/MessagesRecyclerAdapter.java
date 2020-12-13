package com.example.chatapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Activities.SplashActivity;
import com.example.chatapp.Cons;
import com.example.chatapp.Models.Friend;
import com.example.chatapp.Models.FriendRequest;
import com.example.chatapp.Models.Message;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesViewHolder> {
    private Context context;
    private List<Message> messages;
    private User receiverUser;


    public MessagesRecyclerAdapter(Context context, List<Message> messages, User receiverUser) {
        this.context = context;
        this.messages = messages;
        this.receiverUser = receiverUser;
    }

    public void setReceiverUser(User receiverUser) {
        this.receiverUser = receiverUser;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MessagesViewHolder(LayoutInflater.from(context).inflate(R.layout.message_costum_design, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder messagesViewHolder, int position) {
        final Message message = messages.get(position);
        if (message.isSender()) {
            messagesViewHolder.textViewSentMsgDate.setText(message.getDate());
            messagesViewHolder.textViewSentMsgDate.setVisibility(View.GONE);
            messagesViewHolder.textViewSentMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (messagesViewHolder.textViewSentMsgDate.getVisibility() == View.VISIBLE) {
                        messagesViewHolder.textViewSentMsgDate.setVisibility(View.GONE);
                    } else {
                        messagesViewHolder.textViewSentMsgDate.setVisibility(View.VISIBLE);
                    }
                }
            });
            messagesViewHolder.textViewSentMsg.setVisibility(View.VISIBLE);
            messagesViewHolder.textViewReceivedMsg.setVisibility(View.GONE);
            messagesViewHolder.circleImageViewPersonalImage.setVisibility(View.GONE);

            messagesViewHolder.textViewSentMsg.setText(message.getContent());
        } else {
            if (receiverUser.isAccountActive())
                messagesViewHolder.imageViewActive.setVisibility(View.VISIBLE);
            else
                messagesViewHolder.imageViewActive.setVisibility(View.GONE);
            messagesViewHolder.textViewReceivedMsgDate.setText(message.getDate());
            messagesViewHolder.textViewReceivedMsgDate.setVisibility(View.GONE);
            messagesViewHolder.textViewReceivedMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (messagesViewHolder.textViewReceivedMsgDate.getVisibility() == View.VISIBLE) {
                        messagesViewHolder.textViewReceivedMsgDate.setVisibility(View.GONE);
                    } else {
                        messagesViewHolder.textViewReceivedMsgDate.setVisibility(View.VISIBLE);
                    }
                }
            });
            messagesViewHolder.textViewSentMsg.setVisibility(View.GONE);
            messagesViewHolder.textViewReceivedMsg.setVisibility(View.VISIBLE);
            messagesViewHolder.circleImageViewPersonalImage.setVisibility(View.VISIBLE);

            messagesViewHolder.textViewReceivedMsg.setText(message.getContent());
            if (receiverUser != null && receiverUser.getPersonalImage() != null)
                Picasso.get().load(receiverUser.getPersonalImage()).into(messagesViewHolder.circleImageViewPersonalImage);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}

class MessagesViewHolder extends RecyclerView.ViewHolder {
    CircleImageView circleImageViewPersonalImage;
    TextView textViewSentMsg;
    TextView textViewSentMsgDate;
    TextView textViewReceivedMsg;
    TextView textViewReceivedMsgDate;
    ImageView imageViewActive;


    public MessagesViewHolder(View view) {
        super(view);
        this.circleImageViewPersonalImage = view.findViewById(R.id.cmv_personal_image);
        this.textViewSentMsg = view.findViewById(R.id.text_view_sent_msg);
        this.textViewReceivedMsg = view.findViewById(R.id.text_view_received_msg);
        this.textViewSentMsgDate = view.findViewById(R.id.text_view_sent_msg_date);
        this.textViewReceivedMsgDate = view.findViewById(R.id.text_view_received_msg_date);
        this.imageViewActive = view.findViewById(R.id.image_view_active);
    }
}

