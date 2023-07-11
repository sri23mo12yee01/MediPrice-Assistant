package com.mediprice.hospitals.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.mediprice.hospitals.BedRequests;
import com.mediprice.hospitals.MyUtils;
import com.mediprice.hospitals.R;
import com.mediprice.hospitals.model.PatientModel;
import com.mediprice.hospitals.smsIntegration.SmsHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.myviewholder> {
    BedRequests context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Button approveBtn, rejectBtn;
    Spinner spinner;
    String hospitalId;
    String bedType;
    ArrayList<PatientModel> datalist1;
    ArrayAdapter<String> hospitalList;
    ArrayAdapter<CharSequence> bedAdapter;
    private final List<DocumentSnapshot> hospitalSnapList = new ArrayList<>();

    ArrayList<String> names = new ArrayList<String>();

    CollectionReference namesRef = db.collection("hospital");
    private int position;
    private String typeBed;
    private boolean isHospital;

    public RequestsAdapter(ArrayList<PatientModel> datalist1, BedRequests context, boolean isHospital) {
        this.datalist1 = datalist1;
        this.context = context;
        this.isHospital = isHospital;
        System.out.println(isHospital);
        hospitalList = new ArrayAdapter<>(context.getApplicationContext(), android.R.layout.simple_spinner_item, names);
        bedAdapter = ArrayAdapter.createFromResource(context.getApplicationContext(), R.array.bed_type, android.R.layout.simple_spinner_item);


        namesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        hospitalSnapList.add(document);
                        String documentId = document.getId();
                        String hospname = document.getString("name");
                        names.add(hospname);
                    }
                }
            }
        });

    }

    @NonNull
    @Override
    public RequestsAdapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_details, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsAdapter.myviewholder holder, int position) {
        holder.t1.setText(datalist1.get(position).getName());
        holder.t2.setText(datalist1.get(position).getAge());
        holder.t3.setText(datalist1.get(position).getGender());
        holder.t4.setText(datalist1.get(position).getSymptoms());
        holder.t5.setText(datalist1.get(position).getTime());
        holder.t6.setText(datalist1.get(position).getPhone());
        if (datalist1.get(position).getGenId() == null) {
            holder.t7.setText("N/A");
        }
        ArrayList<String> paths = datalist1.get(position).getImages();
        if (paths != null) {
            if (paths.size() == 1) {
                Glide.with(context)
                        .load(paths.get(0))
                        .into(holder.i1);
            } else if (paths.size() == 2) {
                Glide.with(context)
                        .load(paths.get(0))
                        .into(holder.i1);
                Glide.with(context)
                        .load(paths.get(1))
                        .into(holder.i2);
            }
        }
        PatientModel current = datalist1.get(position);
        holder.setData(current, position);
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return datalist1.size();
    }

    public class myviewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView t1, t2, t3, t4, t5, t6, t7;
        ImageView i1, i2;
        Button allocateBtn, rejectBtn, generateBtn;
        private int position;
        Spinner spinner, spinnerBed;

        public myviewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            t1 = itemView.findViewById(R.id.t1);
            t2 = itemView.findViewById(R.id.t2);
            t3 = itemView.findViewById(R.id.t3);
            t4 = itemView.findViewById(R.id.t4);
            t5 = itemView.findViewById(R.id.t5);
            t6 = itemView.findViewById(R.id.t6);
            t7 = itemView.findViewById(R.id.t7);
            allocateBtn = itemView.findViewById(R.id.allocateBtn);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);


            spinner = itemView.findViewById(R.id.spinner);
            spinnerBed = itemView.findViewById(R.id.spinnerBed);
            hospitalList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(hospitalList);
            bedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBed.setAdapter(bedAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    hospitalId = RequestsAdapter.this.hospitalSnapList.get(position).getId();
                    spinner.setSelection(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinnerBed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    bedType = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            //hide spinner for hospital user
            if (isHospital) {
                spinnerBed.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                itemView.findViewById(R.id.textView17).setVisibility(View.GONE);
                itemView.findViewById(R.id.textView18).setVisibility(View.GONE);
            }
            i1 = itemView.findViewById(R.id.i1);
            i2 = itemView.findViewById(R.id.i2);
        }

        public void setData(PatientModel currentObject, int position) {
            this.position = position;
        }

        public void setListeners() {
            allocateBtn.setOnClickListener(myviewholder.this);
            rejectBtn.setOnClickListener(myviewholder.this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.allocateBtn:
                    allocateHospital(datalist1.get(this.position));
                    removeItem(position);

                    break;
                case R.id.rejectBtn:
                    rejectRequest(position);

                    break;
//                case R.id.generateBtn:
//                    generateCode(6);
            }
        }
    }

    private void removeItem(int position) {
        db.collection("patient").document(datalist1.get(position).getId())
                .delete();
        datalist1.remove(position);
        notifyDataSetChanged();


    }

    private void rejectRequest(int position) {
        PatientModel tempP=datalist1.get(position);
        db.collection("patient").document(datalist1.get(position).getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener() {
                                           @Override
                                           public void onComplete(@NonNull Task task) {
                                               Toast.makeText(context.getApplicationContext(), "Rejected Request & Notified to patient", Toast.LENGTH_SHORT).show();
                                               requestRejectSMS(tempP);
                                           }
                                       }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(context.getApplicationContext(), "Failed to reject request", Toast.LENGTH_SHORT).show();
                    }
                });
        datalist1.remove(position);
        notifyDataSetChanged();
    }

    private void allocateHospital(PatientModel patDet) {
        CollectionReference hospitalRef;
        patDet.setGenId(MyUtils.getTimeInMs());
        if (isHospital) {
            patDet.setBedType("nonO2");
            hospitalId = patDet.getSelectedHospitalId();
        } else {
            patDet.setBedType(bedType);
        }

        if (hospitalId != null) {
            hospitalRef = FirebaseFirestore.getInstance().collection("hospital").document(hospitalId).collection("booking");
        } else {
            Toast.makeText(context.getApplicationContext(), "Select a hospital", Toast.LENGTH_LONG).show();
            return;
        }
        String newId = hospitalRef.document().getId();
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
//        batch.update(hospitalRef.document(), new HashMap() {{
//            put("bedType", "something");
//        }});
        batch.set(hospitalRef.document(newId), patDet, SetOptions.merge());
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            private int position;

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateBeds(patDet);
                Toast.makeText(context.getApplicationContext(), "Hospital allocated successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context.getApplicationContext(), "Failed to allocate hospital", Toast.LENGTH_SHORT).show();
            }
        });
//
    }

    private void updateBeds(PatientModel dardi) {
        context.updateBedData(dardi);
        this.updateBedData(dardi);
    }

    //////
    public void updateBedData(PatientModel dardi) {
        DocumentReference onetime = db.collection("hospital").document(hospitalId);
        onetime.get()
                .addOnSuccessListener(context, new OnSuccessListener<DocumentSnapshot>() {
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
                                        "Regards,\n %s", dardi.getName(), sn.get("name"), dardi.getTime(), dardi.getGenId(), sn.get("name"));
                                String dardiKaNumber = dardi.getPhone();
                                Toast.makeText(context, "Approved request successfully", Toast.LENGTH_SHORT).show();
                                Toast.makeText(context, "SMS sent to patient", Toast.LENGTH_LONG).show();
                                Log.e("SMSERR", "SMS" + smsBody);
                                Log.e("SMSERR", "contact " + dardiKaNumber);

                                SmsHelper asyncT = new SmsHelper();
                                asyncT.execute(dardiKaNumber, smsBody);
                            }
                        });

                    }
                });
    }

    private void requestRejectSMS(PatientModel dardi) {
        String smsBody = String.format("%s your request is REJECTED by hospital. Please consider visiting nearby hospitals", dardi.getName());
        String dardiKaNumber = dardi.getPhone();
        Toast.makeText(context, "Approved request successfully", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "SMS sent to patient", Toast.LENGTH_LONG).show();
        Log.e("SMSERR-REJECT", "SMS" + smsBody);
        Log.e("SMSERR-REJECT", "contact " + dardiKaNumber);

                                SmsHelper asyncT = new SmsHelper();
                                asyncT.execute(dardiKaNumber, smsBody);
    }
}
