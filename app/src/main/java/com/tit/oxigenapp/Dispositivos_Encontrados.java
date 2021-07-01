package com.tit.oxigenapp;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class Dispositivos_Encontrados extends ListActivity {

    private ArrayAdapter<String> mArrayAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket; //permite el envio de la informacion y viceversa
    private ArrayList<BluetoothDevice> btDeviceArray = new ArrayList<BluetoothDevice>();
    private BluetoothAdapter mBTAdapter;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    Button regresarBtn;
    TextView spo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos_encontrados);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        regresarBtn = findViewById(R.id.Regresar_Dis_Btn);

        //inicia BT
        mArrayAdapter = new ArrayAdapter<String>(this, R.layout.colorlayout);
        setListAdapter(mArrayAdapter);

        //Get Bluettoth Adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check smartphone support Bluetooth
        if(mBluetoothAdapter == null){
            //Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "Not support bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check Bluetooth enabled
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        // Queryng paried devices
        Set<BluetoothDevice> pariedDevices = mBluetoothAdapter.getBondedDevices();
        if(pariedDevices.size() > 0){
            for(BluetoothDevice device : pariedDevices){
                if (device.getName().equals("HC-05")) {
                    mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    btDeviceArray.add(device);
                }
            }
        }

        regresarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Patient.class));
                finish();
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                1000 * 60 * 1,
                1000 * 60 * 1, alarmIntent);
    }
}