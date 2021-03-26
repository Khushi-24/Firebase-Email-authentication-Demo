package com.example.emailloginexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txtemail, txtpassword, txtconfirmpassword;
    Button btn_register;
    ProgressBar progressBar;
    ImageView banner;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        txtemail = (EditText)findViewById(R.id.txt_email);
        txtpassword = (EditText)findViewById(R.id.txt_password);
        txtconfirmpassword = (EditText)findViewById(R.id.txt_confirm_password);
        progressBar = (ProgressBar)findViewById(R.id.proressBar);
        banner = (ImageView)findViewById(R.id.banner);
        banner.setOnClickListener(this);

        btn_register = (Button)findViewById(R.id.buttonRegister);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.buttonRegister:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = txtemail.getText().toString().trim();
        String password = txtpassword.getText().toString().trim();
        String confirmPassword = txtconfirmpassword.getText().toString().trim();

        if (email.isEmpty()) {
            txtemail.setError("Email is required");
            txtemail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            txtpassword.setError("Password is required");
            txtpassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            txtconfirmpassword.setError("Confirm Password is required");
            txtconfirmpassword.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtemail.setError("Please provide valid email!");
            txtemail.requestFocus();
            return;
        }

        if(password.length() < 6){
            txtpassword.setError("Min Password length should be 6 characters!");
            txtpassword.requestFocus();
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(RegistrationActivity.this, "User has been Registered Successfully!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(RegistrationActivity.this, "Failed to Register! Try Again!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegistrationActivity.this, "Failed to Register! Try Again!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}