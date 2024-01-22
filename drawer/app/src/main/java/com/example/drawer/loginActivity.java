package com.example.drawer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {

    EditText Email, Password;
    Button LoginButton, SignupButton;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.editTextEmail);
        Password = findViewById(R.id.editTextPassword);

        LoginButton = findViewById(R.id.login_login_button);
        SignupButton = findViewById(R.id.login_signup_button);

        auth = FirebaseAuth.getInstance();

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(loginActivity.this, signupActivity.class);
                startActivity(i);
            }
        });
    }

    private void login(){
        String email = Email.getText().toString();
        String password = Password.getText().toString();

        if(email.isEmpty()){
            Toast.makeText(this, "Email alanı boş olamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.isEmpty()){
            Toast.makeText(this, "Parola alanı boş olamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(loginActivity.this, "Login Başarılı", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(loginActivity.this, MainActivity.class);
                            startActivity(i);
                        }else{
                            Toast.makeText(loginActivity.this, "Email ya da parola hatalı", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}