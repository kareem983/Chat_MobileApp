package com.example.chating;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==0){
            ChatsFragment chatsFragment =new ChatsFragment();
            return chatsFragment;
        }

        else if(position==1){
            FriendsFragment firendsFragment =new FriendsFragment();
            return firendsFragment;
        }

        else if(position==2){
            RequestsFragment requestsFragment =new RequestsFragment();
            return requestsFragment;
        }

        else return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        if(position==0){
            return "Chats";
        }

        else if(position==1){
            return "Friends";
        }

        else if(position==2){
            return "Requests";
        }
        else return null;
    }


}
