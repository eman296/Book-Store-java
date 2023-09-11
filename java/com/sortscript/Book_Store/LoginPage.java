package com.sortscript.Book_Store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sortscript.Book_Store.databinding.ActivityAddBookBinding;
import com.sortscript.Book_Store.databinding.ActivityLoginPageBinding;

public class LoginPage extends AppCompatActivity {

    FirebaseAuth mauth;
    ActivityLoginPageBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mauth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etEmail.getText().toString();

                String password = binding.etPassword.getText().toString();

                boolean check = validationinfo(email, password);
                if (check == true) {
//                Toast.makeText(getApplicationContext(), "Valid Enteries", Toast.LENGTH_SHORT).show();
                    mauth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginPage.this, "User Logged in Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginPage.this, HomeMenu.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {

                                Toast.makeText(getApplicationContext(), "Login  Erorr:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });


        binding.txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this,RegisterPage.class));
            }
        });
    }


    private boolean validationinfo(String email, String password) {
        if (email.length() == 0) {
            binding.etEmail.requestFocus();
            binding.etEmail.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (!email.matches("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+")) {
            binding.etEmail.requestFocus();
            binding.etEmail.setError("ENTER VALID EMAIL");
            return false;
        } else if (password.length() <= 5) {
            binding.etPassword.requestFocus();
            binding.etPassword.setError("MAXIMUM 6 CHARACTER REQUIRED");
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onRestart() {
        finish();
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mauth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginPage.this, HomeMenu.class));
            currentUser.reload();
        }
        super.onStart();
    }
}