package com.mediprice.hospitals;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mediprice.hospitals.adapter.DetailsPatAdapter;
import com.mediprice.hospitals.model.model;

import java.util.ArrayList;
import java.util.List;

public class DetailsPatient extends AppCompatActivity {

    RecyclerView recview;
    ArrayList<model> datalist;
    FirebaseFirestore db;
    DetailsPatAdapter adapter;
    ImageView i1, i2;
    String hospitalId;
    EditText searchText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_details_patient);
        recview = findViewById(R.id.recview);
        i1 = findViewById(R.id.i1);
        i2 = findViewById(R.id.i2);
        searchText = findViewById(R.id.searchText);

        hospitalId = getIntent().getStringExtra("id");
        recview.setLayoutManager(new LinearLayoutManager(this));
        datalist = new ArrayList<>();

        adapter = new DetailsPatAdapter(datalist, this);
        recview.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        db.collection("hospital").document(hospitalId).collection("booking").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            model obj = d.toObject(model.class);
                            if(obj.getGenId()==null){
                                continue;
                            }
                            obj.setId(d.getId());
                            datalist.add(obj);
                        }
                        if(datalist.size()>0){
                            TextView tv=findViewById(R.id.listEmptyTv);
                            tv.setVisibility(View.GONE);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
        //search fun
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });
        //end
    }
    private void filter(String text) {

        ArrayList<model> filterList = new ArrayList<>();
        for (model item : datalist) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getGenId().toLowerCase().contains(text.toLowerCase())
            ) {
                filterList.add(item);
            }
        }
        adapter.filteredList(filterList);

    }

}