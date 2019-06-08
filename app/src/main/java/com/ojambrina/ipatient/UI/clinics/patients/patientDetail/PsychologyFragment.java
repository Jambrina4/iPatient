package com.ojambrina.ipatient.UI.clinics.patients.patientDetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.entities.Patient;

import static com.ojambrina.ipatient.utils.Constants.PATIENT;

public class PsychologyFragment extends Fragment {

    private Patient patient;

    public PsychologyFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            patient = (Patient) getArguments().get(PATIENT);
        }
        return inflater.inflate(R.layout.fragment_psychology, container, false);
    }

}
