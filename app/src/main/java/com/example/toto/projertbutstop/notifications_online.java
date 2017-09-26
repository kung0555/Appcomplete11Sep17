package com.example.toto.projertbutstop;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.os.Vibrator;

public class notifications_online extends AppCompatActivity {
    String Busnum;
    String Sum;
    String StartEnd;
    String KM;
    String StopFirst;
    String StopEnd;
    double LatBusStopEnd;
    double LngBusStopEnd;
    double latChanged;
    double lngChanged;
    private LocationManager locationManager;
    double latStartADouble;
    double lngStartADouble;
    double dis;
    int x = 0;
    int a = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_online);

        //รับค่าที่ส่งมา
        intentlistonline();

        //setupLocation
        setupLocation();
    }//Main Method

    private void setupLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
    }

    public Location myFindLocation(String strProvider) {

        Location location = null;
        if (locationManager.isProviderEnabled(strProvider)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);
            location = locationManager.getLastKnownLocation(strProvider);
        }
        return location;
    }

    public LocationListener locationListener = new LocationListener() {
        //ทำงานฟังชันนี้เมื่อ Location มีการเปลี่ยนแปลง
        @Override
        public void onLocationChanged(Location location) {
            latChanged = location.getLatitude();
            lngChanged = location.getLongitude();
            dis = distance(LatBusStopEnd, LngBusStopEnd, latChanged, lngChanged);
            Log.d("Test19", "ระยะทางTest" + dis + "กม.");
            int pp = (int) dis;
            Log.d("Test19", "Testpp" + pp + "กม.");
            //แจ้งเตือนเมื่อใกล้ถึงป้ายจุดหมาย
            if (dis < 0.1 && dis > 0.05) {
                if (x == 0) {
                    Toast.makeText(getApplicationContext(), "ใกล้ถึงแล้ว  "+StopEnd, Toast.LENGTH_SHORT).show();
                    Log.d("Test19", "ใกล้เข้าป้าย");
                    x = 1;
                    Log.d("Test19", "x ==>" + x);
                }
                //แจ้งเตือนเมื่อเลยป้ายจุดหมาย
                if (x == 2) {
                    Toast.makeText(getApplicationContext(), "เลยป้าย  "+StopEnd, Toast.LENGTH_SHORT).show();
                    Vibrator v3 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v3.vibrate(3000);
                    x=3;
                    Log.d("Test19", "เลยป้าย");
                    Log.d("Test19", "x ==>" + x);

                }
            }
            //แจ้งเตือนเมื่อถึงป้ายจุดหมาย
            if (dis < 0.002) {
                if (x == 1) {
                    Toast.makeText(getApplicationContext(), "ถึง "+StopEnd+"แล้ว  ", Toast.LENGTH_SHORT).show();
                    Log.d("Test19", "ถึงแล้วนะจ๊ะ ");
                    x = 2;
                    Log.d("Test19", "x ==>" + x);
                }
            }


        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //ค้นหา GPS ปัจจุบัน
        Location gpsLocation = myFindLocation(LocationManager.GPS_PROVIDER);
        Location networkLocation = myFindLocation(LocationManager.NETWORK_PROVIDER);
        if (gpsLocation != null) {
            latStartADouble = gpsLocation.getLatitude();
            lngStartADouble = gpsLocation.getLongitude();
        }
        else if (networkLocation != null) {
            latStartADouble = networkLocation.getLatitude();
            lngStartADouble = networkLocation.getLongitude();
        }
        else if (gpsLocation == null) {
            Toast.makeText(getApplicationContext(), "ไม่มี GPS", Toast.LENGTH_SHORT).show();
        }
    }

    //หาระยะทาง
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;

        return (dist);
    }

    private static double rad2deg(double rad) {

        return (rad * 180 / Math.PI);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    //รับค่าที่ส่งมา
    private void intentlistonline() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Busnum = bundle.getString("รถประจำทางสาย");
            StartEnd = bundle.getString("วิ่งจาก");
            StopFirst = bundle.getString("ป้ายรถประจำทางเริ่มต้น");
            StopEnd = bundle.getString("ป้ายรถประจำทางปลายทาง");
            Sum = bundle.getString("ต้องผ่านทั้งหมด");
            KM = bundle.getString("ระยะทาง");
            LatBusStopEnd = bundle.getDouble("LatBusStopEnd");
            LngBusStopEnd = bundle.getDouble("LngBusStopEnd");


            TextView tg1 = (TextView) findViewById(R.id.Numberbus);
            TextView tg2 = (TextView) findViewById(R.id.Nowbus);
            TextView tg3 = (TextView) findViewById(R.id.NameStopFirst);
            TextView tg4 = (TextView) findViewById(R.id.NextStopEnd);
            TextView tg5 = (TextView) findViewById(R.id.SumBusStop);
            TextView tg6 = (TextView) findViewById(R.id.Distance);


            tg1.setText("รถประจำทางสาย " + Busnum);
            tg2.setText("เส้นทางเดินรถ " + StartEnd);
            tg3.setText("ป้ายรถประจำทางเริ่มต้น " + StopFirst);
            tg4.setText("ป้ายรถประจำทางปลายทาง " + StopEnd);
            tg5.setText("จำนวนป้ายที่ผ่านระหว่างทางทั้งหมด " + Sum + " ป้าย");
            tg6.setText("ระยะทาง " + KM);

        }
    }

    public void OnclickBackEnd(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("กลับหน้าหลัก");
        dialog.setIcon(R.drawable.iconn);
        dialog.setCancelable(true);
        dialog.setMessage("คุณต้องการกลับหน้าหลัก หรือไม่?");

        dialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(notifications_online.this, home.class);
                startActivity(intent);
                finish();
            }
        });

        dialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("ออกจากแอปพลิเคชัน");
        dialog.setIcon(R.drawable.iconn);
        dialog.setCancelable(true);
        dialog.setMessage("คุณต้องการออกจากแอปพลิเคชัน หรือไม่?");

        dialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

}