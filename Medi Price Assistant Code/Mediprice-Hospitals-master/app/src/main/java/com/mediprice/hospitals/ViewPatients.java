package com.mediprice.hospitals;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mediprice.hospitals.adapter.HospNameAdapter;
import com.mediprice.hospitals.model.hospitalModel;

import java.util.ArrayList;
import java.util.List;

public class ViewPatients extends AppCompatActivity {
    RecyclerView recyviewPat;
    ArrayList<hospitalModel> nameslist;
    HospNameAdapter nameAdapter;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_view_patients);
        recyviewPat = findViewById(R.id.recyviewPat);
        recyviewPat.setLayoutManager(new LinearLayoutManager(this));
        nameslist = new ArrayList<>();
        nameAdapter = new HospNameAdapter(nameslist,this);
        recyviewPat.setAdapter(nameAdapter);
        db = FirebaseFirestore.getInstance();
        db.collection("hospital").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d:list)
                        {
                            hospitalModel obj = d.toObject(hospitalModel.class);
                            obj.setId(d.getId());
                            nameslist.add(obj);
                        }
                        if(nameslist.size()>0){
                            TextView tv=findViewById(R.id.listEmptyTv);
                            tv.setVisibility(View.GONE);
                        }
                        nameAdapter.notifyDataSetChanged();
                    }
                });

    }
}