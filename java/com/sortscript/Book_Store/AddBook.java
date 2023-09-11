package com.sortscript.Book_Store;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sortscript.Book_Store.databinding.ActivityAddBookBinding;

import java.util.UUID;

public class AddBook extends AppCompatActivity {

    ActivityAddBookBinding binding;
    Uri filePath;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    public static final int PERMISSION_CODE = 1001;
    private static final int IMAGE_PICKER_CODE = 1;

    String id, UID, image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Uploading....");
        progressDialog.setCancelable(false);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("BookStore").child("Books");
        storageReference = FirebaseStorage.getInstance().getReference();


        Spinner spinner = (Spinner) findViewById(R.id.category_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_spinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        binding.imgView.setOnClickListener(new View.OnClickListener() {
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

        binding.btnSellNow.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                boolean check = validationinfo(
                        binding.edtTitle.getText().toString(),
                        binding.edtAuthor.getText().toString(),
                        binding.edtDescription.getText().toString(),
                        binding.edtPrice.getText().toString(),
                        binding.edtCondition.getText().toString());

                if (check) {
                    Toast.makeText(getApplicationContext(), "Valid Entries", Toast.LENGTH_SHORT).show();
                    uploadToFirebase(filePath);
                    startActivity(new Intent(AddBook.this, MyBooks.class));
                    finish();
                }
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
            binding.imgView.setImageURI(data.getData());
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

                        id = databaseReference.push().getKey();
                        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        AddBookModelClass model = new AddBookModelClass(UID, id, image,
                                binding.edtTitle.getText().toString(),
                                binding.edtAuthor.getText().toString(),
                                binding.categorySpinner.getSelectedItem().toString(),
                                binding.edtDescription.getText().toString(),
                                binding.edtPrice.getText().toString(),
                                binding.edtCondition.getText().toString());

                        databaseReference.child(id).setValue(model).addOnCompleteListener(task1 -> {

                            if (task1.isSuccessful()) {
                                Toast.makeText(AddBook.this, "Added to Firebase", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddBook.this, "Not Added to Firebase", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(AddBook.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
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


    private boolean validationinfo(String titles, String authors, String descriptions, String sellprices, String condition) {

        if (titles.length() == 0) {
            binding.edtTitle.requestFocus();
            binding.edtTitle.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (authors.length() == 0) {
            binding.edtAuthor.requestFocus();
            binding.edtAuthor.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (descriptions.length() == 0) {
            binding.edtDescription.requestFocus();
            binding.edtDescription.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (sellprices.length() == 0) {
            binding.edtPrice.requestFocus();
            binding.edtPrice.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (condition.length() == 0) {
            binding.edtCondition.requestFocus();
            binding.edtCondition.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (filePath == null) {
            Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

}