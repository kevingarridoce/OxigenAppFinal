package com.tit.oxigenapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class Patient extends AppCompatActivity {
    Button historicoBtn, infoBtn, diagramaBtn, medicacionBtn, conectarBtn;
    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    TextView bienvenido_txt;
    private String idUser;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        historicoBtn = findViewById(R.id.historico_btn);
        infoBtn = findViewById(R.id.info_btn);
        diagramaBtn = findViewById(R.id.diagrama_btn);
        medicacionBtn = findViewById(R.id.medicacion_Pac_btn);
        bienvenido_txt = findViewById(R.id.bienvenido_txt);
        conectarBtn = findViewById(R.id.Conectar_Btn);

        fAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = fAuth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();
        idUser = fAuth.getCurrentUser().getUid();

        Button logout = findViewById(R.id.logout_paciente_btn);

        bienvenido_txt.setText(user.getEmail());

        //Para llamar a otras clases y interfaz
        /*logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutAdmin();
            }
        });*/

        historicoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Historico_Paciente.class));
                finish();
            }
        });

        conectarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Dispositivos_Encontrados.class));
                finish();
            }
        });

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Informacion_Paciente.class));
                finish();
            }
        });

        diagramaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Diagrama_Paciente.class));
                finish();
            }
        });

        medicacionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Medicacion_Paciente.class));
                finish();
            }
        });
    }

     public void logoutAdmin(View view) {
        FirebaseAuth.getInstance().signOut();
         Intent intent = new Intent(this, AlarmReceiver.class);
         PendingIntent sender = PendingIntent.getBroadcast(this, 1, intent, 0);
         AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
         alarmManager.cancel(sender);
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}