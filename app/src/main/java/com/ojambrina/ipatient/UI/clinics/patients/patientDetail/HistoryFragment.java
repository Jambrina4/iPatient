package com.ojambrina.ipatient.UI.clinics.patients.patientDetail;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.adapters.PatientMedicConditionsAdapter;
import com.ojambrina.ipatient.adapters.PatientMedicExaminationAdapter;
import com.ojambrina.ipatient.adapters.PatientRegularExerciseAdapter;
import com.ojambrina.ipatient.adapters.PatientRegularMedicationAdapter;
import com.ojambrina.ipatient.adapters.PatientSurgicalOperationsAdapter;
import com.ojambrina.ipatient.entities.Patient;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.ojambrina.ipatient.utils.Constants.CLINICS;
import static com.ojambrina.ipatient.utils.Constants.CLINIC_NAME;
import static com.ojambrina.ipatient.utils.Constants.PATIENT;
import static com.ojambrina.ipatient.utils.Constants.PATIENTS;
import static com.ojambrina.ipatient.utils.Constants.PATIENT_NAME;

public class HistoryFragment extends Fragment {

    //Butterknife
    @BindView(R.id.image_patient)
    CircleImageView imagePatient;
    @BindView(R.id.text_patient_name)
    TextView textPatientName;
    @BindView(R.id.text_patient_surname)
    TextView textPatientSurname;
    @BindView(R.id.text_born_date)
    TextView textBornDate;
    @BindView(R.id.text_phone)
    TextView textPhone;
    @BindView(R.id.text_email)
    TextView textEmail;
    @BindView(R.id.text_profession)
    TextView textProfession;
    @BindView(R.id.recycler_medication)
    RecyclerView recyclerMedication;
    @BindView(R.id.recycler_medic_conditions)
    RecyclerView recyclerMedicConditions;
    @BindView(R.id.recycler_regular_exercise)
    RecyclerView recyclerRegularExercise;
    @BindView(R.id.recycler_surgical_operations)
    RecyclerView recyclerSurgicalOperations;
    @BindView(R.id.recycler_medic_examination)
    RecyclerView recyclerMedicExamination;
    @BindView(R.id.image_add_medication)
    ImageView imageAddMedication;
    @BindView(R.id.image_add_medic_condition)
    ImageView imageAddMedicCondition;
    @BindView(R.id.image_add_exercise)
    ImageView imageAddExercise;
    @BindView(R.id.image_add_surgical_operation)
    ImageView imageAddSurgicalOperation;
    @BindView(R.id.image_add_medic_examination)
    ImageView imageAddMedicExamination;

    private Context context;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private Patient patient;
    private String patientName;
    private String clinicName;
    private PatientRegularMedicationAdapter patientRegularMedicationAdapter;
    private PatientMedicConditionsAdapter patientMedicConditionsAdapter;
    private PatientRegularExerciseAdapter patientRegularExerciseAdapter;
    private PatientSurgicalOperationsAdapter patientSurgicalOperationsAdapter;
    private PatientMedicExaminationAdapter patientMedicExaminationAdapter;
    private List<String> regularMedication = new ArrayList<>();
    private List<String> medicConditions = new ArrayList<>();
    private List<String> regularExercise = new ArrayList<>();
    private List<String> surgicalOperations = new ArrayList<>();
    private List<String> medicExamination = new ArrayList<>();
    Unbinder unbinder;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            patient = (Patient) getArguments().get(PATIENT);
            patientName = (String) getArguments().get(PATIENT_NAME);
            clinicName = (String) getArguments().get(CLINIC_NAME);
        }

        context = getContext();

        setFirebase();
        getPatientData();
        listeners();

        return view;
    }

    //TODO Revisar metodo de recogida de datos de paciente

    private void getPatientData() {
        firebaseFirestore.collection(CLINICS).document(clinicName).collection(PATIENTS).document(patientName).addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e);
                return;
            }
            patient = documentSnapshot.toObject(Patient.class);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            setAdapters();
            printData();
        });
    }

    private void setAdapters() {
        patientRegularMedicationAdapter = new PatientRegularMedicationAdapter(context, patient, clinicName, patientName, firebaseFirestore);
        recyclerMedication.setAdapter(patientRegularMedicationAdapter);
        patientMedicConditionsAdapter = new PatientMedicConditionsAdapter(context, patient, clinicName, patientName, firebaseFirestore);
        recyclerMedicConditions.setAdapter(patientMedicConditionsAdapter);
        patientRegularExerciseAdapter = new PatientRegularExerciseAdapter(context, patient, clinicName, patientName, firebaseFirestore);
        recyclerRegularExercise.setAdapter(patientRegularExerciseAdapter);
        patientSurgicalOperationsAdapter = new PatientSurgicalOperationsAdapter(context, patient, clinicName, patientName, firebaseFirestore);
        recyclerSurgicalOperations.setAdapter(patientSurgicalOperationsAdapter);
        patientMedicExaminationAdapter = new PatientMedicExaminationAdapter(context, patient, clinicName, patientName, firebaseFirestore);
        recyclerMedicExamination.setAdapter(patientMedicExaminationAdapter);
    }

    private void listeners() {
        imageAddMedication.setOnClickListener(v -> showDialog(0));

        imageAddMedicCondition.setOnClickListener(v -> showDialog(1));

        imageAddExercise.setOnClickListener(v -> showDialog(2));

        imageAddSurgicalOperation.setOnClickListener(v -> showDialog(3));

        imageAddMedicExamination.setOnClickListener(v -> showDialog(4));
    }

    private void setFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(CLINICS);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void printData() {
        String name = patient.getName();
        String surname = patient.getSurname();
        String bornDate = patient.getBornDate();
        String phone = patient.getPhone();
        String email = patient.getEmail();
        String profession = patient.getProfession();

        Glide.with(context)
                .load(patient.getProfileImage())
                .into(imagePatient);

        textPatientName.setText(name);
        textPatientSurname.setText(surname);
        textBornDate.setText(bornDate);
        textPhone.setText(phone);
        textEmail.setText(email);
        textProfession.setText(profession);
    }

    public void showDialog(int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        dialogBuilder.setView(dialogView);

        final EditText editItem = dialogView.findViewById(R.id.edit_item);

        switch (position) {
            case 0:
                dialogBuilder.setTitle("Añadir medicación habitual");
                dialogBuilder.setPositiveButton("Aceptar", (dialog, whichButton) -> {
                    if (editItem.getText().toString().trim().length() != 0) {
                        regularMedication.clear();
                        regularMedication.addAll(patient.getRegularMedication());
                        regularMedication.add(editItem.getText().toString().trim());
                        patient.setRegularMedication(regularMedication);
                        firebaseFirestore.collection(CLINICS).document(clinicName).collection(PATIENTS).document(patientName).set(patient).addOnCompleteListener(task -> patientRegularMedicationAdapter.setData(patient));
                    }
                });
                break;
            case 1:
                dialogBuilder.setTitle("Añadir afección médica");
                dialogBuilder.setPositiveButton("Aceptar", (dialog, whichButton) -> {
                    if (editItem.getText().toString().trim().length() != 0) {
                        medicConditions.clear();
                        medicConditions.addAll(patient.getMedicConditions());
                        medicConditions.add(editItem.getText().toString().trim());
                        patient.setMedicConditions(medicConditions);
                        firebaseFirestore.collection(CLINICS).document(clinicName).collection(PATIENTS).document(patientName).set(patient).addOnCompleteListener(task -> patientMedicConditionsAdapter.setData(patient));
                    }
                });
                break;
            case 2:
                dialogBuilder.setTitle("Añadir ejercicio habitual");
                dialogBuilder.setPositiveButton("Aceptar", (dialog, whichButton) -> {
                    if (editItem.getText().toString().trim().length() != 0) {
                        regularExercise.clear();
                        regularExercise.addAll(patient.getRegularExercise());
                        regularExercise.add(editItem.getText().toString().trim());
                        patient.setRegularExercise(regularExercise);
                        firebaseFirestore.collection(CLINICS).document(clinicName).collection(PATIENTS).document(patientName).set(patient).addOnCompleteListener(task -> patientRegularExerciseAdapter.setData(patient));
                    }
                });
                break;
            case 3:
                dialogBuilder.setTitle("Añadir operación quirúrjica");
                dialogBuilder.setPositiveButton("Aceptar", (dialog, whichButton) -> {
                    if (editItem.getText().toString().trim().length() != 0) {
                        surgicalOperations.clear();
                        surgicalOperations.addAll(patient.getSurgicalOperations());
                        surgicalOperations.add(editItem.getText().toString().trim());
                        patient.setSurgicalOperations(surgicalOperations);
                        firebaseFirestore.collection(CLINICS).document(clinicName).collection(PATIENTS).document(patientName).set(patient).addOnCompleteListener(task -> patientSurgicalOperationsAdapter.setData(patient));
                    }
                });
                break;
            case 4:
                dialogBuilder.setTitle("Añadir reconocimiento médico");
                dialogBuilder.setPositiveButton("Aceptar", (dialog, whichButton) -> {
                    if (editItem.getText().toString().trim().length() != 0) {
                        medicExamination.clear();
                        medicExamination.addAll(patient.getMedicExamination());
                        medicExamination.add(editItem.getText().toString().trim());
                        patient.setMedicExamination(medicExamination);
                        firebaseFirestore.collection(CLINICS).document(clinicName).collection(PATIENTS).document(patientName).set(patient).addOnCompleteListener(task -> patientMedicExaminationAdapter.setData(patient));
                    }
                });
                break;
        }

        dialogBuilder.setNegativeButton("Cancelar", (dialog, whichButton) -> dialog.dismiss());
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
