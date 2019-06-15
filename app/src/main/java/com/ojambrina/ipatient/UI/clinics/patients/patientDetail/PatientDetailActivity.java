package com.ojambrina.ipatient.UI.clinics.patients.patientDetail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.adapters.ViewPagerAdapter;
import com.ojambrina.ipatient.entities.Patient;
import com.ojambrina.ipatient.utils.Utils;

import java.io.Serializable;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ojambrina.ipatient.utils.Constants.CLINICS;
import static com.ojambrina.ipatient.utils.Constants.CLINIC_NAME;
import static com.ojambrina.ipatient.utils.Constants.PATIENT;
import static com.ojambrina.ipatient.utils.Constants.PATIENTS;
import static com.ojambrina.ipatient.utils.Constants.PATIENT_NAME;
import static com.ojambrina.ipatient.utils.Constants.SHARED_PREFERENCES;

public class PatientDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager pager;

    private Context context;
    private AppCompatActivity contextForToolbar;
    private Intent intent;
    private String patientName;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private String clinic_name;
    private SharedPreferences sharedPreferences;
    private ViewPagerAdapter viewPagerAdapter;
    private Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);
        ButterKnife.bind(this);

        intent = getIntent();
        patientName = intent.getStringExtra(PATIENT_NAME);
        clinic_name = intent.getStringExtra(CLINIC_NAME);
        patient = (Patient) intent.getSerializableExtra(PATIENT);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        contextForToolbar = this;
        context = this;

        setToolbar();
        setFirebase();
        setAdapter();
        listeners();
    }

    private void setToolbar() {
        Utils.configToolbar(contextForToolbar, toolbar);
        toolbar.setTitle("Rehabilitación");
    }

    private void setFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(CLINICS);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setAdapter() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), context, patient, clinic_name, patientName, firebaseFirestore);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(context, android.R.color.black));
        pager.setAdapter(viewPagerAdapter);
    }

    private void listeners() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}