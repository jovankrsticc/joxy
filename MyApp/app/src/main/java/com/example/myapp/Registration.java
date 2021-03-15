package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    private EditText name,lastName,number,userName,email,password;
    private Button regBtn;
    private TextView log;
    private FirebaseAuth fAuth;
    private FirebaseDatabase database= FirebaseDatabase.getInstance();
    private DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        name=(EditText) findViewById(R.id.name);
        lastName=(EditText) findViewById(R.id.lastName);
        number=(EditText) findViewById(R.id.number);
        userName=(EditText) findViewById(R.id.userName);
        email=(EditText) findViewById(R.id.email);
        password=(EditText) findViewById(R.id.password);
        regBtn=(Button) findViewById(R.id.regBtn);
        log=(TextView)findViewById(R.id.log);
        fAuth=FirebaseAuth.getInstance();
        ref=database.getReference("Users");

        if(fAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),Home.class));
            finish();
        }


       regBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String emailS = email.getText().toString();
                String passwordS = password.getText().toString();
                String nameS=name.getText().toString();
                String lastNameS=lastName.getText().toString();
                String userNameS =userName.getText().toString();
                String numberS=number.getText().toString();

                if (TextUtils.isEmpty(emailS))
                {
                    email.setError("Email is required.");
                    email.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(passwordS))
                {
                    password.setError("Password is required.");
                    password.requestFocus();
                    return;
                }

                fAuth.createUserWithEmailAndPassword(emailS,passwordS).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Registration.this, "User created.",Toast.LENGTH_SHORT).show();
                            String id=fAuth.getCurrentUser().getUid();
                            ref.child(id).setValue(new User(nameS,lastNameS,userNameS,numberS, emailS,passwordS));
                            startActivity(new Intent(getApplicationContext(),Image.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(Registration.this, "Error."+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });


    }
}