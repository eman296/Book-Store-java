package com.sortscript.Book_Store;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sortscript.Book_Store.databinding.ActivityLoginPageBinding;
import com.sortscript.Book_Store.databinding.ActivityRegisterPageBinding;

public class RegisterPage extends AppCompatActivity {

    ActivityRegisterPageBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("BookStore").child("Users");

        binding.btnSigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = binding.etUsername.getText().toString();
                String email = binding.etEmail.getText().toString();
                String phone = binding.etPhoneNo.getText().toString();
                String password = binding.etPassword.getText().toString();
                String image = null;

                boolean check = validationinfo(username,email,phone,password);
                if (check==true){

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        progressDialog = new ProgressDialog(RegisterPage.this);
                                        progressDialog.setTitle("Updating Data");
                                        progressDialog.setProgress(10);
                                        progressDialog.setMax(3);
                                        progressDialog.setMessage("Loading...");
                                        new MyTask().execute();


                                        // Sign in success, update UI with the signed-in user's information
                                        String id =reference.push().getKey();
                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        Insert_ModelClass insert_modelClass = new Insert_ModelClass(id,uid,username,email,phone,password,image);


                                        reference.child(uid).setValue(insert_modelClass).addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                Toast.makeText(RegisterPage.this, "Added to Firebase", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegisterPage.this, LoginPage.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                progressDialog.dismiss();
                                                startActivity(intent);
                                            }

                                            else {
                                                Toast.makeText(RegisterPage.this, "Not Added to Firebase", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                    else
                                    {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterPage.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }

            }
        });

        binding.txtSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterPage.this,LoginPage.class));
            }
        });
    }

    public class MyTask extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            progressDialog.show();
        }

        public Void doInBackground(Void... unused) {
            return null;
        }
    }


    private boolean validationinfo(String username, String email, String phone, String password) {
        if (username.length() == 0) {
            binding.etUsername.requestFocus();
            binding.etUsername.setError("FIELD CANNOT BE EMPTY");
            return false;
        }
        else if (email.length()==0){
            binding.etEmail.requestFocus();
            binding.etEmail.setError("FIELD CANNOT BE EMPTY");
            return false;
        }
        else if (!email.matches("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+")){
            binding.etEmail.requestFocus();
            binding.etEmail.setError("ENTER VALID EMAIL");
            return false;
        }
        else if (phone.length()==0){
            binding.etPhoneNo.requestFocus();
            binding.etPhoneNo.setError("FIELD CANNOT BE EMPTY");
            return false;
        }
        else if (!phone.matches("^[0-9]{11}$")){
            binding.etPhoneNo.requestFocus();
            binding.etPhoneNo.setError("CORRECT FORMAT: 03xxxxxxxxx");
            return false;
        }
        else if (password.length()<=5){
            binding.etPassword.requestFocus();
            binding.etPassword.setError("MAXIMUM 6 CHARACTER REQUIRED");
            return false;
        }
        else {
            return true;
        }
    }


    @Override
    protected void onRestart() {
        finish();
        super.onRestart();
    }
}