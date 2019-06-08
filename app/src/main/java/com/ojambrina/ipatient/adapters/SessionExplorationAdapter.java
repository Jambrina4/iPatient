package com.ojambrina.ipatient.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.entities.Session;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ojambrina.ipatient.utils.Constants.CLINICS;
import static com.ojambrina.ipatient.utils.Constants.PATIENTS;
import static com.ojambrina.ipatient.utils.Constants.SESSION_LIST;

public class SessionExplorationAdapter extends RecyclerView.Adapter<SessionExplorationAdapter.ViewHolder> {

    private Context context;
    private String exploration;
    private List<String> explorationList = new ArrayList<>();
    private List<String> highlightList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private String clinic_name;
    private String patientName;
    private String date;
    private Session session;

    public SessionExplorationAdapter(Context context, Session session, String clinic_name, String patientName, String date, FirebaseFirestore firebaseFirestore) {
        this.context = context;
        this.session = session;
        this.clinic_name = clinic_name;
        this.patientName = patientName;
        this.date = date;
        this.firebaseFirestore = firebaseFirestore;
        highlightList.clear();
        highlightList.addAll(session.getHighlightList());
        explorationList.clear();
        explorationList.addAll(session.getExplorationList());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        exploration = explorationList.get(holder.getAdapterPosition());

        holder.textDetail.setText(exploration);

        if (highlightList.contains(explorationList.get(holder.getAdapterPosition()))) {
            holder.imageHighlight.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_stars_black_24dp));
        } else {
            holder.imageHighlight.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_black_24dp));
        }

        holder.layoutHighlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (highlightList.contains(explorationList.get(holder.getAdapterPosition()))) {
                    highlightList.remove(explorationList.get(holder.getAdapterPosition()));
                } else {
                    highlightList.add(explorationList.get(holder.getAdapterPosition()));
                }
                session.setHighlightList(highlightList);
                firebaseFirestore.collection(CLINICS).document(clinic_name).collection(PATIENTS).document(patientName).collection(SESSION_LIST).document(date).set(session);
            }
        });

        holder.layoutRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (highlightList.contains(explorationList.get(holder.getAdapterPosition()))) {
                    highlightList.remove(explorationList.get(holder.getAdapterPosition()));
                }
                explorationList.remove(explorationList.get(holder.getAdapterPosition()));
                session.setHighlightList(highlightList);
                session.setExplorationList(explorationList);
                firebaseFirestore.collection(CLINICS).document(clinic_name).collection(PATIENTS).document(patientName).collection(SESSION_LIST).document(date).set(session);
            }
        });
    }

    @Override
    public int getItemCount() {
        return explorationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_detail)
        ImageView imageDetail;
        @BindView(R.id.text_detail)
        TextView textDetail;
        @BindView(R.id.image_highlight)
        ImageView imageHighlight;
        @BindView(R.id.layout_highlight)
        LinearLayout layoutHighlight;
        @BindView(R.id.image_remove)
        ImageView imageRemove;
        @BindView(R.id.layout_remove)
        LinearLayout layoutRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}