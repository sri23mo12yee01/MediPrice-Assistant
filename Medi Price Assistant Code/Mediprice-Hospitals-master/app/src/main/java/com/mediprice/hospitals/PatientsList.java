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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mediprice.hospitals.Helpers.SpHelper;
import com.mediprice.hospitals.adapter.RecyclerViewAdapter;
import com.mediprice.hospitals.model.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class PatientsList extends AppCompatActivity {
    RecyclerView recview;
    ArrayList<model> datalist;
    FirebaseFirestore db;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    RecyclerViewAdapter adapter;
    ImageView i1, i2;
    String hospitalId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_patients_list);
        recview = findViewById(R.id.recview);
        i1 = findViewById(R.id.i1);
        i2 = findViewById(R.id.i2);

        TextView listEmptyTv = findViewById(R.id.listEmptyTv);
        hospitalId = SpHelper.getRoll(this).getId();//mAuth.getCurrentUser().getUid();
        recview.setLayoutManager(new LinearLayoutManager(this));
        datalist = new ArrayList<>();

        adapter = new RecyclerViewAdapter(datalist, this, this);
        recview.setAdapter(adapter);


        db = FirebaseFirestore.getInstance();
        db.collection("hospital").document(hospitalId).collection("booking").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {

                            model obj = d.toObject(model.class);
                            if (obj.getGenId() != null) {
                                continue;
                            }
                            obj.setId(d.getId());
                            obj.toStringOverload();
                            datalist.add(obj);
                        }
                        if (datalist.size() > 0) {
                            listEmptyTv.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

    }

//    public void deleteData(int position) {
//
//        db.collection("hospital").document(hospitalId).collection("booking").document(datalist.get(position).getId())
//                .delete()
//                .addOnCompleteListener(task -> Toast.makeText(PatientsList.this, "Rejected Request Successfully", Toast.LENGTH_LONG).show())
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(PatientsList.this, "Failed to reject request", Toast.LENGTH_LONG).show();
//                    }
//                });
//    }


    public void updateBedData(model dardi) {
        Log.d("TAG", "approveBtn() returned: " + dardi.getBedType());
        DocumentReference onetime = db.collection("hospital").document(hospitalId);
        onetime.get()
                .addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    DocumentSnapshot sn = null;

                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        sn = documentSnapshot;
                        int val = documentSnapshot.getLong("nonO2").intValue() - 1;
                        int vacantval = documentSnapshot.getLong("vacant").intValue() - 1;
                        onetime.update(
                                "nonO2", val,
                                "vacant", vacantval
                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                String smsBody = String.format("%s your request for bed has been approved by %s " +
                                        "if you don't reach the hospital within %s" +
                                        "\n your request will get cancelled\n" +
                                        "\n unique code for verification at hospital is : %s " +
                                        //"\n bed allocated to you is %s bed " +
                                        "Regards,\n %s", dardi.getName(), sn.get("name"), dardi.getTime(), dardi.getId().substring(0,5), sn.get("name"));
                                String dardiKaNumber = dardi.getPhone();
                                Toast.makeText(PatientsList.this, "Approved request successfully", Toast.LENGTH_SHORT).show();
                                Toast.makeText(PatientsList.this, "SMS sent to patient", Toast.LENGTH_LONG).show();
                                Log.e("SMSERR","SMS"+smsBody);
                                Log.e("SMSERR","contact "+dardiKaNumber);

//                                SmsHelper asyncT = new SmsHelper();
//                                asyncT.execute(dardiKaNumber, smsBody);
//                                PatientsList.this.onBackPressed();
                            }
                        });

                    }
                });
    }

    public void removeData(int position) {

        db.collection("hospital").document(hospitalId).collection("booking").document(datalist.get(position).getId())
                .delete();
    }


}
