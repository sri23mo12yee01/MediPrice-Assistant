package com.mediprice.hospitals;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mediprice.hospitals.Helpers.SpHelper;
import com.mediprice.hospitals.adapter.RequestsAdapter;
import com.mediprice.hospitals.model.PatientModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class BedRequests extends AppCompatActivity {

    RecyclerView recview1;
    ArrayList<PatientModel> datalist1;
    FirebaseFirestore db;
    RequestsAdapter adapter;
    ImageView i1, i2;
    //String hospitalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_bed_requests);
        recview1 = findViewById(R.id.recview1);
        i1 = findViewById(R.id.i1);
        i2 = findViewById(R.id.i2);
        TextView tv = findViewById(R.id.listEmptyTv);

        recview1.setLayoutManager(new LinearLayoutManager(this));
        datalist1 = new ArrayList<>();
        System.out.println(SpHelper.getRoll(this));
        boolean isHospital = SpHelper.getRoll(this).getRole().equals("H");
        String hospitalidlogged = SpHelper.getRoll(this).getId();
        adapter = new RequestsAdapter(datalist1, this, isHospital);
        recview1.setAdapter(adapter);


        db = FirebaseFirestore.getInstance();

        db.collection("patient").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {

                            PatientModel obj = d.toObject(PatientModel.class);
                            obj.setId(d.getId());
                            try {
                                obj.setSelectedHospitalId(d.getString("hospitalId"));
                            } catch (Exception e) {

                            }

                            if (isHospital && hospitalidlogged.equals(d.getString("hospitalId"))) {
                                datalist1.add(obj);
                            }
                            if (!isHospital) {
                                datalist1.add(obj);
                            }
                        }
                        if (datalist1.isEmpty()) {
                            tv.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public void updateBedData(PatientModel dardi) {
        Log.d("TAG", "updateBedData() returned: " + dardi.getBedType());
        return;

    }

    public void deleteRequest(String position) {

        db.collection("patient").document(position)
                .delete()
                .addOnCompleteListener(task -> Toast.makeText(BedRequests.this, "Rejected Request Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(BedRequests.this, "Failed to reject request", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}