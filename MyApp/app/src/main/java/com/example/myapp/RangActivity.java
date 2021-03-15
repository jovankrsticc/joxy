package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

public class RangActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<User, AddFriendViewHolder> adapter;
    FirebaseRecyclerOptions<User> options;
    DatabaseReference korisniciReference, zahteviZaPrijateljstvoReference, korisnik;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    Query query;
    String trenutniKorisnik;
    User ulogovaniKorisnik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rang);
        korisniciReference = FirebaseDatabase.getInstance().getReference().child("Users");
        zahteviZaPrijateljstvoReference = FirebaseDatabase.getInstance().getReference().child("Friend requests");
        query = korisniciReference.orderByChild("rate");
        //korisniciReference = (DatabaseReference) korisniciReference.orderByChild("brojTokena");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        trenutniKorisnik = mAuth.getCurrentUser().getUid();
        korisnik = FirebaseDatabase.getInstance().getReference();
        DatabaseReference db= korisnik.child("Users");
        db.addValueEventListener(new ValueEventListener() {
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
        LoadKorisnici();
    }

    private void LoadKorisnici() {
        options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        adapter = new FirebaseRecyclerAdapter<User, AddFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AddFriendViewHolder holder, int position, @NonNull User model) {
                holder.mUserName.setText(model.userName);
                holder.mUuidKorisnika.setText(getRef(position).getKey().toString());
                String idKorisnika = getRef(position).getKey().toString();
                holder.mAddFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference rf = zahteviZaPrijateljstvoReference.child(idKorisnika).child(trenutniKorisnik);
                        //zahteviZaPrijateljstvoReference.child(getRef(position).getKey().toString()).child(key).setValue(trenutniKorisnik);
                        rf.setValue(ulogovaniKorisnik);
                    }
                });
            }

            @NonNull
            @Override
            public AddFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_friends, parent, false);
                return new AddFriendViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


}