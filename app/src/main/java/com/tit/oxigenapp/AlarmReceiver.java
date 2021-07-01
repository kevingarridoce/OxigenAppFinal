package com.tit.oxigenapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int spo2 = 0;

        ObtenerDatos paciente = new ObtenerDatos();
        paciente.conectar();

        if (paciente.conectar() > 0) {
            spo2 = paciente.conectar();
            System.out.println(spo2);
        }
    }
}