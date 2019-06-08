package com.ojambrina.ipatient.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.UI.clinics.patients.patientDetail.PatientDetailActivity;
import com.ojambrina.ipatient.entities.Patient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.ojambrina.ipatient.utils.Constants.CLINIC_NAME;
import static com.ojambrina.ipatient.utils.Constants.PATIENT_NAME;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {

    private Patient patient;
    private Context context;
    private List<Patient> patientList;
    private String clinic_name;

    public PatientAdapter(Context context, List<Patient> patientList, String clinic_name) {
        this.context = context;
        this.patientList = patientList;
        this.clinic_name = clinic_name;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        patient = patientList.get(holder.getAdapterPosition());

        holder.textPatient.setText(patient.getName());

        if (patientList.get(holder.getAdapterPosition()).getProfileImage() == null) {
            holder.imagePatient.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_person_black_24dp));
        } else {
            Glide.with(context)
                    .load(patient.getProfileImage())
                    .into(holder.imagePatient);
        }

        holder.layoutPatient.setOnClickListener(v -> {
            Intent intent = new Intent(context, PatientDetailActivity.class);
            intent.putExtra(PATIENT_NAME, patientList.get(holder.getAdapterPosition()).getName());
            intent.putExtra(CLINIC_NAME, clinic_name);
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
