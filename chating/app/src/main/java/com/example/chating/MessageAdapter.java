package com.example.chating;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        TextView senderTextView = (TextView) listItemView.findViewById(R.id.sender_inList);
        TextView recieverTextView = (TextView) listItemView.findViewById(R.id.receiver_inList);
        ImageView seenIcon = (ImageView)listItemView.findViewById(R.id.seenIcon);


        senderTextView.setText(currentMesage.getSenderMessage());
        recieverTextView.setText(currentMesage.getReceiverMessage());

        if(currentMesage.getMessageType().equals("R")){
            sendContainer.setVisibility(View.GONE);
            recieverTextView.setVisibility(View.VISIBLE);
        }
        else if(currentMesage.getMessageType().equals("S")){
            recieverTextView.setVisibility(View.GONE);
            sendContainer.setVisibility(View.VISIBLE);
        }


        if(currentMesage.getSeenStateNum() == 1)seenIcon.setImageResource(R.drawable.sent1);
        else if(currentMesage.getSeenStateNum() == 2)seenIcon.setImageResource(R.drawable.sent2);
        else if(currentMesage.getSeenStateNum() == 3)seenIcon.setImageResource(R.drawable.seen);


        return listItemView;
    }



}
