package com.example.firebasecrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    FloatingActionButton btnAdd;
    EditText etSearch;

    RecyclerView rvCarList;
    FirebaseRecyclerOptions<Car> options;
    FirebaseRecyclerAdapter<Car,CarViewHolder> adapter;

    DatabaseReference dbref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Data Reference
        dbref= FirebaseDatabase.getInstance().getReference().child("cars");

        //Recycler View
        rvCarList=findViewById(R.id.rvCarList);
        rvCarList.setLayoutManager(new LinearLayoutManager(this));
        rvCarList.setHasFixedSize(true);

        //Load Data in Recycler View
        LoadData("");

        //floating action button
        btnAdd=findViewById(R.id.btnAdd);


        //starting main activity
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
            }
        });

        //search view implementation
        etSearch=findViewById(R.id.search);
        etSearch.addTextChangedListener(textWatcher);

    }

    //search implementation
    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString()!=null){
                LoadData(s.toString());
            }else{
                LoadData("");
            }
        }
    };

    private void LoadData(String schar) {

        //searc implementaion
        Query query=dbref.orderByChild("name").startAt(schar).endAt(schar+"\uf8ff");

        //send model class (Car) to build models or options
        //we can set dbref instead of query if wont to apply search
        options=new FirebaseRecyclerOptions.Builder<Car>().setQuery(query,Car.class).build();

        //create firebase ui adapter
        adapter=new FirebaseRecyclerAdapter<Car, CarViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CarViewHolder holder, int position, @NonNull Car model) {
                holder.carName.setText(model.getName());
                Picasso.get().load(model.getPurl()).into(holder.carImage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(HomeActivity.this,InfoActivity.class);
                        i.putExtra("carKey",getRef(position).getKey());
                        startActivity(i);

                    }
                });
            }

            @NonNull
            @Override
            public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_single_item,parent,false);
                return new CarViewHolder(view);
            }
        };
        adapter.startListening();
        rvCarList.setAdapter(adapter);
    }

    private class CarViewHolder extends RecyclerView.ViewHolder {
        TextView carName;
        ImageView carImage;
        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carName=itemView.findViewById(R.id.carName);
            carImage=itemView.findViewById(R.id.carImage);
        }
    }
}