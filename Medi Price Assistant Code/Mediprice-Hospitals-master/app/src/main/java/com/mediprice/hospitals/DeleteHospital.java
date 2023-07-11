package com.mediprice.hospitals;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mediprice.hospitals.adapter.HospitalListAdapter;
import com.mediprice.hospitals.model.hospitalModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DeleteHospital extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<hospitalModel> detaillist;
    FirebaseFirestore db;
    HospitalListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_delete_hospital);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        detaillist = new ArrayList<>();
        adapter = new HospitalListAdapter(detaillist, this);

        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        db.collection("hospital").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            hospitalModel obj = d.toObject(hospitalModel.class);
                            obj.setId(d.getId());
                            detaillist.add(obj);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

    }

    public void deleteHospital(String position) {
        new MaterialAlertDialogBuilder(DeleteHospital.this)
                .setTitle("Confirm")
                .setMessage("Action can't be Undone. proceed?")
                .setNegativeButton("Cancel", (dialog1, which) -> {
                    dialog1.dismiss();
                    DeleteHospital.this.onBackPressed();
                }).setPositiveButton("Delete", (dialog1, which) -> {
            db.collection("hospital").document(position)
                    .delete()
                    .addOnCompleteListener(task -> {
                        Toast.makeText(DeleteHospital.this, "Hospital deleted.", Toast.LENGTH_SHORT).show();
                        DeleteHospital.this.onBackPressed();
                        }
                    )
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(DeleteHospital.this, "Failed to delete hospital", Toast.LENGTH_SHORT).show();
                        }
                    });
            dialog1.dismiss();
        }).show();
    }
}