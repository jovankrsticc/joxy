package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class DodajObjekatActivity extends AppCompatActivity {
    EditText longitudeKorisnika, latitudeKorisnika, nazivObjekta;
    Button dodajObjekatNaMapi;
    DatabaseReference trenutniKorisnik, noviObjekatNaMapi;
    FirebaseAuth fAuth;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_objekat);
        longitudeKorisnika = findViewById(R.id.editTextLongitudeKorisnika);
        latitudeKorisnika = findViewById(R.id.editTextLatitudeKorisnika);
        nazivObjekta = findViewById(R.id.nazivObjekta);
        dodajObjekatNaMapi = findViewById(R.id.dodajNoviObjekat);
        noviObjekatNaMapi = FirebaseDatabase.getInstance().getReference().child("Objects");
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid().toString();
        trenutniKorisnik = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        trenutniKorisnik.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                longitudeKorisnika.setText(snapshot.child("myLocation").child("longitude").getValue().toString());
                latitudeKorisnika.setText(snapshot.child("myLocation").child("latitude").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        dodajObjekatNaMapi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nazivObjektaString = nazivObjekta.getText().toString();
                String longitudeKorisnikaString = longitudeKorisnika.getText().toString();
                String latitudeKorisnikaString = latitudeKorisnika.getText().toString();
                if(nazivObjektaString.isEmpty())
                {
                    Toast.makeText(DodajObjekatActivity.this, "Object name is required!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String key = noviObjekatNaMapi.push().getKey();
                    MyObject myObject=new MyObject(nazivObjektaString,Double.valueOf(latitudeKorisnikaString),Double.valueOf(longitudeKorisnikaString),new Date(2021,5,8));
                    noviObjekatNaMapi.child(key).setValue(myObject);
                    Intent intent=new Intent(DodajObjekatActivity.this, MapsActivity.class);
                    intent.putExtra("Objekat",(Parcelable)myObject);
                    Toast.makeText(DodajObjekatActivity.this, "Uspesno ste dodali novi objekat na mapi", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}