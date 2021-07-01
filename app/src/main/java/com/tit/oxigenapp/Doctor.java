package com.tit.oxigenapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class Doctor extends AppCompatActivity {
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    private String idUser;
    Spinner sp_Paciente = null;
    Button NuevoPaciente, InformacionPaciente, historico, medicacion, diagrama, cerrar;
    TextView bienvenido_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        fStore = FirebaseFirestore.getInstance();

        fAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = fAuth.getCurrentUser();

        sp_Paciente = findViewById(R.id.spinner_Paciente);
        NuevoPaciente = findViewById(R.id.buttonAgregarPaciente);
        InformacionPaciente = findViewById(R.id.button_informacion);
        historico = findViewById(R.id.button_historico_paciente);
        medicacion = findViewById(R.id.button_medicacion);
        diagrama=findViewById(R.id.button_diagrama_paciente);
        cerrar=findViewById(R.id.logout_doctor_btn);
        bienvenido_txt = findViewById(R.id.bienvenidoDoctor_txt);

        //obtenerDatos();

        bienvenido_txt.setText(user.getEmail());

        //Lamada de la funcion de carga de paciente
        carga_Paciente();

        NuevoPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Ingreso_Paciente.class));
            }
        });
    }

    //Cargar Paciente
    public void carga_Paciente () {
        List<String> usuarios = new ArrayList<>();
        fAuth = FirebaseAuth.getInstance();

        idUser = fAuth.getCurrentUser().getUid();
        DocumentReference nombre = fStore.collection("Usuarios").document(idUser);
        CollectionReference pacienteRef = fStore.collection("Usuarios").document(idUser).collection("Pacientes");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, usuarios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_Paciente.setAdapter(adapter);

        pacienteRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("Nombre Completo");
                        usuarios.add(subject);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        InformacionPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp_Paciente.getSelectedItem() != null) {
                    String datos = sp_Paciente.getSelectedItem().toString();
                    CollectionReference pacienteRef = fStore.collection("Usuarios").document(idUser).collection("Pacientes");
                    pacienteRef.whereEqualTo("Nombre Completo", datos).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d( "Paciente",document.getId() + " => " + document.getData());
                                    String datos = document.getString("Codigo Paciente").toString();

                                    Bundle parmetros = new Bundle();
                                    parmetros.putString("datos", datos);
                                    Intent i = new Intent(getApplicationContext(), Informacion_Paciente_Doctor.class);
                                    i.putExtras(parmetros);
                                    startActivity(i);
                                }
                            } else {
                                Log.d("Paciente", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                } else {
                    Toast.makeText(Doctor.this,"Debe agregar un nuevo paciente para ver la informacion.",Toast.LENGTH_SHORT).show();
                }

            }
        });

        historico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp_Paciente.getSelectedItem() != null) {
                    String datos =sp_Paciente.getSelectedItem().toString();
                    CollectionReference pacienteRef = fStore.collection("Usuarios").document(idUser).collection("Pacientes");
                    String datos2;
                    pacienteRef.whereEqualTo("Nombre Completo", datos).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d( "Paciente",document.getId() + " => " + document.getData());
                                    String datos=document.getString("Codigo Paciente").toString();

                                    Bundle parmetros = new Bundle();
                                    parmetros.putString("datos", datos);
                                    Intent i = new Intent(getApplicationContext(), Historico_Paciente_Doctor.class);
                                    i.putExtras(parmetros);
                                    startActivity(i);
                                }
                            } else {
                                Log.d("Paciente", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                } else {
                    Toast.makeText(Doctor.this,"Debe agregar un nuevo paciente para ver el historico.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        medicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp_Paciente.getSelectedItem() != null) {
                    String datos = sp_Paciente.getSelectedItem().toString();
                    CollectionReference pacienteRef = fStore.collection("Usuarios").document(idUser).collection("Pacientes");
                    pacienteRef.whereEqualTo("Nombre Completo", datos).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d( "Paciente",document.getId() + " => " + document.getData());
                                    String datos = document.getId().toString();

                                    Bundle parmetros = new Bundle();
                                    parmetros.putString("datos3", datos);
                                    Intent i = new Intent(getApplicationContext(), Medicacion_Doctor.class);
                                    i.putExtras(parmetros);
                                    startActivity(i);
                                }
                            } else {
                                Log.d("Paciente", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                } else {
                    Toast.makeText(Doctor.this,"Debe agregar un nuevo paciente para ver la medicacion.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        diagrama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp_Paciente.getSelectedItem() != null) {
                    String datos =sp_Paciente.getSelectedItem().toString();
                    CollectionReference pacienteRef = fStore.collection("Usuarios").document(idUser).collection("Pacientes");
                    String datos2;
                    pacienteRef.whereEqualTo("Nombre Completo", datos).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d( "Paciente",document.getId() + " => " + document.getData());
                                    String datos=document.getString("Codigo Paciente").toString();

                                    Bundle parmetros = new Bundle();
                                    parmetros.putString("datos", datos);
                                    Intent i = new Intent(getApplicationContext(), Diagrama_Paciente_Doctor.class);
                                    i.putExtras(parmetros);
                                    startActivity(i);
                                }
                            } else {
                                Log.d("Paciente", "Error getting documents: ", task.getException());

                            }
                        }
                    });
                } else {
                    Toast.makeText(Doctor.this,"Debe agregar un nuevo paciente para ver el diagrama.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });
    }

    /*private void obtenerDatos() {
        fAuth = FirebaseAuth.getInstance();
        idUser = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("Usuarios").document(idUser);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                bienvenido_txt.setText(documentSnapshot.getString("Nombre Completo"));
            }
        });
    }*/
}