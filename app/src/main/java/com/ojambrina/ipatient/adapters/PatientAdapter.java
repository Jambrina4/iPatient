package com.ojambrina.ipatient.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.UI.clinics.patients.patientDetail.PatientDetailActivity;
import com.ojambrina.ipatient.entities.Patient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.ojambrina.ipatient.utils.Constants.CLINICS;
import static com.ojambrina.ipatient.utils.Constants.CLINIC_NAME;
import static com.ojambrina.ipatient.utils.Constants.PATIENT;
import static com.ojambrina.ipatient.utils.Constants.PATIENTS;
import static com.ojambrina.ipatient.utils.Constants.PATIENT_NAME;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {

    private Context context;
    private List<Patient> patientList;
    private String clinic_name;
    private FirebaseFirestore firebaseFirestore;

    public PatientAdapter(Context context, List<Patient> patientList, String clinic_name, FirebaseFirestore firebaseFirestore) {
        this.context = context;
        this.patientList = patientList;
        this.clinic_name = clinic_name;
        this.firebaseFirestore = firebaseFirestore;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Patient patient = patientList.get(holder.getAdapterPosition());

        holder.textPatient.setText(patientList.get(holder.getAdapterPosition()).getName());

        if (patientList.get(holder.getAdapterPosition()).getProfileImage() == null) {
            holder.imagePatient.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_person_black_24dp));
        } else {
            Glide.with(context)
                    .load(patientList.get(holder.getAdapterPosition()).getProfileImage())
                    .into(holder.imagePatient);
        }

        holder.layoutPatient.setOnClickListener(v -> {
            Patient getPatient = patientList.get(holder.getAdapterPosition());
            Intent intent = new Intent(context, PatientDetailActivity.class);
            intent.putExtra(PATIENT_NAME, patientList.get(holder.getAdapterPosition()).getName());
            intent.putExtra(CLINIC_NAME, clinic_name);
            intent.putExtra(PATIENT, getPatient);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_patient)
        CircleImageView imagePatient;
        @BindView(R.id.text_patient)
        TextView textPatient;
        @BindView(R.id.layout_patient)
        LinearLayout layoutPatient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
