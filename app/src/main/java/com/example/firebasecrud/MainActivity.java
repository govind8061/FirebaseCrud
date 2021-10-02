package com.example.firebasecrud;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Button btnUpload;
    ImageView imgSelect;
    EditText etName;
    ProgressBar progressBar;
    Uri imgURI;
    boolean isImageAdded=false;
    DatabaseReference dbref;
    StorageReference stref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ImageViews
        imgSelect=findViewById(R.id.imgSelect);

        //Buttons
        btnUpload=findViewById(R.id.btnUpload);

        //EditTexts
        etName=findViewById(R.id.etName);

        //ProgressBar
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        //dbref
        dbref= FirebaseDatabase.getInstance().getReference().child("cars");

        //stref
        stref= FirebaseStorage.getInstance().getReference().child("images");

        //Select Image
        selectImage();

        //Upload Image onClick of btnUpload
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=etName.getText().toString();
                if (isImageAdded && name!=null){
                    uploadImage(name);
                    etName.getText().clear();
                }
            }
        });




    }

    //method to selectImage
    private  void selectImage(){
        imgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startForResult.launch("image/*");
            }
        });
    }

    //startActivity for Result to setImageUri
    ActivityResultLauncher<String> startForResult=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result!=null){
                imgURI=result;
                imgSelect.setImageURI(imgURI);
                isImageAdded=true;
            }
        }
    });

    //method to uploadImage
    private void uploadImage(final String imgName){
        progressBar.setVisibility(View.VISIBLE);
        String key=dbref.push().getKey();
        stref.child(key+".jpg").putFile(imgURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                stref.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        HashMap hashMap=new HashMap();
                        hashMap.put("purl",uri.toString());
                        hashMap.put("name",imgName);
                        dbref.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }
}