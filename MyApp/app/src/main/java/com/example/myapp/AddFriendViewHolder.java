package com.example.myapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AddFriendViewHolder extends RecyclerView.ViewHolder
{

    TextView mUserName, mUuidKorisnika;
    Button mAddFriend;

    public AddFriendViewHolder(@NonNull View itemView) {
        super(itemView);
        mUserName = itemView.findViewById(R.id.rang_user_name);
        mUuidKorisnika = itemView.findViewById(R.id.uuid_korisnika);
        mUuidKorisnika.setVisibility(View.GONE);
        mAddFriend = itemView.findViewById(R.id.dodajPrijatelja);
    }
}
