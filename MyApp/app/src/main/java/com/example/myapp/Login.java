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

public class Login extends AppCompatActivity {

    private EditText email,password;
    private Button btnLogIn;
    private TextView btnReg;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=(EditText)findViewById(R.id.emailLog);
        password=(EditText)findViewById(R.id.passwordLog);
        btnLogIn=(Button)findViewById(R.id.logBtn);
        btnReg=(TextView)findViewById(R.id.btnReg);
        fAuth=FirebaseAuth.getInstance();



        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailS = email.getText().toString();
                String passwordS = password.getText().toString();

                if (TextUtils.isEmpty(emailS))
                {
                    email.setError("Email adresa je neophodna.");
                    email.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(passwordS))
                {
                    password.setError("Lozinka adresa je neophodna.");
                    password.requestFocus();
                    return;
                }

                fAuth.signInWithEmailAndPassword(emailS,passwordS).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Login.this, "User logged in.",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(Login.this, "Error."+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Registration.class));
                finish();
            }
        });
    }
}