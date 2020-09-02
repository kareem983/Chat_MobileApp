package com.example.chatting;
 
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
        // Here, we initialize the ArrayAdapter's internal storage for the context and the chatting_list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, androidFlavors);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View ListItemView = convertView;

        if(ListItemView == null) {
            ListItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.friends_list, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the chatting_list
        Friends currentFriend = getItem(position);

        //define xml components
        CircleImageView FriendImage = (CircleImageView) ListItemView.findViewById(R.id.FriendImage);
        TextView FriendName = (TextView) ListItemView.findViewById(R.id.FriendName);
        TextView FriendDate = (TextView)ListItemView.findViewById(R.id.FriendDate);
        CircleImageView FriendOnlineIcon = (CircleImageView)ListItemView.findViewById(R.id.FriendOnlineIcon);

        //set data
        Picasso.get().load(currentFriend.getFriendImage()).placeholder(R.drawable.user).into(FriendImage);
        FriendName.setText(currentFriend.getFriendName());
        FriendDate.setText(currentFriend.getFriendDate());


        if(currentFriend.isOnline()) {
            FriendOnlineIcon.setImageResource(currentFriend.getFriendOnlineIcon());
            FriendOnlineIcon.setVisibility(View.VISIBLE);
        }
        else{
            FriendOnlineIcon.setVisibility(View.GONE);
        }


        return ListItemView;
    }

}
