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
    //Sender xml
    private LinearLayout sendContainer;
    private TextView senderTextView;
    private ImageView senderImage;
    private LinearLayout SendRecordContainer;
    private TextView SendMessageTime;
    private ImageView seenIcon;
    //Receiver xml
    private LinearLayout ReceiveContainer;
    private TextView receiverTextView;
    private ImageView ReceiveImage;
    private LinearLayout ReceiveRecordContainer;
    private TextView ReceiveMessageTime;


    public MessageAdapter(Activity context, ArrayList<Message> androidFlavors) {
         super(context, 0, androidFlavors);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ListItemView = convertView;
        if(ListItemView == null) {
            ListItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.message_list, parent, false);
        }

        final Message currentMessage = getItem(position);

        //define xml components
        //Sender xml
        sendContainer = (LinearLayout)ListItemView.findViewById(R.id.SContainer);
        senderTextView = (TextView) ListItemView.findViewById(R.id.sender_inList);
        senderImage = (ImageView)ListItemView.findViewById(R.id.sender_Image);
        SendRecordContainer = (LinearLayout)ListItemView.findViewById(R.id.senderRecordContainerInList);
        SendMessageTime = (TextView)ListItemView.findViewById(R.id.SendMessageTime);
        seenIcon = (ImageView)ListItemView.findViewById(R.id.seenIcon);

        //Receiver xml
        ReceiveContainer = (LinearLayout)ListItemView.findViewById(R.id.RContainer);
        receiverTextView = (TextView) ListItemView.findViewById(R.id.receiver_inList);
        ReceiveImage = (ImageView)ListItemView.findViewById(R.id.Receiver_Image);
        ReceiveRecordContainer = (LinearLayout)ListItemView.findViewById(R.id.ReceiverRecordContainerInList);
        ReceiveMessageTime = (TextView)ListItemView.findViewById(R.id.ReceiveMessageTime);


        // Receiver Container
        if(!currentMessage.getIsSender()){
            sendContainer.setVisibility(View.GONE);
            senderTextView.setVisibility(View.GONE);
            senderImage.setVisibility(View.GONE);
            SendRecordContainer.setVisibility(View.GONE);
            SendMessageTime.setVisibility(View.GONE);
            seenIcon.setVisibility(View.GONE);


            ReceiveContainer.setVisibility(View.VISIBLE);
            ReceiveMessageTime.setText(currentMessage.getMessageTime());
            if(currentMessage.getMessageType().equals("Image")){
                Picasso.get().load(currentMessage.getReceiverMessage()).into(ReceiveImage);
                ReceiveImage.setVisibility(View.VISIBLE);
                receiverTextView.setVisibility(View.GONE);
            }
            else if(currentMessage.getMessageType().equals("Message")){
                receiverTextView.setText(currentMessage.getReceiverMessage());
                receiverTextView.setVisibility(View.VISIBLE);
                ReceiveImage.setVisibility(View.GONE);
            }
            else if(currentMessage.getMessageType().equals("Record")){
                ReceiveRecordContainer.setVisibility(View.VISIBLE);
                receiverTextView.setVisibility(View.GONE);
                ReceiveImage.setVisibility(View.GONE);
            }

        }


        // Sender Container
        else if(currentMessage.getIsSender()){
            ReceiveContainer.setVisibility(View.GONE);
            receiverTextView.setVisibility(View.GONE);
            ReceiveImage.setVisibility(View.GONE);
            ReceiveRecordContainer.setVisibility(View.GONE);
            ReceiveMessageTime.setVisibility(View.GONE);

            sendContainer.setVisibility(View.VISIBLE);
            SendMessageTime.setText(currentMessage.getMessageTime());
            if(currentMessage.getMessageType().equals("Image")){
                Picasso.get().load(currentMessage.getSenderMessage()).into(senderImage);
                senderImage.setVisibility(View.VISIBLE);
                senderTextView.setVisibility(View.GONE);
            }
            else if(currentMessage.getMessageType().equals("Message")){
                senderTextView.setText(currentMessage.getSenderMessage());
                senderTextView.setVisibility(View.VISIBLE);
                senderImage.setVisibility(View.GONE);
            }
            else if(currentMessage.getMessageType().equals("Record")){
                SendRecordContainer.setVisibility(View.VISIBLE);
                senderTextView.setVisibility(View.GONE);
                senderImage.setVisibility(View.GONE);
            }

            seenIcon.setVisibility(View.VISIBLE);
            if(currentMessage.getSeenStateNum() == 1)seenIcon.setImageResource(R.drawable.sent1);
            else if(currentMessage.getSeenStateNum() == 2)seenIcon.setImageResource(R.drawable.sent2);
            else if(currentMessage.getSeenStateNum() == 3)seenIcon.setImageResource(R.drawable.seen);

        }


        sendContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        ReceiveContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });


        return ListItemView;
    }

}
