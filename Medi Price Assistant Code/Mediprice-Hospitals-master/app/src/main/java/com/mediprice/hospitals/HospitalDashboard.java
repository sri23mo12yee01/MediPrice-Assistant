package com.mediprice.hospitals;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mediprice.hospitals.Helpers.SpHelper;

public class HospitalDashboard extends AppCompatActivity {
    EditText vacantBeds,o2Beds,nonO2Beds,icuBeds,ventilatorBeds;
    Button bedRequests, logoutBtn,manageBedsBtn, addPatientBtn,viewMyPatients; //updateBtn, updateO2Btn, updateNonO2Btn, updateIcuBtn, updateVentilatorBtn;
    Intent requests;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String hospitalId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_hospital_dashboard);
        bedRequests = findViewById(R.id.bedRequests);
        vacantBeds = findViewById(R.id.price);
        o2Beds = findViewById(R.id.o2Beds);
        nonO2Beds = findViewById(R.id.nonO2Beds);
        icuBeds = findViewById(R.id.icuBeds);
        ventilatorBeds = findViewById(R.id.ventilatorBeds);
        manageBedsBtn = findViewById(R.id.manageBedsBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        addPatientBtn = findViewById(R.id.addPatientBtn);
        viewMyPatients= findViewById(R.id.viewMyPatients);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        requests = new Intent(this, PatientsList.class);

        bedRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(requests);
                Intent request = new Intent(HospitalDashboard.this,BedRequests.class);
                startActivity(request);
            }
        });
        manageBedsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manage = new Intent(HospitalDashboard.this,UpdateBedData.class);
                startActivity(manage);
            }
        });

//        updateBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateValue();
//            }
//        });
//
        viewMyPatients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent add = new Intent(HospitalDashboard.this,AddPatient.class);
//                startActivity(add);
                String dbHospitalId= SpHelper.getRoll(HospitalDashboard.this).getId();
                Intent viewPat = new Intent(HospitalDashboard.this,DetailsPatient.class);
                viewPat.putExtra("id",dbHospitalId);
                startActivity(viewPat);
            }
        });
        addPatientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(HospitalDashboard.this,AddPatient.class);
                startActivity(add);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                SpHelper.clearRole(HospitalDashboard.this);
                startActivity(new Intent(HospitalDashboard.this, LoginHospital.class));
            }
        });
//
//        db.collection("hospital").document(hospitalId).get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                        if (task.isSuccessful()) {
//                            System.out.println("FETCH SUCC"+task.getResult().getString("email"));
//                            if (task.getResult().exists()) {
//                                String userName = task.getResult().getString("email");
//
////                                String vacBed = String.valueOf(task.getResult().getLong("vacant"));
////                                String o2Bed = String.valueOf(task.getResult().getLong("o2"));
////                                String nonO2Bed = String.valueOf(task.getResult().getLong("nonO2"));
////                                String icuBed = String.valueOf(task.getResult().getLong("icu"));
////                                String ventilatorBed = String.valueOf(task.getResult().getLong("ventilator"));
//                                name.setText(userName);
////                                vacbed.setText(vacBed);
////                                o2Beds.setText(o2Bed);
////                                nonO2Beds.setText(nonO2Bed);
////                                icuBeds.setText(icuBed);
////                                ventilatorBeds.setText(ventilatorBed);
//                            }
//                        }
//                    }
//                });
    }

/*    private void updateValue() {
        int vacant = 0;
        if(vacantBeds.getText().toString().isEmpty()){
            vacantBeds.setError("Enter vacant beds");
            vacantBeds.requestFocus();
            return;
        }else{
            vacant= Integer.parseInt(vacantBeds.getText().toString());
            hospitalModel m = new hospitalModel(
                    vacant
            );
            db.collection("hospital").document(hospitalId)
                    .update(
                            "vacant",m.getVacant()
                    )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(HospitalDashboard.this, "Vacant bed count updated successfully", Toast.LENGTH_LONG).show();
                        }
                    });
            clearData();
        }

 */
        //String vacant = vacantBeds.getText().toString().trim();
/*        if(!hasValidationErrors(vacant))
        {
            hospitalModel m = new hospitalModel(
                    vacant
            );
            db.collection("hospital").document(hospitalId)
                    .update(
                            "vacant",m.getVacant()
                    )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(HospitalDashboard.this, "Bed count updated successfully", Toast.LENGTH_LONG).show();
                        }
                    });
        }
 */
    }


