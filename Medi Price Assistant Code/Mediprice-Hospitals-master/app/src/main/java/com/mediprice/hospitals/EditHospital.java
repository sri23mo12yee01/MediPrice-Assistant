package com.mediprice.hospitals;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mediprice.hospitals.adapter.EditHospAdapter;
import com.mediprice.hospitals.model.hospitalModel;

import java.util.ArrayList;
import java.util.List;

public class EditHospital extends AppCompatActivity {
    RecyclerView recyview;
    ArrayList<hospitalModel> hospmodel;
    FirebaseFirestore db;
    EditHospAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_edit_hospital);
        recyview = findViewById(R.id.recyview);
        recyview.setLayoutManager(new LinearLayoutManager(this));
        hospmodel = new ArrayList<>();
        adapter = new EditHospAdapter(hospmodel, this);
        recyview.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        db.collection("hospital").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            hospitalModel obj = d.toObject(hospitalModel.class);
                            obj.setId(d.getId());
                            hospmodel.add(obj);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

}