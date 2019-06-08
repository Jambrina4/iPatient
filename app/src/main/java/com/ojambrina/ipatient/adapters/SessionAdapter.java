package com.ojambrina.ipatient.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private Context context;
    private Session session;
    private List<Session> sessionList;
    private FirebaseFirestore firebaseFirestore;
    private List<String> reasonList = new ArrayList<>();
    private List<String> explorationList = new ArrayList<>();
    private List<String> treatmentList = new ArrayList<>();
    private String clinic_name;
    private String patientName;

    private SessionHighlightAdapter sessionHighlightAdapter;
    private SessionReasonAdapter sessionReasonAdapter;
    private SessionExplorationAdapter sessionExplorationAdapter;
    private SessionTreatmentAdapter sessionTreatmentAdapter;

    public SessionAdapter(Context context, Session session, List<Session> sessionList, String clinic_name, String patientName, FirebaseFirestore firebaseFirestore) {
        this.context = context;
        this.session = session;
        this.sessionList = sessionList;
        this.clinic_name = clinic_name;
        this.patientName = patientName;
        this.firebaseFirestore = firebaseFirestore;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        session = sessionList.get(holder.getAdapterPosition());

        setSession(holder);

        holder.textSessionDate.setText(session.getDate());
        holder.layoutSession.setOnClickListener(v -> {
            if (holder.layoutSessionDetail.getVisibility() == View.VISIBLE) {
                holder.layoutSessionDetail.setVisibility(View.GONE);
                holder.recyclerSessionHighlight.setVisibility(View.VISIBLE);
                holder.viewSeparator.setVisibility(View.VISIBLE);
            } else {
                holder.layoutSessionDetail.setVisibility(View.VISIBLE);
                holder.recyclerSessionHighlight.setVisibility(View.GONE);
                holder.viewSeparator.setVisibility(View.GONE);
            }
        });

        holder.imageAddReason.setOnClickListener(v -> showDialog(holder, 0));

        holder.imageAddExploration.setOnClickListener(v -> showDialog(holder, 1));

        holder.imageAddTreatment.setOnClickListener(v -> showDialog(holder, 2));
    }

    private void setSession(ViewHolder holder) {
        sessionHighlightAdapter = new SessionHighlightAdapter(context, session);
        sessionReasonAdapter = new SessionReasonAdapter(context, session, clinic_name, patientName, session.getDate(), firebaseFirestore);
        sessionExplorationAdapter = new SessionExplorationAdapter(context, session, clinic_name, patientName, session.getDate(), firebaseFirestore);
        sessionTreatmentAdapter = new SessionTreatmentAdapter(context, session, clinic_name, patientName, session.getDate(), firebaseFirestore);

        holder.recyclerSessionHighlight.setAdapter(sessionHighlightAdapter);
        holder.recyclerAddVisitReason.setAdapter(sessionReasonAdapter);
        holder.recyclerAddExploration.setAdapter(sessionExplorationAdapter);
        holder.recyclerAddTreatment.setAdapter(sessionTreatmentAdapter);

        sessionHighlightAdapter.notifyDataSetChanged();
        sessionReasonAdapter.notifyDataSetChanged();
        sessionExplorationAdapter.notifyDataSetChanged();
        sessionTreatmentAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_session_date)
        TextView textSessionDate;
        @BindView(R.id.view_separator)
        View viewSeparator;
        @BindView(R.id.recycler_session_highlight)
        RecyclerView recyclerSessionHighlight;
        @BindView(R.id.recycler_add_visit_reason)
        RecyclerView recyclerAddVisitReason;
        @BindView(R.id.image_add_reason)
        ImageView imageAddReason;
        @BindView(R.id.recycler_add_exploration)
        RecyclerView recyclerAddExploration;
        @BindView(R.id.image_add_exploration)
        ImageView imageAddExploration;
        @BindView(R.id.recycler_add_treatment)
        RecyclerView recyclerAddTreatment;
        @BindView(R.id.image_add_treatment)
        ImageView imageAddTreatment;
        @BindView(R.id.layout_session_detail)
        LinearLayout layoutSessionDetail;
        @BindView(R.id.layout_session)
        LinearLayout layoutSession;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void showDialog(ViewHolder holder, int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        dialogBuilder.setView(dialogView);

        final EditText editItem = dialogView.findViewById(R.id.edit_item);

        switch (position) {
            case 0:
                dialogBuilder.setTitle("Añadir motivo de visita");
                dialogBuilder.setPositiveButton("Aceptar", (dialog, whichButton) -> {
                    if (editItem.getText().toString().trim().length() != 0) {
                        session = sessionList.get(holder.getAdapterPosition());
                        reasonList.clear();
                        reasonList.addAll(session.getReasonList());
                        reasonList.add(editItem.getText().toString().trim());
                        session.setReasonList(reasonList);
                        firebaseFirestore.collection(CLINICS).document(clinic_name).collection(PATIENTS).document(patientName).collection(SESSION_LIST).document(session.getDate()).set(session).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sessionReasonAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                break;
            case 1:
                dialogBuilder.setTitle("Añadir exploración");
                dialogBuilder.setPositiveButton("Aceptar", (dialog, whichButton) -> {
                    if (editItem.getText().toString().trim().length() != 0) {
                        session = sessionList.get(holder.getAdapterPosition());
                        explorationList.clear();
                        explorationList.addAll(session.getExplorationList());
                        explorationList.add(editItem.getText().toString().trim());
                        session.setExplorationList(explorationList);
                        firebaseFirestore.collection(CLINICS).document(clinic_name).collection(PATIENTS).document(patientName).collection(SESSION_LIST).document(session.getDate()).set(session).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sessionExplorationAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                break;
            case 2:
                dialogBuilder.setTitle("Añadir tratamiento");
                dialogBuilder.setPositiveButton("Aceptar", (dialog, whichButton) -> {
                    if (editItem.getText().toString().trim().length() != 0) {
                        session = sessionList.get(holder.getAdapterPosition());
                        treatmentList.clear();
                        treatmentList.addAll(session.getTreatmentList());
                        treatmentList.add(editItem.getText().toString().trim());
                        session.setTreatmentList(treatmentList);
                        firebaseFirestore.collection(CLINICS).document(clinic_name).collection(PATIENTS).document(patientName).collection(SESSION_LIST).document(session.getDate()).set(session).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sessionTreatmentAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                break;
        }

        dialogBuilder.setNegativeButton("Cancelar", (dialog, whichButton) -> dialog.dismiss());
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}