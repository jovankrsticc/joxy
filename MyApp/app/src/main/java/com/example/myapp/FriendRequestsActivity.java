package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class FriendRequestsActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<User, FriendRequestHolder> adapter;
    FirebaseRecyclerOptions<User> options;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    Query query;
    String trenutniKorisnik;
    DatabaseReference zahteviZaPrijateljstvo, prijateljstva, korisnici;
    User ulogovaniKorisnik=new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        mAuth = FirebaseAuth.getInstance();
        trenutniKorisnik = mAuth.getCurrentUser().getUid().toString();
        zahteviZaPrijateljstvo = FirebaseDatabase.getInstance().getReference().child("Friend requests").child(trenutniKorisnik);
        prijateljstva = FirebaseDatabase.getInstance().getReference().child("Friendships");
        recyclerView = findViewById(R.id.recyclerViewFriendRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        korisnici = FirebaseDatabase.getInstance().getReference().child("Users");
        korisnici.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imeKorisnika = snapshot.child(trenutniKorisnik).child("firstName").getValue(String.class);
                String prezimeKorisnika = snapshot.child(trenutniKorisnik).child("lastName").getValue(String.class);
                String korisnickoIme = snapshot.child(trenutniKorisnik).child("userName").getValue(String.class);
                String emailAdresa = snapshot.child(trenutniKorisnik).child("email").getValue(String.class);
                String lozinka = snapshot.child(trenutniKorisnik).child("password").getValue(String.class);
                String brojTelefona = snapshot.child(trenutniKorisnik).child("number").getValue(String.class);
                int brojTokena = snapshot.child(trenutniKorisnik).child("rate").getValue(Integer.class);
                ulogovaniKorisnik = new User(imeKorisnika, prezimeKorisnika, emailAdresa, korisnickoIme, brojTelefona, lozinka, brojTokena);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LoadRequests();
    }

    private void LoadRequests() {
        options = new FirebaseRecyclerOptions.Builder<User>().setQuery(zahteviZaPrijateljstvo, User.class).build();
        adapter = new FirebaseRecyclerAdapter<User, FriendRequestHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendRequestHolder holder, int position, @NonNull User model) {
                holder.mUserName.setText(model.userName);
                holder.mUuid.setText(getRef(position).getKey().toString());
                holder.mAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        zahteviZaPrijateljstvo.child(getRef(position).getKey().toString()).removeValue();
                        //String key = prijateljstva.push().getKey();
                        //prijateljstva.child(key).setValue(new Friendship(model, ulogovaniKorisnik));
                        prijateljstva.child(trenutniKorisnik).child(getRef(position).getKey().toString()).setValue(model);
                        prijateljstva.child(getRef(position).getKey().toString()).child(trenutniKorisnik).setValue(ulogovaniKorisnik);
                    }
                });
            }

            @NonNull
            @Override
            public FriendRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_requests, parent, false);
                return new FriendRequestHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


}