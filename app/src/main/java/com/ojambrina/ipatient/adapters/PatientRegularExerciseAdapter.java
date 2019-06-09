package com.ojambrina.ipatient.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.entities.Patient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ojambrina.ipatient.utils.Constants.CLINICS;
import static com.ojambrina.ipatient.utils.Constants.PATIENTS;

public class PatientRegularExerciseAdapter extends RecyclerView.Adapter<PatientRegularExerciseAdapter.ViewHolder> {

    private Context context;
    private List<String> list = new ArrayList<>();
    private String detail;
    private Patient patient;
    private FirebaseFirestore firebaseFirestore;
    private String clinicName;
    private String patientName;

    public PatientRegularExerciseAdapter(Context context, Patient patient, String clinicName, String patientName, FirebaseFirestore firebaseFirestore) {
        this.context = context;
        this.patient = patient;
        this.clinicName = clinicName;
        this.patientName = patientName;
        this.firebaseFirestore = firebaseFirestore;
        list.clear();
        list.addAll(patient.getRegularExercise());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        detail = list.get(holder.getAdapterPosition());

        holder.textDetail.setText(detail);

        holder.layoutRemove.setOnClickListener(v -> {
            list.remove(holder.getAdapterPosition());
            patient.setRegularExercise(list);
            firebaseFirestore.collection(CLINICS).document(clinicName).collection(PATIENTS).document(patientName).set(patient);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_detail)
        TextView textDetail;
        @BindView(R.id.layout_remove)
        LinearLayout layoutRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setData(Patient patient) {
        list.clear();
        list.addAll(patient.getRegularExercise());
        notifyDataSetChanged();
    }
}
