package com.example.practicafirebase;


import static com.example.practicafirebase.Constants.PREF_ACCOUNT_NAME;
import static com.example.practicafirebase.Constants.REQUEST_ACCOUNT_PICKER;
import static com.example.practicafirebase.Constants.REQUEST_PERMISSION_GET_ACCOUNTS;
import static com.google.android.gms.common.GooglePlayServicesUtilLight.isGooglePlayServicesAvailable;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
//import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import java.util.Collections;
import java.util.Date;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practicafirebase.model.Cita;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.store.FileDataStoreFactory;
//import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import java.util.UUID;
import java.util.Calendar;

import pub.devrel.easypermissions.EasyPermissions;

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
    public static final String CHANNEL_ID = "1";
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "../../../credentials.json";
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
//            Toast.makeText(getContext(), "Cita confirmada", Toast.LENGTH_SHORT).show();
            Cita cita = new Cita(UUID.randomUUID().toString(), doctorName.getText().toString(), speciality.getText().toString(), selectedDate.getText().toString().substring(19), user.getUid());
            reference.child("Citas").child(user.getUid()).child(cita.getId()).setValue(cita);
            confirmDate.setVisibility(View.GONE);
            selectedDate.setText("");
            // Cita get fecha splitted by year month and date
            String[] fecha = cita.getFecha().split("/");
            // trim every part of the date
            for (int i = 0; i < fecha.length; i++) {
                fecha[i] = fecha[i].trim();
            }
            Calendar beginTime = Calendar.getInstance();
            beginTime.set(Integer.parseInt(fecha[2]), Integer.parseInt(fecha[1]) - 1, Integer.parseInt(fecha[0]));
            Calendar endTime = Calendar.getInstance();
            endTime.set(Integer.parseInt(fecha[2]), Integer.parseInt(fecha[1]) - 1, Integer.parseInt(fecha[0]));
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, "Cita Medica")
                    .putExtra(CalendarContract.Events.DESCRIPTION, "Cita con el doctor")
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, "Consultorio")
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            startActivity(intent);
            createNotificationChannel(this.getContext());
            createNotification();

//            final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
//            Calendar service = null;
//            try {
//                service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                        .setApplicationName(APPLICATION_NAME)
//                        .build();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            Event event = new Event()
//                    .setSummary("My Event")
//                    .setLocation("Event Location")
//                    .setDescription("Event Description");
//
//            DateTime startDateTime = new DateTime(new Date());
//            DateTime endDateTime = new DateTime(new Date());
//            EventDateTime start = new EventDateTime().setDateTime(startDateTime);
//            EventDateTime end = new EventDateTime().setDateTime(endDateTime);
//            event.setStart(start);
//            event.setEnd(end);
//
//            String calendarId = "primary";
//            try {
//                event = service.events().insert(calendarId, event).execute();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            System.out.printf("Event created: %s\n", event.getHtmlLink());


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
        //get selected date in DD/MM/YYYY format in utc time zone
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // create a instance of date, add it to calendar plus 1 day and convert it to string
            Calendar calendar = Calendar.getInstance();
            //format date
            calendar.setTimeInMillis((Long) selection);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date date = calendar.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
            // format date
            String dateString = simpleDateFormat.format(date);
            selectedDate.setText("Fecha seleccionada: " + dateString);
            confirmDate.setVisibility(View.VISIBLE);
        });
        datePicker.show(getFragmentManager(), "DATE_PICKER");
    }
//        private static Credential getCredentials ( final NetHttpTransport HTTP_TRANSPORT)
//            throws IOException {
//            // Load client secrets.
//            InputStream in = new FileInputStream("app/credentials.json");
//            if (in == null) {
//                throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
//            }
//            GoogleClientSecrets clientSecrets =
//                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//            // Build flow and trigger user authorization request.
//            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
//                    .setAccessType("offline")
//                    .build();
//            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//            //returns an authorized Credential object.
//            return credential;
//        }

    //
    private void createNotification() {
        Intent intent = new Intent(getContext(), SearchFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setContentTitle("Cita registrada")
                .setSmallIcon(R.drawable.cardiologia)
                .setContentText("Tu cita ha sido registrada con exito, por favor agendala en tu calendario")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermission4sResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());

    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}