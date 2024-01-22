package com.example.drawer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drawer.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class signupActivity extends AppCompatActivity {

    EditText Name, Surname, Email, Password;
    Button Signup, Login;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Name = findViewById(R.id.editTextName);
        Surname = findViewById(R.id.editTextSurname);
        Email = findViewById(R.id.editTextEmailforSignup);
        Password = findViewById(R.id.editTextPasswordforSignup);

        Signup = findViewById(R.id.signup_signup_button);
        Login = findViewById(R.id.signup_login_button);

        auth = FirebaseAuth.getInstance();

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = Name.getText().toString();
                String surname = Surname.getText().toString();
                String email = Email.getText().toString();
                String password = Password.getText().toString();

                if(name.isEmpty()){
                    Toast.makeText(signupActivity.this, "İsim alanı boş olamaz", Toast.LENGTH_SHORT).show();
                }

                if(surname.isEmpty()){
                    Toast.makeText(signupActivity.this, "Soyisim alanı boş olamaz", Toast.LENGTH_SHORT).show();
                }

                if(email.isEmpty()){
                    Toast.makeText(signupActivity.this, "Email alanı boş olamaz", Toast.LENGTH_SHORT).show();
                }

                if(password.isEmpty()){
                    Toast.makeText(signupActivity.this, "Parola alanı boş olamaz", Toast.LENGTH_SHORT).show();
                }

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String uid = task.getResult().getUser().getUid();
                            Toast.makeText(signupActivity.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), loginActivity.class));

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference ref = db.collection("UserModel");
                            UserModel user = new UserModel(name, surname, email, password);
                            ref.add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "Belge eklendi. Belge ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Belge eklenirken hata oluştu", e);
                                        }
                                    });
                        }else{
                            Toast.makeText(signupActivity.this, "Kayıt Başarısız", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), loginActivity.class));
            }
        });
    }
}