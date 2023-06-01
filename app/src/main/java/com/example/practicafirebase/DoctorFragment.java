package com.example.practicafirebase;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practicafirebase.model.Cita;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoctorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoctorFragment extends Fragment {
    Button openDialog;
    TextView selectedDate;
    TextView doctorName;
    TextView speciality;
    Button confirmDate;
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DoctorFragment(FirebaseUser user) {
        // Required empty public constructor
        this.user = user;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DoctorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DoctorFragment newInstance(String param1, String param2, FirebaseUser user) {
        DoctorFragment fragment = new DoctorFragment(user);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor, container, false);
        openDialog = view.findViewById(R.id.openDialog);
        selectedDate = view.findViewById(R.id.selectedDate);
        confirmDate = view.findViewById(R.id.confirmDate);
        doctorName = view.findViewById(R.id.doctorName);
        speciality = view.findViewById(R.id.doctorSpecialty);
        confirmDate.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Cita confirmada", Toast.LENGTH_SHORT).show();
            Cita cita = new Cita(UUID.randomUUID().toString(), doctorName.getText().toString(), speciality.getText().toString(), selectedDate.getText().toString().substring(19), user.getUid());
            reference.child("Citas").child(user.getUid()).child(cita.getId()).setValue(cita);
            confirmDate.setVisibility(View.GONE);
            selectedDate.setText("");
        });
        openDialog.setOnClickListener(v -> openDialog());
        return view;
    }

    private void openDialog() {
        CalendarConstraints calendarConstraints = new CalendarConstraints.Builder()
                .setStart(MaterialDatePicker.todayInUtcMilliseconds())
                .setEnd(MaterialDatePicker.thisMonthInUtcMilliseconds())
                .build();
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona una fecha")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(calendarConstraints)
                .build();
        //get selected date in DD/MM/YYYY format
        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate.setText("Fecha seleccionada: " + datePicker.getHeaderText());
            confirmDate.setVisibility(View.VISIBLE);
        });
        datePicker.show(getFragmentManager(), "DATE_PICKER");
    }
}