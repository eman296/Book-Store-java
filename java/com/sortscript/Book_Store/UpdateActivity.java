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
import com.sortscript.Book_Store.databinding.ActivityUpdateBinding;

import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity {


    ActivityUpdateBinding binding;
    Uri filePath;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String passimg,uid;
    ArrayList<AddBookModelClass> list;
    AddBookModelClass addBookModelClass;
    FirebaseAuth mAuth;

    public static final int PERMISSION_CODE = 1001;
    private static final int IMAGE_PICKER_CODE = 1;

    String id, image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Uploading....");
        progressDialog.setCancelable(false);

        uid = FirebaseAuth.getInstance().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("BookStore").child("Books");
        storageReference = FirebaseStorage.getInstance().getReference();

        list = new ArrayList<>();


        databaseReference.orderByChild("id").equalTo(getIntent().getStringExtra("PrivateKey")).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    addBookModelClass = dataSnapshot.getValue(AddBookModelClass.class);
                    binding.edtTitle.setText(addBookModelClass.getTitle());
                    binding.edtAuthor.setText(addBookModelClass.getAuthor());
                    binding.edtDescription.setText(addBookModelClass.getDescription());
                    binding.edtPrice.setText(addBookModelClass.getSellprice());
                    binding.edtCondition.setText(addBookModelClass.getCondition());
                    id = addBookModelClass.getId();
                    uid = addBookModelClass.getUid();
                    Glide.with(getApplicationContext()).load(addBookModelClass.getImage())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(binding.imgView);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                boolean check = validationinfo(
                        binding.edtTitle.getText().toString(),
                        binding.edtAuthor.getText().toString(),
                        binding.edtDescription.getText().toString(),
                        binding.edtPrice.getText().toString(),
                        binding.edtCondition.getText().toString());
                if (check)
                {
                    if(filePath==null)
                    {
                        databaseReference.child(id).child("title").setValue(binding.edtTitle.getText().toString());
                        databaseReference.child(id).child("author").setValue(binding.edtAuthor.getText().toString());
                        databaseReference.child(id).child("category").setValue(binding.categorySpinner.getSelectedItem().toString());
                        databaseReference.child(id).child("description").setValue(binding.edtDescription.getText().toString());
                        databaseReference.child(id).child("sellprice").setValue(binding.edtPrice.getText().toString());
                        databaseReference.child(id).child("condition").setValue(binding.edtCondition.getText().toString());
                        Glide.with(getApplicationContext()).load(addBookModelClass.getImage())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(binding.imgView);
                        Toast.makeText(UpdateActivity.this, "Uplpaded Successfull", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UpdateActivity.this, MyBooks.class));
                        finish();
                    }
                    else
                    {
                        uploadToFirebase(filePath);
                        Toast.makeText(UpdateActivity.this, "Uplpaded Successfull", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UpdateActivity.this, MyBooks.class));
                        finish();
                    }

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
                        AddBookModelClass addBookModelClass = new AddBookModelClass(uid, id, image,
                                binding.edtTitle.getText().toString(),
                                binding.edtAuthor.getText().toString(),
                                binding.categorySpinner.getSelectedItem().toString(),
                                binding.edtDescription.getText().toString(),
                                binding.edtPrice.getText().toString(),
                                binding.edtCondition.getText().toString());

                        databaseReference.child(id).setValue(addBookModelClass).addOnCompleteListener(task1 -> {

                            if (task1.isSuccessful()) {
                                Toast.makeText(UpdateActivity.this, "Book Updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UpdateActivity.this, "Fail to Update", Toast.LENGTH_SHORT).show();
                            }
                        });
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
        } else {
            return true;
        }

    }

}

