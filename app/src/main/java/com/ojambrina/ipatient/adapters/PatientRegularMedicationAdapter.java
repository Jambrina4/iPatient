package com.ojambrina.ipatient.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.entities.Patient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PatientRegularMedicationAdapter extends RecyclerView.Adapter<PatientRegularMedicationAdapter.ViewHolder> {

    private Context context;
    private List<String> list = new ArrayList<>();
    private String detail;
    private Patient patient;

    public PatientRegularMedicationAdapter(Context context, Patient patient) {
        this.context = context;
        this.patient = patient;
        list.clear();
        list.addAll(patient.getRegularMedication());
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_detail)
        TextView textDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setData(Patient patient) {
        list.clear();
        list.addAll(patient.getRegularMedication());
        notifyDataSetChanged();
    }
}
