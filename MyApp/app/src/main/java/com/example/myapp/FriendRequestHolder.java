package com.example.myapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FriendRequestHolder extends RecyclerView.ViewHolder
{
    TextView mUserName, mUuid;
    Button mAccept;

    public FriendRequestHolder(@NonNull View itemView) {
        super(itemView);

        mUserName = itemView.findViewById(R.id.request_user_name);
        mUuid = itemView.findViewById(R.id.uuid_requester);
        mAccept = itemView.findViewById(R.id.accept);
        mUuid.setVisibility(View.GONE);
    }
}
