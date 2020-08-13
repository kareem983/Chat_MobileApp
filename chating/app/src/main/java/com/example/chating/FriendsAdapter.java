package com.example.chating;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends ArrayAdapter<Friends> {


    public FriendsAdapter(Activity context, ArrayList<Friends> androidFlavors) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the chating_list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, androidFlavors);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.friends_list, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the chating_list
        Friends currentFriend = getItem(position);



        CircleImageView FriendImage = (CircleImageView) listItemView.findViewById(R.id.FriendImage);
        // Get the image resource ID from the current AndroidFlavor object and
        // set the image to iconView
        Picasso.get().load(currentFriend.getFriendImage()).placeholder(R.drawable.user).into(FriendImage);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView FriendName = (TextView) listItemView.findViewById(R.id.FriendName);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        FriendName.setText(currentFriend.getFriendName());


        TextView FriendDate = (TextView)listItemView.findViewById(R.id.FriendDate);
        FriendDate.setText(currentFriend.getFriendDate());

        CircleImageView FriendOnlineIcon = (CircleImageView)listItemView.findViewById(R.id.FriendOnlineIcon);

        if(currentFriend.isOnline()) {
            FriendOnlineIcon.setImageResource(currentFriend.getFriendOnlineIcon());
            FriendOnlineIcon.setVisibility(View.VISIBLE);
        }
        else{
            FriendOnlineIcon.setVisibility(View.GONE);
        }


        // Return the whole chating_list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }




}
