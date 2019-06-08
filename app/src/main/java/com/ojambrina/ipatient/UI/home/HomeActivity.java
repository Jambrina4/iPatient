package com.ojambrina.ipatient.UI.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.UI.clinics.CreateClinicActivity;
import com.ojambrina.ipatient.UI.clinics.patients.AddPatient;
import com.ojambrina.ipatient.UI.login.LoginActivity;
import com.ojambrina.ipatient.adapters.ClinicAdapter;
import com.ojambrina.ipatient.adapters.PatientAdapter;
import com.ojambrina.ipatient.entities.Clinic;
import com.ojambrina.ipatient.entities.Patient;
import com.ojambrina.ipatient.utils.AppPreferences;
import com.ojambrina.ipatient.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ojambrina.ipatient.utils.Constants.CLINICS;
import static com.ojambrina.ipatient.utils.Constants.CLINIC_LIST;
import static com.ojambrina.ipatient.utils.Constants.CLINIC_NAME;
import static com.ojambrina.ipatient.utils.Constants.LATEST_CLINIC;
import static com.ojambrina.ipatient.utils.Constants.NO_CLINIC_ADDED;
import static com.ojambrina.ipatient.utils.Constants.PATIENTS;
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
    @BindView(R.id.text_email)
    TextView textEmail;
    @BindView(R.id.layout_user)
    LinearLayout layoutUser;
    @BindView(R.id.separator1)
    View separator1;
    @BindView(R.id.progress_bar_drawer)
    ProgressBar progressBarDrawer;
    @BindView(R.id.recycler_clinic)
    RecyclerView recyclerClinic;
    @BindView(R.id.separator2)
    View separator2;
    @BindView(R.id.text_add_clinic)
    TextView textAddClinic;
    @BindView(R.id.separator3)
    View separator3;
    @BindView(R.id.text_connect_clinic)
    TextView textConnectClinic;
    @BindView(R.id.separator4)
    View separator4;
    @BindView(R.id.text_logout)
    TextView textLogout;
    @BindView(R.id.separator5)
    View separator5;
    @BindView(R.id.text_assistance)
    TextView textSettings;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    //Declarations
    private List<Clinic> clinicList = new ArrayList<>();
    private List<Clinic> clinicListPreferences = new ArrayList<>();
    private List<Patient> patientList = new ArrayList<>();
    private ClinicAdapter clinicAdapter;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase firebaseDatabase;
    private Context context;
    private AppCompatActivity contextForToolbar;
    private PatientAdapter patientAdapter;
    private String clinicName;
    private String latestClinic;
    private String clinicPassword;
    private AppPreferences appPreferences;
    private SharedPreferences sharedPreferences;
    private FirebaseUser firebaseUser;
    private String name;
    private boolean isValidClinicName;

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
        setToolbar();

        getUser();

        setDrawerAdapter();
        setPatientAdapter();

        setClinicList();
        setPatientList();

        drawerListeners();
        patientListeners();
    }

    private void getUser() {
        //TODO METODO PARA OBTENER EL PROFESIONAL Y PINTAR LOS DATOS EN EL DRAWER
        //firebaseFirestore.collection(PROFESSIONALS).
    }

    private void setFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(CLINICS);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setToolbar() {
        Utils.configToolbar(contextForToolbar, toolbar);
        textEmail.setText(firebaseUser.getEmail());
        if (latestClinic.equals(NO_CLINIC_ADDED)) {
            toolbar.setTitle(null);
        } else {
            toolbar.setTitle(latestClinic);
        }
    }

    private void setDrawerAdapter() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.black));

        clinicAdapter = new ClinicAdapter(context, clinicListPreferences, sharedPreferences, new ClinicAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Clinic clinic) {
                latestClinic = clinicListPreferences.get(position).getName();
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
            }
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
        if (!sharedPreferences.getString(CLINIC_LIST, "").isEmpty()) {
            clinicListPreferences.clear();
            Gson gson = new Gson();
            String json = sharedPreferences.getString(CLINIC_LIST, "");
            Type type = new TypeToken<List<Clinic>>() {
            }.getType();
            clinicListPreferences.addAll(gson.fromJson(json, type));
            drawerTextNoData.setVisibility(View.GONE);
            progressBarDrawer.setVisibility(View.GONE);
            recyclerClinic.setVisibility(View.VISIBLE);
            clinicAdapter.notifyDataSetChanged();
        } else {
            progressBarDrawer.setVisibility(View.GONE);
            drawerTextNoData.setVisibility(View.VISIBLE);
        }

        firebaseFirestore.collection(CLINICS).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("ERROR", "Listen failed.", e);
                    return;
                }

                List<Clinic> list = queryDocumentSnapshots.toObjects(Clinic.class);

                clinicList.addAll(list);
            }
        });
    }

    private void setPatientList() {
        firebaseFirestore.collection(CLINICS).document(latestClinic).collection(PATIENTS).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("ERROR", "Listen failed.", e);
                    return;
                }

                List<Patient> list = queryDocumentSnapshots.toObjects(Patient.class);

                patientList.clear();
                patientList.addAll(list);
                progressBarPatients.setVisibility(View.GONE);

                if (patientList.size() == 0) {
                    homeTextNoData.setVisibility(View.VISIBLE);
                    recyclerPatients.setVisibility(View.GONE);
                } else {
                    homeTextNoData.setVisibility(View.GONE);
                    recyclerPatients.setVisibility(View.VISIBLE);
                }

                patientAdapter.notifyDataSetChanged();
                Log.d("INFO", "Current patients in clinic: " + list);
            }
        });
    }

    private void drawerListeners() {
        textAddClinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateClinicActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        textConnectClinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);

                Dialog dialog = new Dialog(context);

                dialog.setContentView(R.layout.dialog_connect_clinic);
                dialog.setCancelable(false);

                EditText editName = dialog.findViewById(R.id.edit_clinic_name);
                EditText editPassword = dialog.findViewById(R.id.edit_clinic_password);
                ImageView imageCancel = dialog.findViewById(R.id.image_close);
                TextView textSend = dialog.findViewById(R.id.text_send);

                imageCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                textSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        validateClinicName(editName);

                        //TODO Arreglar bug no se actualiza la información al agregar la clinica

                        if (isValidClinicName) {
                            clinicName = editName.getText().toString().trim();
                            clinicPassword = editPassword.getText().toString().trim();
                            if (clinicPassword.length() > 0) {
                                for (int i = 0; i < clinicList.size(); i++) {
                                    if (clinicList.get(i).getName().equals(clinicName)) {
                                        if (clinicList.get(i).getPassword().equals(clinicPassword)) {
                                            if (sharedPreferences.getString(CLINIC_LIST, "").isEmpty()) {
                                                clinicListPreferences.clear();
                                                Gson gson = new Gson();
                                                clinicListPreferences.add(clinicList.get(i));
                                                String listOfClinics = gson.toJson(clinicListPreferences);

                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString(CLINIC_LIST, listOfClinics);
                                                editor.putString(LATEST_CLINIC, clinicList.get(i).getName());
                                                editor.apply();
                                                dialog.dismiss();

                                                setClinicList();
                                                setPatientList();
                                                //updateClinicList(clinicListPreferences);
                                                updatePatientList(clinicList.get(i).getName());
                                                updateTitle(clinicList.get(i).getName());
                                                break;
                                            } else {
                                                clinicListPreferences.clear();
                                                Gson gson = new Gson();
                                                String json = sharedPreferences.getString(CLINIC_LIST, "");

                                                Type type = new TypeToken<List<Clinic>>() {
                                                }.getType();
                                                clinicListPreferences = gson.fromJson(json, type);

                                                clinicListPreferences.add(clinicList.get(i));
                                                String listOfClinics = gson.toJson(clinicListPreferences);

                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString(CLINIC_LIST, listOfClinics);
                                                editor.putString(LATEST_CLINIC, clinicList.get(i).getName());
                                                editor.apply();
                                                dialog.dismiss();

                                                setClinicList();
                                                setPatientList();
                                                //updateClinicList(clinicListPreferences);
                                                updatePatientList(clinicList.get(i).getName());
                                                updateTitle(clinicList.get(i).getName());
                                                break;
                                            }
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

                    }
                });
                dialog.show();
            }
        });

        textLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        textSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);
                Toast.makeText(context, "Asistencia", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, AssistanceActivity.class);
                startActivity(intent);
            }
        });
    }

    //private void updateClinicList(List<Clinic> list) {
    //    clinicAdapter.setData(list);
    //}

    private void updatePatientList(String name) {
        firebaseFirestore.collection(CLINICS).document(name).collection(PATIENTS).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("ERROR", "Listen failed.", e);
                    return;
                }

                List<Patient> list = queryDocumentSnapshots.toObjects(Patient.class);

                patientList.clear();
                patientList.addAll(list);

                homeTextNoData.setVisibility(View.GONE);
                recyclerPatients.setVisibility(View.VISIBLE);

                patientAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateTitle(String name) {
        toolbar.setTitle(name);
    }

    private void patientListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPatient();
            }
        });
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
