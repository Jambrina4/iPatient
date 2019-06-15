package com.ojambrina.ipatient.UI.clinics.patients.patientDetail;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.adapters.SessionAdapter;
import com.ojambrina.ipatient.entities.Patient;
import com.ojambrina.ipatient.entities.Session;
import com.ojambrina.ipatient.utils.MyLinearLayoutManager;
import com.ojambrina.ipatient.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.ojambrina.ipatient.utils.Constants.CLINICS;
import static com.ojambrina.ipatient.utils.Constants.CLINIC_NAME;
import static com.ojambrina.ipatient.utils.Constants.FISIO_TUTORIAL;
import static com.ojambrina.ipatient.utils.Constants.PATIENTS;
import static com.ojambrina.ipatient.utils.Constants.PATIENT_NAME;
import static com.ojambrina.ipatient.utils.Constants.PATTERN;
import static com.ojambrina.ipatient.utils.Constants.SESSION_LIST;
import static com.ojambrina.ipatient.utils.Constants.SHARED_PREFERENCES;

public class FisioFragment extends Fragment {

    @BindView(R.id.recycler_session)
    RecyclerView recyclerSession;
    @BindView(R.id.add_session)
    FloatingActionButton fab;
    @BindView(R.id.text_session_date_tutorial)
    TextView textSessionDateTutorial;
    @BindView(R.id.layout_start)
    LinearLayout layoutStart;
    @BindView(R.id.fab_tutorial)
    FloatingActionButton fabTutorial;
    @BindView(R.id.button_understand)
    Button buttonUnderstand;
    @BindView(R.id.layout_background_tutorial)
    RelativeLayout layoutBackgroundTutorial;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private Context context;
    private Patient patient;
    private String clinic_name;
    private String patientName;
    private List<Session> sessionList = new ArrayList<>();
    private List<String> highlightList = new ArrayList<>();
    private List<String> reasonList = new ArrayList<>();
    private List<String> explorationList = new ArrayList<>();
    private List<String> treatmentList = new ArrayList<>();
    private SessionAdapter sessionAdapter;
    private SharedPreferences sharedPreferences;
    private Session session;
    private MyLinearLayoutManager myLinearLayoutManager;

    Unbinder unbinder;

    public FisioFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fisio, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            clinic_name = (String) getArguments().get(CLINIC_NAME);
            patientName = (String) getArguments().get(PATIENT_NAME);
        }

        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        context = getContext();
        myLinearLayoutManager = new MyLinearLayoutManager(context);

        loadTutorial();
        setFirebase();
        getSessionList();
        setAdapter();
        listeners();

        return view;
    }

    private void loadTutorial() {
        if (!sharedPreferences.getBoolean(FISIO_TUTORIAL, false)) {
            layoutBackgroundTutorial.setVisibility(View.VISIBLE);
            myLinearLayoutManager.setScrollEnabled(false);
            recyclerSession.setVisibility(View.GONE);
        } else {
            myLinearLayoutManager.setScrollEnabled(true);
            recyclerSession.setVisibility(View.VISIBLE);
        }
    }

    private void setAdapter() {
        sessionAdapter = new SessionAdapter(context, session, sessionList, clinic_name, patientName, firebaseFirestore);
        recyclerSession.setLayoutManager(myLinearLayoutManager);
        recyclerSession.setAdapter(sessionAdapter);
    }

    private void listeners() {
        fab.setOnClickListener(v -> {
            firebaseFirestore.collection(CLINICS).document(clinic_name).collection(PATIENTS).document(patientName).collection(SESSION_LIST).document(Utils.getCurrentDay()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (!task.getResult().exists()) {
                        addSession();
                        firebaseFirestore.collection(CLINICS).document(clinic_name).collection(PATIENTS).document(patientName).collection(SESSION_LIST).document(Utils.getCurrentDay()).set(session);
                    }
                }
            });
            sessionAdapter.notifyDataSetChanged();
        });

        fab.setOnLongClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year1, month1, dayOfMonth) -> {
                month1 = month1 + 1;
                String dayCorrected;
                if (dayOfMonth < 10) {
                    dayCorrected = "0" + dayOfMonth;
                } else {
                    dayCorrected = String.valueOf(dayOfMonth);
                }
                String monthCorrected;
                if (month1 < 10) {
                    monthCorrected = "0" + month1;
                } else {
                    monthCorrected = String.valueOf(month1);
                }
                String sessionDate = dayCorrected + "-" + monthCorrected + "-" + year1;
                addFutureSession(sessionDate);
                firebaseFirestore.collection(CLINICS).document(clinic_name).collection(PATIENTS).document(patientName).collection(SESSION_LIST).document(sessionDate).set(session);
            }, year, month, day);
            datePickerDialog.show();

            return false;
        });

        buttonUnderstand.setOnClickListener(v -> {
            layoutBackgroundTutorial.setVisibility(View.GONE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(FISIO_TUTORIAL, true);
            editor.apply();
            myLinearLayoutManager.setScrollEnabled(true);
            recyclerSession.setVisibility(View.VISIBLE);
        });
    }

    private void getSessionList() {
        firebaseFirestore.collection(CLINICS).document(clinic_name).collection(PATIENTS).document(patientName).collection(SESSION_LIST).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("ERROR", "Listen failed.", e);
                    return;
                }

                List<Session> list = queryDocumentSnapshots.toObjects(Session.class);

                sessionList.clear();
                sessionList.addAll(list);
                sortSessionsByDate(sessionList);
                sessionAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addSession() {
        session = new Session();
        String sessionDate = Utils.getCurrentDay();
        session.setDate(sessionDate);
        session.setHighlightList(highlightList);
        session.setReasonList(reasonList);
        session.setExplorationList(explorationList);
        session.setTreatmentList(treatmentList);
        try {
            session.setDateMillis(Utils.formatMillis(sessionDate, PATTERN));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sessionList.add(session);
    }

    private void addFutureSession(String sessionDate) {
        session = new Session();
        session.setDate(sessionDate);
        session.setHighlightList(highlightList);
        session.setReasonList(reasonList);
        session.setExplorationList(explorationList);
        session.setTreatmentList(treatmentList);
        try {
            session.setDateMillis(Utils.formatMillis(sessionDate, PATTERN));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sessionList.add(session);
    }

    private void setFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(CLINICS);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private List<Session> sortSessionsByDate(List<Session> sessionList) {
        Collections.sort(sessionList, (o1, o2) -> {
            long timeMillis1 = o1.getDateMillis();
            long timeMillis2 = o2.getDateMillis();

            if (timeMillis1 > timeMillis2) {
                return 1;
            } else if (timeMillis1 < timeMillis2) {
                return -1;
            } else return 0;
        });

        Collections.reverse(sessionList);

        return sessionList;
    }
}
