package com.ojambrina.ipatient.UI.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.UI.clinics.CreateClinicActivity;
import com.ojambrina.ipatient.UI.clinics.patients.AddPatient;
import com.ojambrina.ipatient.UI.home.assistance.TecnicAssistanceActivity;
import com.ojambrina.ipatient.UI.login.LoginActivity;
import com.ojambrina.ipatient.adapters.ClinicAdapter;
import com.ojambrina.ipatient.adapters.PatientAdapter;
import com.ojambrina.ipatient.entities.Clinic;
import com.ojambrina.ipatient.entities.ConnectedClinic;
import com.ojambrina.ipatient.entities.Patient;
import com.ojambrina.ipatient.entities.Professional;
import com.ojambrina.ipatient.utils.AppPreferences;
import com.ojambrina.ipatient.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.ojambrina.ipatient.utils.Constants.CLINICS;
import static com.ojambrina.ipatient.utils.Constants.CLINIC_NAME;
import static com.ojambrina.ipatient.utils.Constants.CONNECTED_CLINIC_LIST;
import static com.ojambrina.ipatient.utils.Constants.HOME_TUTORIAL;
import static com.ojambrina.ipatient.utils.Constants.LATEST_CLINIC;
import static com.ojambrina.ipatient.utils.Constants.NO_CLINIC_ADDED;
import static com.ojambrina.ipatient.utils.Constants.PATIENTS;
import static com.ojambrina.ipatient.utils.Constants.PROFESSIONAL;
import static com.ojambrina.ipatient.utils.Constants.PROFESSIONALS;
import static com.ojambrina.ipatient.utils.Constants.SHARED_PREFERENCES;

public class HomeActivity extends AppCompatActivity {

    //Butterknife
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress_bar_patients)
    ProgressBar progressBarPatients;
    @BindView(R.id.home_text_no_data)
    TextView homeTextNoData;
    @BindView(R.id.drawer_text_no_data)
    TextView drawerTextNoData;
    @BindView(R.id.recycler_patients)
    RecyclerView recyclerPatients;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.text_name)
    TextView textName;
    @BindView(R.id.text_surname)
    TextView textSurname;
    @BindView(R.id.layout_user)
    LinearLayout layoutUser;
    @BindView(R.id.progress_bar_drawer)
    ProgressBar progressBarDrawer;
    @BindView(R.id.recycler_clinic)
    RecyclerView recyclerClinic;
    @BindView(R.id.text_add_clinic)
    TextView textAddClinic;
    @BindView(R.id.text_connect_clinic)
    TextView textConnectClinic;
    @BindView(R.id.text_configuration)
    TextView textConfiguration;
    @BindView(R.id.text_tecnic_assistance)
    TextView textTecnicAssistance;
    @BindView(R.id.text_logout)
    TextView textLogout;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.text_start)
    TextView textStart;
    @BindView(R.id.layout_start)
    LinearLayout layoutStart;
    @BindView(R.id.text_welcome)
    TextView textWelcome;
    @BindView(R.id.text_patient_list)
    TextView textPatientList;
    @BindView(R.id.button_understand)
    Button buttonUnderstand;
    @BindView(R.id.layout_background_tutorial)
    RelativeLayout layoutBackgroundTutorial;

    //Declarations
    private List<Clinic> clinicList = new ArrayList<>();
    private List<Patient> patientList = new ArrayList<>();
    private List<ConnectedClinic> connectedClinicList = new ArrayList<>();
    private ClinicAdapter clinicAdapter;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase firebaseDatabase;
    private ListenerRegistration firebaseListener;
    private Context context;
    private AppCompatActivity contextForToolbar;
    private PatientAdapter patientAdapter;
    private String clinicName;
    private String latestClinic;
    private String clinicPassword;
    private String email;
    private AppPreferences appPreferences;
    private SharedPreferences sharedPreferences;
    private FirebaseUser firebaseUser;
    private String name;
    private boolean isValidClinicName;
    private Intent intent;
    private Professional professional;
    private String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        context = this;
        contextForToolbar = this;
        appPreferences = new AppPreferences();
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        latestClinic = sharedPreferences.getString(LATEST_CLINIC, NO_CLINIC_ADDED);

        setFirebase();
        getProfessional();
        setToolbar();

        setDrawerAdapter();
        setPatientAdapter();

        setPatientList();

        drawerListeners();
        listeners();
    }

    private void loadTutorial() {
        if (!sharedPreferences.getBoolean(HOME_TUTORIAL, false)) {
            layoutBackgroundTutorial.setVisibility(View.VISIBLE);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            homeTextNoData.setVisibility(View.GONE);
            progressBarPatients.setVisibility(View.GONE);
            String professionalName = professional.getName() + " " + professional.getSurname();
            textWelcome.setText(professionalName);
        }
    }

    private void setDrawerData() {
        if (professional.getImage() != null) {
            Utils.loadGlide(context, professional.getImage(), profileImage);
        } else {
            profileImage.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_person_black_24dp));
        }
        textName.setText(professional.getName());
        textSurname.setText(professional.getSurname());
        if (latestClinic.equals(NO_CLINIC_ADDED)) {
            toolbar.setTitle(null);
        } else {
            toolbar.setTitle(latestClinic);
        }
    }

    private void getProfessional() {
        email = firebaseUser.getEmail();

        firebaseFirestore.collection(PROFESSIONALS).addSnapshotListener((queryDocumentSnapshots, e) -> {
            List<Professional> professionalList = queryDocumentSnapshots.toObjects(Professional.class);

            for (int i = 0; i < professionalList.size(); i++) {
                if (professionalList.get(i).getEmail().equals(email)) {
                    professional = professionalList.get(i);

                    loadTutorial();
                    setClinicList();
                    setDrawerData();
                }
            }
        });
    }

    private void setFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setToolbar() {
        Utils.configToolbar(contextForToolbar, toolbar);
    }

    private void setDrawerAdapter() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.black));

        clinicAdapter = new ClinicAdapter(context, connectedClinicList, sharedPreferences, (position, connectedClinic) -> {
            latestClinic = connectedClinicList.get(position).getName();
            toolbar.setTitle(latestClinic);

            if (sharedPreferences.getString(LATEST_CLINIC, "").isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(LATEST_CLINIC, latestClinic);
                editor.apply();
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(LATEST_CLINIC, latestClinic);
                editor.apply();
            }

            clinicAdapter.notifyDataSetChanged();

            setPatientList();
            patientAdapter.notifyDataSetChanged();
            drawerLayout.closeDrawer(Gravity.START);
        });
        recyclerClinic.setAdapter(clinicAdapter);
    }

    private void setPatientAdapter() {
        patientAdapter = new PatientAdapter(context, patientList, latestClinic);
        GridLayoutManager layout = new GridLayoutManager(context, 3);
        recyclerPatients.setLayoutManager(layout);
        recyclerPatients.setAdapter(patientAdapter);
    }

    private void setClinicList() {
        fullName = professional.getName() + " " + professional.getSurname();
        firebaseFirestore.collection(PROFESSIONALS).document(fullName).collection(CONNECTED_CLINIC_LIST).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("ERROR", "Listen failed.", e);
                    return;
                }
                List<ConnectedClinic> list = queryDocumentSnapshots.toObjects(ConnectedClinic.class);

                if (!sharedPreferences.getBoolean(HOME_TUTORIAL, false)) {
                    layoutBackgroundTutorial.setVisibility(View.VISIBLE);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    if (list.size() != 0) {
                        connectedClinicList.clear();
                        connectedClinicList.addAll(list);
                        drawerTextNoData.setVisibility(View.GONE);
                        progressBarDrawer.setVisibility(View.GONE);
                        recyclerClinic.setVisibility(View.VISIBLE);
                        clinicAdapter.setData(connectedClinicList);
                        fab.show();
                        homeTextNoData.setVisibility(View.GONE);
                        progressBarPatients.setVisibility(View.GONE);
                    } else {
                        fab.hide();
                        progressBarDrawer.setVisibility(View.GONE);
                        drawerTextNoData.setVisibility(View.VISIBLE);
                        homeTextNoData.setVisibility(View.GONE);
                        progressBarPatients.setVisibility(View.GONE);
                    }
                } else {
                    if (list.size() != 0) {
                        connectedClinicList.clear();
                        connectedClinicList.addAll(list);
                        drawerTextNoData.setVisibility(View.GONE);
                        progressBarDrawer.setVisibility(View.GONE);
                        recyclerClinic.setVisibility(View.VISIBLE);
                        clinicAdapter.setData(connectedClinicList);
                        fab.show();
                    } else {
                        fab.hide();
                        progressBarDrawer.setVisibility(View.GONE);
                        drawerTextNoData.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        firebaseFirestore.collection(CLINICS).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e);
                return;
            }

            List<Clinic> list = queryDocumentSnapshots.toObjects(Clinic.class);

            clinicList.addAll(list);
        });
    }

    private void setPatientList() {
        firebaseFirestore.collection(CLINICS).document(latestClinic).collection(PATIENTS).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e);
                return;
            }

            List<Patient> list = queryDocumentSnapshots.toObjects(Patient.class);

            patientList.clear();
            patientList.addAll(list);
            progressBarPatients.setVisibility(View.GONE);

            if (!sharedPreferences.getBoolean(HOME_TUTORIAL, false)) {
                layoutBackgroundTutorial.setVisibility(View.VISIBLE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                homeTextNoData.setVisibility(View.GONE);
                progressBarPatients.setVisibility(View.GONE);
            } else {
                if (patientList.size() == 0) {
                    homeTextNoData.setVisibility(View.VISIBLE);
                    recyclerPatients.setVisibility(View.GONE);
                    fab.show();
                } else {
                    homeTextNoData.setVisibility(View.GONE);
                    recyclerPatients.setVisibility(View.VISIBLE);
                    fab.show();
                }
            }

            patientAdapter.notifyDataSetChanged();
            Log.d("INFO", "Current patients in clinic: " + list);
        });
    }

    private void drawerListeners() {
        textAddClinic.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateClinicActivity.class);
            intent.putExtra(PROFESSIONAL, professional);
            startActivity(intent);
            drawerLayout.closeDrawer(Gravity.START);
            finish();
        });

        textConnectClinic.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);

            Dialog dialog = new Dialog(context);

            dialog.setContentView(R.layout.dialog_connect_clinic);
            dialog.setCancelable(false);

            EditText editName = dialog.findViewById(R.id.edit_clinic_name);
            EditText editPassword = dialog.findViewById(R.id.edit_clinic_password);
            ImageView imageCancel = dialog.findViewById(R.id.image_close);
            TextView textSend = dialog.findViewById(R.id.text_send);

            imageCancel.setOnClickListener(v1 -> dialog.cancel());

            textSend.setOnClickListener(v12 -> {
                validateClinicName(editName);


                if (isValidClinicName) {
                    clinicName = editName.getText().toString().trim();
                    clinicPassword = editPassword.getText().toString().trim();
                    if (clinicPassword.length() > 0) {
                        for (int i = 0; i < clinicList.size(); i++) {
                            if (clinicList.get(i).getName().equals(clinicName)) {
                                if (clinicList.get(i).getPassword().equals(clinicPassword)) {
                                    ConnectedClinic connectedClinic = new ConnectedClinic();
                                    connectedClinic.setName(clinicList.get(i).getName());
                                    connectedClinic.setImage(clinicList.get(i).getImage());
                                    connectedClinicList.add(connectedClinic);
                                    firebaseFirestore.collection(PROFESSIONALS).document(fullName).collection(CONNECTED_CLINIC_LIST).document(clinicList.get(i).getName()).set(connectedClinic);

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(LATEST_CLINIC, clinicList.get(i).getName());
                                    editor.apply();
                                    dialog.dismiss();

                                    setClinicList();
                                    setPatientList();
                                    updateClinicList(connectedClinicList);
                                    updatePatientList(clinicList.get(i).getName());
                                    updateTitle(clinicList.get(i).getName());
                                    break;
                                } else {
                                    editName.setError("Datos de inicio de sesion incorrectos");
                                    editPassword.setError("Datos de inicio de sesion incorrectos");
                                    editName.requestFocus();
                                    break;
                                }
                            } else {
                                editName.setError("Datos de inicio de sesion incorrectos");
                                editPassword.setError("Datos de inicio de sesion incorrectos");
                                editName.requestFocus();
                            }
                        }
                    } else {
                        editName.setError("Datos de inicio de sesion incorrectos");
                        editPassword.setError("Datos de inicio de sesion incorrectos");
                        editName.requestFocus();
                    }
                } else {
                    editPassword.setError("El campo no puede estar vacío");
                    editName.requestFocus();
                }
            });
            dialog.show();
        });

        textConfiguration.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(context, ConfigurationActivity.class);
            startActivity(intent);
        });

        textTecnicAssistance.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(context, TecnicAssistanceActivity.class);
            startActivity(intent);
        });

        textLogout.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void updateClinicList(List<ConnectedClinic> list) {
        clinicAdapter.setData(list);
    }

    private void updatePatientList(String name) {
        firebaseListener = firebaseFirestore.collection(CLINICS).document(name).collection(PATIENTS).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e);
                return;
            }

            List<Patient> list = queryDocumentSnapshots.toObjects(Patient.class);

            patientList.clear();
            patientList.addAll(list);

            if (patientList.size() == 0) {
                homeTextNoData.setVisibility(View.VISIBLE);
                recyclerPatients.setVisibility(View.GONE);
                fab.hide();
            } else {
                homeTextNoData.setVisibility(View.GONE);
                recyclerPatients.setVisibility(View.VISIBLE);
                fab.show();
            }

            patientAdapter.notifyDataSetChanged();
        });
    }

    private void updateTitle(String name) {
        toolbar.setTitle(name);
    }

    private void listeners() {
        buttonUnderstand.setOnClickListener(v -> {
            layoutBackgroundTutorial.setVisibility(View.GONE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(HOME_TUTORIAL, true);
            editor.apply();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            homeTextNoData.setVisibility(View.VISIBLE);
        });

        fab.setOnClickListener(v -> addPatient());
    }

    private void addPatient() {
        Intent intent = new Intent(context, AddPatient.class);
        intent.putExtra(CLINIC_NAME, latestClinic);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawers();
        else
            super.onBackPressed();
    }

    //Validations
    private void validateClinicName(EditText editClinicName) {
        name = editClinicName.getText().toString().trim();
        if (name.length() > 0) {
            isValidClinicName = true;
        } else {
            editClinicName.requestFocus();
            editClinicName.setError("El campo nombre no puede estar vacío");
            isValidClinicName = false;
        }
    }
}