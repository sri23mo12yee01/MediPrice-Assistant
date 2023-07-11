package com.mediprice.hospitals;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.mediprice.hospitals.Helpers.SpHelper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoginHospital extends AppCompatActivity {
    EditText nameHospital,phoneHospital,pwdHospital,emailHospital;
    FirebaseAuth mAuth;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    FirebaseFirestore db;
    TextView forgotpwd;
    Intent register,hospitaldash;
    private RadioGroup userTypeSelection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_login_hospital);
        register = new Intent(this,SigningHospital.class);
        hospitaldash = new Intent(this,HospitalDashboard.class);

        // Check if user already logged in
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        SpHelper.Role currentUserRole=SpHelper.getRoll(this);
        if(mAuth.getCurrentUser()!=null){
            startActivity(hospitaldash);
            finish();
        }else if("C".equals(currentUserRole.getRole())){
            //show control room
            Intent intent = new Intent(LoginHospital.this,ControlRoom.class);
            startActivity(intent);
            finish();
        }



        nameHospital = findViewById(R.id.nameHospital);
        phoneHospital = findViewById(R.id.phoneHospital);

        pwdHospital = findViewById(R.id.pwdHospital);
        emailHospital = findViewById(R.id.emailHospital);
        userTypeSelection=findViewById(R.id.userRadGroup);

//        pwdHospital.setText("covid19");
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btncntrl = findViewById(R.id.controlBtn);
        Button btnHospDash = findViewById(R.id.hospDash);

        btncntrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginHospital.this,ControlRoom.class);
                startActivity(intent);
            }
        });

        btnHospDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(hospitaldash);
            }
        });

        forgotpwd = findViewById(R.id.forgotPassword);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(firebaseRemoteConfigSettings);
        firebaseRemoteConfig.setDefaultsAsync(R.xml.email_default_values);
        getValueFromFireBaseCOnfig();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        forgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Receive Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String mail = resetMail.getText().toString();
                     mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginHospital.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginHospital.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });

                passwordResetDialog.create().show();

            }
        });
    }
    private void getValueFromFireBaseCOnfig() {
        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if(task.isSuccessful())
                        {
                            Log.i("LoginHospital", String.valueOf(task.getResult()));
                            String cemail = firebaseRemoteConfig.getString("control_email");
                        }
                        else{
                            Toast.makeText(LoginHospital.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loginUser() {
        String email = emailHospital.getText().toString().trim();
        String pwd = pwdHospital.getText().toString().trim();
        if(email.isEmpty()){
            emailHospital.setError("Enter email");
            emailHospital.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailHospital.setError("Please enter a valid email");
            emailHospital.requestFocus();
            return;
        }
        if(pwd.isEmpty()){
            pwdHospital.setError("Enter password");
            pwdHospital.requestFocus();
            return;
        }
        if(pwd.length()<6)
        {
            pwdHospital.setError("Minimum password length should be 6 characters");
            pwdHospital.requestFocus();
            return;
        }

        int selecedType=userTypeSelection.getCheckedRadioButtonId();
        RadioButton userType=findViewById(selecedType);
        String item = (String) userType.getText(); //spinnerUser.getSelectedItem().toString();
        String enteredEmail= emailHospital.getText().toString();
        String enteredPass= pwdHospital.getText().toString();
        String controlRoomEmailFromDb=firebaseRemoteConfig.getString("control_email");
        String docId = firebaseRemoteConfig.getString("doc_id");

        if(item.equals("Control Room")){
            db.collection("controlroom").document(docId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists()){
                                String enterPwd = task.getResult().getString("pwd");
                                if( item.equals("Control Room") && (enteredEmail.equals(controlRoomEmailFromDb)) && (enteredPass.equals(enterPwd)))
                                {
                                    SpHelper.saveRoll(LoginHospital.this,docId,"C");
                                    Intent intent = new Intent(LoginHospital.this,ControlRoom.class);
                                    startActivity(intent);
                                    return;
                                }else{
                                    Toast.makeText(LoginHospital.this, "CR : Invalid Login", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }else{
            mAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        //
                        db.collection("hospital").get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot d:list)
                                        {
                                            if(d.get("email").toString().equals(email)){
                                                Toast.makeText(LoginHospital.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                                SpHelper.saveRoll(LoginHospital.this,d.getId(),"H");
                                                startActivity(hospitaldash);
                                                return;
                                            }
                                        }
                                        Toast.makeText(LoginHospital.this, "HSP : Invalid Login", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else{
                        Toast.makeText(LoginHospital.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }



}
