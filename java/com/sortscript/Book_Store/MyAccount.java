package com.sortscript.Book_Store;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sortscript.Book_Store.databinding.ActivityMyAccountBinding;

public class MyAccount extends AppCompatActivity {

    ActivityMyAccountBinding binding;
    Insert_ModelClass insert_modelClass;

    Uri filePath;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    public static final int PERMISSION_CODE = 1001;
    private static final int IMAGE_PICKER_CODE = 1;


    String Id, image, UID,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Uploading....");
        progressDialog.setCancelable(false);


        binding.etName.setEnabled(false);
        binding.etEmail.setEnabled(false);
        binding.etPhoneNo.setEnabled(false);
        binding.userImg.setEnabled(false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("BookStore").child("Users");
        storageReference = FirebaseStorage.getInstance().getReference();



        binding.userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        pickImageFromGallery();
                    }
                }
            }


        });


        databaseReference.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            insert_modelClass = dataSnapshot.getValue(Insert_ModelClass.class);
                            binding.etName.setText(insert_modelClass.getUsername());
                            binding.etEmail.setText(insert_modelClass.getEmail());
                            binding.etPhoneNo.setText(insert_modelClass.getPhone());

                            Id = insert_modelClass.getId();
                            password = insert_modelClass.getPassword();
                            UID = insert_modelClass.getUid();
                            Glide.with(getApplicationContext()).load(insert_modelClass.getImage())
                                    .placeholder(R.drawable.ic_baseline_person_24)
                                    .into(binding.userImg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.edtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.edtBtn.setVisibility(View.GONE);
                binding.saveBtn.setVisibility(View.VISIBLE);
                binding.etName.setEnabled(true);
                binding.etEmail.setEnabled(true);
                binding.etPhoneNo.setEnabled(true);
                binding.userImg.setEnabled(true);

            }
        });




        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {


                String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();



                String username = binding.etName.getText().toString();
                String email = binding.etEmail.getText().toString();
                String phone = binding.etPhoneNo.getText().toString();


                boolean check = validationinfo(username, email, phone);


                if (check) {
                    Toast.makeText(getApplicationContext(), "Valid Enteries", Toast.LENGTH_SHORT).show();
                    if(filePath==null)
                    {
                        databaseReference.child(UID).child("username").setValue(binding.etName.getText().toString());
                        databaseReference.child(UID).child("email").setValue(binding.etEmail.getText().toString());
                        databaseReference.child(UID).child("phone").setValue(binding.etPhoneNo.getText().toString());

                    }
                    else {
                        uploadToFirebase(filePath);
//                    finish();
                    }
                }
                binding.etName.setEnabled(false);
                binding.etEmail.setEnabled(false);
                binding.etPhoneNo.setEnabled(false);
                binding.userImg.setEnabled(false);
                binding.edtBtn.setVisibility(View.VISIBLE);
                binding.saveBtn.setVisibility(View.GONE);
            }

        });

    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICKER_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICKER_CODE) {
            binding.userImg.setImageURI(data.getData());
            filePath = data.getData();
        }
    }

    private void uploadToFirebase(Uri uri) {

        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        image = uri.toString();

                        Id = databaseReference.push().getKey();
                        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        password=insert_modelClass.getPassword();

                        Insert_ModelClass insert_modelClass = new Insert_ModelClass(Id,UID,
                                binding.etName.getText().toString(),
                                binding.etEmail.getText().toString(),
                                binding.etPhoneNo.getText().toString(),
                                password, image);

                        databaseReference.child(UID).setValue(insert_modelClass).addOnCompleteListener(task1 -> {

                            if (task1.isSuccessful()) {
                                Toast.makeText(MyAccount.this, "Added to Firebase", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MyAccount.this, "Not Added to Firebase", Toast.LENGTH_SHORT).show();
                            }
                        });

//                        String modelId = databaseReference.push().getKey();
                        databaseReference.child(UID).setValue(insert_modelClass);
                        Toast.makeText(MyAccount.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressDialog.dismiss();
//                Toast.makeText(AddBook.this, snapshot.toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(AddBook.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)


    private boolean validationinfo(String username, String email, String phone) {
        if (username.length() == 0) {
            binding.etName.requestFocus();
            binding.etName.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (email.length() == 0) {
            binding.etEmail.requestFocus();
            binding.etEmail.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (!email.matches("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+")) {
            binding.etEmail.requestFocus();
            binding.etEmail.setError("ENTER VALID EMAIL");
            return false;
        } else if (phone.length() == 0) {
            binding.etPhoneNo.requestFocus();
            binding.etPhoneNo.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (!phone.matches("^[0-9]{11}$")) {
            binding.etPhoneNo.requestFocus();
            binding.etPhoneNo.setError("CORRECT FORMAT: 03xxxxxxxxx");
            return false;
        } else {
            return true;
        }
    }
}
