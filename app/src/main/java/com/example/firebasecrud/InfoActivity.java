package com.example.firebasecrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class InfoActivity extends AppCompatActivity {

    ImageView imageCar;
    TextView nameCar;
    Button btnDel;

    DatabaseReference ref;


    //reference for deletion
    DatabaseReference dbref;
    StorageReference stref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //ImageViews
        imageCar=findViewById(R.id.imageCar);

        //TextViews
        nameCar=findViewById(R.id.nameCar);

        //Buttons
        btnDel=findViewById(R.id.btnDel);

        //Database reference
        ref= FirebaseDatabase.getInstance().getReference().child("cars");

        //getting car key
        String carKey=getIntent().getStringExtra("carKey");

        //reference for deletion
        dbref=FirebaseDatabase.getInstance().getReference().child("cars").child(carKey);
        stref= FirebaseStorage.getInstance().getReference().child("images").child(carKey+".jpg");


        //
        ref.child(carKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name=snapshot.child("name").getValue().toString();
                    String purl=snapshot.child("purl").getValue().toString();
                    Picasso.get().load(purl).into(imageCar);
                    nameCar.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //deleting item
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        stref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(InfoActivity.this,HomeActivity.class));
                            }
                        });
                    }
                });
            }
        });



    }
}