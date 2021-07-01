package com.tit.oxigenapp;

import android.app.NotificationChannel;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ObtenerDatos {
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public int conectar(){
        int totalNum = 0;

        FirebaseAuth fAuth;
        FirebaseFirestore fStore;

        Calendar C = Calendar.getInstance();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        System.out.println(btAdapter.getBondedDevices());
        BluetoothDevice hc05 = btAdapter.getRemoteDevice("00:19:09:03:74:52");
        System.out.println(hc05.getName());
        BluetoothSocket btsocket=null;


        try {
            btsocket = hc05.createRfcommSocketToServiceRecord(myUUID);
            System.out.println(btsocket);
            btsocket.connect();
            System.out.println("Esta conectado" +btsocket.isConnected());
        } catch (IOException e) {
            System.out.println("Catch 1");
        }

        try {
            OutputStream mmOutStream = null;
            if(btsocket.isConnected()){
                mmOutStream = btsocket.getOutputStream();
                mmOutStream.write(new String("1").getBytes());
            }
        } catch (IOException e) {
            System.out.println("Catch 2");
        }

        InputStream intputStream = null;
        String CHANNEL_ID = "";

        try {
            int bytesAvailable = 0;
            byte[] packetBytes;
            int count = 0;
            do {
                intputStream = btsocket.getInputStream();
                //intputStream.skip(intputStream.available());
                bytesAvailable = intputStream.available();
                packetBytes = new byte[bytesAvailable];
                count++;
            } while (bytesAvailable == 0 && count < 500);
            String total = null;
            if (bytesAvailable > 0) {
                int i2 = 0;
                for(int i=0; i<bytesAvailable; i++) {
                    byte b = (byte) intputStream.read();
                    i2++;
                    //System.out.println((char) b);
                    if (i2 < 2) {
                        byte c = (byte) intputStream.read();
                        System.out.println("Esto es B: " + (char) b);
                        System.out.println("Esto es c: " + (char) c);
                        total = Character.toString((char) b) + Character.toString((char) c);
                        totalNum = Integer.parseInt(total);
                        System.out.println("Este es el total: " + totalNum);
                        FirebaseUser user = fAuth.getCurrentUser();
                        CollectionReference df = fStore.collection("Usuarios").document(user.getUid()).collection("spo2");
                        Map<String,Object> envioDato = new HashMap<>();
                        if (totalNum <= 85) {
                            envioDato.put("hipoxia" , "Hipoxia Grave");
                        } else {
                            if (totalNum >= 86 && totalNum <= 90) {
                                envioDato.put("hipoxia" , "Hipoxia Moderada");
                            } else {
                                if (totalNum >= 91 && totalNum <= 94) {
                                    envioDato.put("hipoxia" , "Hipoxia Leve");
                                } else {
                                    if (totalNum >= 95 && totalNum < 100) {
                                        envioDato.put("hipoxia" , "Normal");
                                    }
                                }
                            }
                        }
                        envioDato.put("porcentaje" , totalNum);
                        envioDato.put("fecha" , C.getTime());
                        df.add(envioDato);
                        return totalNum;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Catch 3");
        }

        try {
            btsocket.close();
            System.out.println(btsocket.isConnected());
        } catch (IOException e) {
            System.out.println("Catch 4");
        }
        return totalNum;
    }
}