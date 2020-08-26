package com.example.chatting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Activity context, ArrayList<Message> androidFlavors) {
         super(context, 0, androidFlavors);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.message_list, parent, false);
        }

        Message currentMesage = getItem(position);


        LinearLayout sendContainer = (LinearLayout)listItemView.findViewById(R.id.SContainer);
        LinearLayout ReceiveContainer = (LinearLayout)listItemView.findViewById(R.id.RContainer);

        TextView senderTextView = (TextView) listItemView.findViewById(R.id.sender_inList);
        ImageView senderImage = (ImageView)listItemView.findViewById(R.id.sender_Image);
        ImageView Icona = (ImageView)listItemView.findViewById(R.id.icona);
        TextView receiverTextView = (TextView) listItemView.findViewById(R.id.receiver_inList);
        ImageView ReceiveImage = (ImageView)listItemView.findViewById(R.id.Receiver_Image);
        ImageView seenIcon = (ImageView)listItemView.findViewById(R.id.seenIcon);


        if(currentMesage.getMessageType().equals("R")){
            sendContainer.setVisibility(View.GONE);
            senderTextView.setVisibility(View.GONE);
            senderImage.setVisibility(View.GONE);
            seenIcon.setVisibility(View.GONE);

            ReceiveContainer.setVisibility(View.VISIBLE);
            if(currentMesage.isImage()){
                Picasso.get().load(currentMesage.getReceiverMessage()).into(ReceiveImage);
                ReceiveImage.setVisibility(View.VISIBLE);
                receiverTextView.setVisibility(View.GONE);
                Icona.setVisibility(View.VISIBLE);
            }
            else {
                receiverTextView.setText(currentMesage.getReceiverMessage());
                receiverTextView.setVisibility(View.VISIBLE);
                ReceiveImage.setVisibility(View.GONE);
                Icona.setVisibility(View.GONE);
            }
        }



        else if(currentMesage.getMessageType().equals("S")){
            ReceiveContainer.setVisibility(View.GONE);
            receiverTextView.setVisibility(View.GONE);
            ReceiveImage.setVisibility(View.GONE);
            Icona.setVisibility(View.GONE);

            sendContainer.setVisibility(View.VISIBLE);
            if(currentMesage.isImage()){
                Picasso.get().load(currentMesage.getSenderMessage()).into(senderImage);
                senderImage.setVisibility(View.VISIBLE);
                senderTextView.setVisibility(View.GONE);
            }
            else {
                senderTextView.setText(currentMesage.getSenderMessage());
                senderTextView.setVisibility(View.VISIBLE);
                senderImage.setVisibility(View.GONE);
            }

            seenIcon.setVisibility(View.VISIBLE);
            if(currentMesage.getSeenStateNum() == 1)seenIcon.setImageResource(R.drawable.sent1);
            else if(currentMesage.getSeenStateNum() == 2)seenIcon.setImageResource(R.drawable.sent2);
            else if(currentMesage.getSeenStateNum() == 3)seenIcon.setImageResource(R.drawable.seen);

        }





        return listItemView;
    }



}
