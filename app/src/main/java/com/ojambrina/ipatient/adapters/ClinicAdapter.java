package com.ojambrina.ipatient.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.entities.Clinic;
import com.ojambrina.ipatient.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.ojambrina.ipatient.utils.Constants.LATEST_CLINIC;

public class ClinicAdapter extends RecyclerView.Adapter<ClinicAdapter.ViewHolder> {

    private Context context;
    private List<Clinic> clinicList = new ArrayList<>();
    private Clinic clinic;
    private String clinicName;
    private OnClickListener listener;
    private SharedPreferences sharedPreferences;

    public ClinicAdapter(Context context, List<Clinic> clinicList, SharedPreferences sharedPreferences, OnClickListener listener) {
        this.context = context;
        this.clinicList.addAll(clinicList);
        this.sharedPreferences = sharedPreferences;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clinic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        clinic = clinicList.get(holder.getAdapterPosition());
        clinicName = clinic.getName();

        if (clinic.getImage() != null) {
            Utils.loadGlide(context, clinicList.get(holder.getAdapterPosition()).getImage(), holder.imageClinic);
        } else {
            holder.imageClinic.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_building_24dp));
        }

        holder.textClinic.setText(clinicName);
        holder.layoutClinic.setBackgroundResource(0);
        if (sharedPreferences.getString(LATEST_CLINIC, "").equals(clinicName)) {
            holder.layoutClinic.setBackground(ContextCompat.getDrawable(context, R.drawable.grey_background));
        } else {
            holder.layoutClinic.setBackgroundResource(0);
        }
        holder.layoutClinic.setOnClickListener(v -> {
            holder.layoutClinic.setBackground(ContextCompat.getDrawable(context, R.drawable.grey_background));

            listener.onClick(holder.getAdapterPosition(), clinic);
        });
    }

    @Override
    public int getItemCount() {
        return clinicList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_clinic)
        TextView textClinic;
        @BindView(R.id.layout_clinic)
        LinearLayout layoutClinic;
        @BindView(R.id.image_clinic)
        CircleImageView imageClinic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickListener {
        void onClick(int position, Clinic clinic);
    }

    public void setData(List<Clinic> list) {
        clinicList.clear();
        clinicList.addAll(list);
        notifyDataSetChanged();
    }
}
