package com.tit.oxigenapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Medicacion_Paciente extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView medicacionPac, nomDoctor;
    Button regresarBtn;
    private String idUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicacion_paciente);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        idUser = fAuth.getCurrentUser().getUid();

        medicacionPac = findViewById(R.id.medicacion_txt);
        nomDoctor = findViewById(R.id.nom_doctor_txt);
        regresarBtn = findViewById(R.id.regresar_doc_btn);
        obtenerDoc();
        obtenerDatos();

        regresarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Patient.class));

            }
        });
    }

    private void obtenerDatos() {
        DocumentReference pacienteStore = fStore.collection("Usuarios").document(idUser);
        pacienteStore.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String codigoDoc = value.getString("Codigo Doctor");
                String codigoPac = value.getString("Codigo");
                if (codigoDoc != null) {


                    CollectionReference comparacion = fStore.collection("Usuarios").document(codigoDoc).collection("Pacientes");

                    Log.d( "Doctor",codigoDoc);


                    comparacion.whereEqualTo("Codigo Paciente",codigoPac).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {


                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    medicacionPac.setText(document.getString("Medicacion"));
                                }
                            } else {
                                Log.d("Paciente", "Error getting documents: ", task.getException());

                            }
                        }
                    });


                }
            }
        });
    }


    private void obtenerDoc() {
        DocumentReference pacienteStore = fStore.collection("Usuarios").document(idUser);
        pacienteStore.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String codigoDoc = value.getString("Codigo Doctor");
                obtenerDoc2( codigoDoc);

            }


        });
    }
    private void obtenerDoc2(String codigoDoc) {
        if (codigoDoc != null) {

            DocumentReference documentReference = fStore.collection("Usuarios").document(codigoDoc);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    nomDoctor.setText(documentSnapshot.getString("Nombre Completo"));

                }
            });




        } else {
            nomDoctor.setText("Sin asignacion de Doctor.");
        }


    }

}
