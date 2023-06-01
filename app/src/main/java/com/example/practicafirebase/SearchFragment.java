package com.example.practicafirebase;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.practicafirebase.model.Cita;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    FirebaseUser user;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment(FirebaseUser user) {
        // Required empty public constructor
        this.user = user;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2, FirebaseUser user) {
        SearchFragment fragment = new SearchFragment(user);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        TableLayout tableLayout = view.findViewById(R.id.tableLayout);

        DatabaseReference citasRef = FirebaseDatabase.getInstance().getReference().child("Citas").child(user.getUid().toString());

        citasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() != null){
                    tableLayout.removeAllViews();
                    //agrega los encabezados
                    TableRow tableRowHeader = new TableRow(getContext());
                    TextView textViewNombreMedicoHeader = new TextView(getContext());
                    textViewNombreMedicoHeader.setText("Nombre del médico");
                    TextView textViewEspecialidadHeader = new TextView(getContext());
                    textViewEspecialidadHeader.setText("Especialidad");
                    TextView textViewFechaHeader = new TextView(getContext());
                    textViewFechaHeader.setText("Fecha");
                    tableRowHeader.addView(textViewNombreMedicoHeader);
                    tableRowHeader.addView(textViewEspecialidadHeader);
                    tableRowHeader.addView(textViewFechaHeader);
                    tableLayout.addView(tableRowHeader);

                    for (DataSnapshot citaSnapshot : dataSnapshot.getChildren()) {
                        // Obtener los datos de la cita
                        String nombreMedico = citaSnapshot.child("nombreMedico").getValue(String.class);
                        String especialidad = citaSnapshot.child("especialidad").getValue(String.class);
                        String fecha = citaSnapshot.child("fecha").getValue(String.class);

                        // Crear una nueva fila
                        TableRow tableRow = new TableRow(getContext());

                        // Crear y configurar los TextView para cada dato de la cita
                        TextView textViewNombreMedico = new TextView(getContext());
                        textViewNombreMedico.setText(nombreMedico);
                        TextView textViewEspecialidad = new TextView(getContext());
                        textViewEspecialidad.setText(especialidad);
                        TextView textViewFecha = new TextView(getContext());
                        textViewFecha.setText(fecha);

                        // Agregar los TextView a la fila
                        tableRow.addView(textViewNombreMedico);
                        tableRow.addView(textViewEspecialidad);
                        tableRow.addView(textViewFecha);

                        // Agregar la fila al TableLayout
                        tableLayout.addView(tableRow);
                    }
                }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error si la lectura de datos falla
            }
        });

        return view;
    }


}