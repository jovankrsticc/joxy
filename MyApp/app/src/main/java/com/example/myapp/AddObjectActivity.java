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

public class AddObjectActivity extends AppCompatActivity {

    DatabaseReference currentUser, newObject;
    FirebaseAuth fAuth;
    String userID;
    EditText userLong, userLat, objectName;
    Button btnName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_object);
        userLong = findViewById(R.id.editTextUserLong);
        userLat = findViewById(R.id.editTextLatitudeKorisnika);
        objectName = findViewById(R.id.objName);
        btnName = findViewById(R.id.addObj);
        newObject = FirebaseDatabase.getInstance().getReference().child("Objects");
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        currentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userLong.setText(snapshot.child("myLocation").child("longitude").getValue().toString());
                userLat.setText(snapshot.child("myLocation").child("latitude").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String objectNameS = objectName.getText().toString();
                String userLongS = userLong.getText().toString();
                String userLatS = userLat.getText().toString();
                if(objectNameS.isEmpty())
                {
                    Toast.makeText(AddObjectActivity.this, "Object name is required!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String key = newObject.push().getKey();
                    MyObject myObject=new MyObject(objectNameS,Double.valueOf(userLatS),Double.valueOf(userLongS),new Date(2021,5,8));
                    newObject.child(key).setValue(myObject);
                    Intent intent=new Intent(AddObjectActivity.this, MapsActivity.class);
                  //  intent.putExtra("Object",(Parcelable)myObject);
                    Toast.makeText(AddObjectActivity.this, "Object added.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}