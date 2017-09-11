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


import java.util.ArrayList;

public class notifications_offline extends AppCompatActivity {
    private LocationManager locationManager;
    double latChanged;
    double lngChanged;
    double latStartADouble;
    double lngStartADouble;

    String Bus;
    int SumBus;
    String strSumBut;
    ArrayList<String> LatBus = new ArrayList<String>();
    ArrayList<String> LngBus = new ArrayList<String>();
    ArrayList<String> NameBus = new ArrayList<String>();
    int x =0;
    int p = 0;
    int SumBus2;

    ArrayList<Double> TestLat = new ArrayList<Double>();
    ArrayList<Double> TestLng = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_offline);
        setupLocation();
        getValueIntent();
        forTestLatlng();
    }

    private void forTestLatlng() {
        for (int i = 0; i < LatBus.size(); i++) {
            TestLat.add(Double.parseDouble(LatBus.get(i)));
            Log.d("LocationListener", "TestLat " + TestLat);
        }
        for (int x = 0; x < LngBus.size(); x++) {
            TestLng.add(Double.parseDouble(LngBus.get(x)));
            Log.d("LocationListener", "TestLng " + TestLng);
        }
    }


    private void getValueIntent() {

        String tag = "getValueIntent";
        SumBus = getIntent().getIntExtra("SumBus", 0);
        LatBus = getIntent().getStringArrayListExtra("LatBus");
        LngBus = getIntent().getStringArrayListExtra("LngBus");
        NameBus = getIntent().getStringArrayListExtra("NameBus");
        Bus = getIntent().getStringExtra("Bus");
        strSumBut = Integer.toString(SumBus);
        SumBus2 = SumBus - 1;
        Log.d(tag, "LatBus ==>" + LatBus);
        Log.d(tag, "LngBus ==>" + LngBus);
        Log.d(tag, "NameBus ==>" + NameBus);
        Log.d(tag, "NameBus ==>" + Bus);
        TextView tg1 = (TextView) findViewById(R.id.NumberbusOff);
        TextView tg2 = (TextView) findViewById(R.id.Namebus1Off);
        TextView tg3 = (TextView) findViewById(R.id.Namebus2Off);
        TextView tg4 = (TextView) findViewById(R.id.NumberbusstopOff);
        tg1.setText("รถประจำทางสาย " + Bus);
        tg2.setText("ชื่อป้ายรถประจำทางปัจจุบัน : " + NameBus.get(0));
        tg3.setText("ชื่อป้ายรถประจำทางถัดไป : " + NameBus.get(1));
        tg4.setText("จำนวนป้ายที่ผ่านระหว่างทางทั้งหมด " + strSumBut + " ป้าย");
        Toast.makeText(notifications_offline.this,"รถประจำทางสาย " + Bus, Toast.LENGTH_SHORT).show();
        Toast.makeText(notifications_offline.this,"ชื่อป้ายรถประจำทางปัจจุบัน : " + NameBus.get(0), Toast.LENGTH_SHORT).show();
        Toast.makeText(notifications_offline.this,"ชื่อป้ายรถประจำทางถัดไป : " + NameBus.get(1), Toast.LENGTH_SHORT).show();
        Toast.makeText(notifications_offline.this,"จำนวนป้ายที่ผ่าน " + strSumBut + " ป้าย", Toast.LENGTH_SHORT).show();
    }

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
            try {
                latChanged = location.getLatitude();
                lngChanged = location.getLongitude();
                TextView gg2 = (TextView) findViewById(R.id.Namebus1Off);
                TextView gg3 = (TextView) findViewById(R.id.Namebus2Off);
                TextView gg4 = (TextView) findViewById(R.id.NumberbusstopOff);
                ArrayList<Float> dis = new ArrayList<Float>();
                //วนเก็บค่าระยะห่างแต่ละป้าย
                for (int a = 0; a < LngBus.size(); a++) {
                    dis.add((float) distance(TestLat.get(a), TestLng.get(a), latChanged, lngChanged));
                    Log.d("LocationListener", "dis " + dis);
                    //Toast.makeText(getApplicationContext(), "dis  "+dis.get(p), Toast.LENGTH_SHORT).show();
                }
                if (p == 0) {
                    //แจ้งเตือนเมื่ออยู่ที่ป้ายเริ่มต้น
                    if (dis.get(p) < 0.03 && x == 0) {
                        Log.d("Test19", "ป้ายบัจุบัน " + NameBus.get(p));
                        Log.d("Test19", "ป้ายต่อไปคือ " + NameBus.get(p + 1));
                        Toast.makeText(getApplicationContext(), "ป้ายบัจุบัน  " + NameBus.get(p), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "ป้ายถัดไปคือ  " + NameBus.get(p + 1), Toast.LENGTH_SHORT).show();

                        gg2.setText("ชื่อป้ายรถประจำทางปัจจุบัน : " + NameBus.get(p));
                        gg3.setText("ชื่อป้ายรถประจำทางถัดไป : " + NameBus.get(p + 1));
                        p = p + 1;

                        gg4.setText("จำนวนป้ายที่เหลือ " + SumBus2 + " ป้าย");
                        SumBus2--;
                    }
                    //แจ้งเตือนเมื่อไม่อยู่ที่ป้ายเริ่มต้นและใกล้จะถึงป้ายเริ่มต้น
                    if (dis.get(p) < 0.07 && dis.get(p) > 0.04 && x == 0) {
                        Toast.makeText(getApplicationContext(), "ใกล้ถึง  " + NameBus.get(p) + "แล้ว", Toast.LENGTH_SHORT).show();
                        Log.d("Test19", "ใกล้ถึง" + NameBus.get(p) + "แล้ว");
                        x = 2;
                    }
                    //แจ้งเตือนเมื่อถึงป้ายเริ่มต้น
                    if (dis.get(p) < 0.02 && x == 2) {
                        Log.d("Test19", "ป้ายบัจุบัน " + NameBus.get(p));
                        Log.d("Test19", "ป้ายต่อไปคือ " + NameBus.get(p + 1));
                        Toast.makeText(getApplicationContext(), "ป้ายบัจุบัน  " + NameBus.get(p), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "ป้ายถัดไปคือ  " + NameBus.get(p + 1), Toast.LENGTH_SHORT).show();
                        gg4.setText("จำนวนป้ายที่เหลือ " + SumBus2 + " ป้าย");
                        gg2.setText("ชื่อป้ายรถประจำทางปัจจุบัน : " + NameBus.get(p));
                        gg3.setText("ชื่อป้ายรถประจำทางถัดไป : " + NameBus.get(p + 1));
                        x = 0;
                        p = p + 1;
                        SumBus2--;


                    }

                }
                if (p>0) {
                    //แจ้งเตือนใกล้ถึงป้าย เมื่อระยะห่างระหว่าง 70ม. ถึง 40ม.
                    if (dis.get(p) < 0.07 && dis.get(p) > 0.04 && x == 0) {
                            Toast.makeText(getApplicationContext(), "ใกล้ถึง  " + NameBus.get(p) + "แล้ว", Toast.LENGTH_SHORT).show();
                            Log.d("Test19", "ใกล้ถึง" + NameBus.get(p) + "แล้ว");
                            x = 1;

                    }
                    //แจ้งเตือนถึงป้าย เมื่อระยะห่างน้อยกว่า 20ม.
                    if (dis.get(p) < 0.02 && x == 1) {
                        Log.d("Test19", "ป้ายบัจุบัน " + NameBus.get(p));
                        Toast.makeText(getApplicationContext(), "ป้ายบัจุบัน  " + NameBus.get(p), Toast.LENGTH_SHORT).show();
                        gg2.setText("ชื่อป้ายรถประจำทางปัจจุบัน : " + NameBus.get(p));
                        p = p + 1;
                        gg4.setText("จำนวนป้ายที่เหลือ " + SumBus2 + " ป้าย");
                        SumBus2--;
                        //ถ้ายังไม่ถึงป้ายสุดท้าย แจ้งเตือนป้ายถัดไป
                        if (p < dis.size()) {
                            x = 0;
                            Log.d("Test19", "p " + p);
                            Log.d("Test19", "ป้ายถัดไปคือ " + NameBus.get(p));
                            Toast.makeText(getApplicationContext(), "ป้ายถัดไปคือ  " + NameBus.get(p), Toast.LENGTH_SHORT).show();
                            gg3.setText("ชื่อป้ายรถประจำทางถัดไป : " + NameBus.get(p));
                        }//ถ้าไม่ใช่ ให้ x = 2
                        else {
                            x = 2;
                        }
                    }
                    //แจ้งเตือนเลยป้าย
                    if (dis.get(dis.size()-1) > 0.04&& x == 2) {
                        Log.d("Test19", "เลย " + NameBus.get(dis.size() - 1));
                        Toast.makeText(getApplicationContext(), "เลย  " + NameBus.get(dis.size()-1)+"แล้ว", Toast.LENGTH_SHORT).show();
                        Vibrator v3 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v3.vibrate(3000);
                        x = 3;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
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
        //ค้นหา GPS
        Location gpsLocation = myFindLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            latStartADouble = gpsLocation.getLatitude();
            lngStartADouble = gpsLocation.getLongitude();
        }
        if (gpsLocation == null) {
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

    public void OnclickEnd(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("กลับหน้าหลัก");
        dialog.setIcon(R.drawable.iconn);
        dialog.setCancelable(true);
        dialog.setMessage("คุณต้องการกลับหน้าหลัก หรือไม่?");

        dialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(notifications_offline.this, home.class);
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

