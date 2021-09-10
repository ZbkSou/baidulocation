package com.zbk.savedemo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String FLAG = "UP_LOCATION";
    LocationBroadcast locationBroadcast;

    Button stopService;
    Button location;
    Button hide;
    TextView textView;
    TextView tvTime;
    TextView tvLocation;

    TextView tvRefresh ;
    Intent intent;
    String name;
    private MapView mMapView = null;
    private boolean showMap = true;
    MyConnection conn = new MyConnection();
    ForegroundLocationService.LocationBinder locationBinder;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = getIntent().getStringExtra("name");

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        textView = findViewById(R.id.textView);
        tvLocation = findViewById(R.id.tv_location);
        tvTime = findViewById(R.id.tv_time);
        tvRefresh= findViewById(R.id.tv_refresh);
        textView.setText("当前用户：" + (name!=null?name:"未输入用户名"));
        stopService = (Button) findViewById(R.id.stop);
        stopService.setVisibility(View.GONE);
        stopService.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onClick()");
                unbindService(conn);
                stopService(intent);

            }
        });

        location = (Button) findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationBinder != null) {
                    BDLocation bdLocation = locationBinder.getLocation();
                    if (mMapView != null && bdLocation != null && mMapView.getVisibility() == View.VISIBLE) {
                        tvRefresh.setText("手动显示："+bdLocation.getTime()+"\n"+bdLocation.getLocType());
                        mMapView.getMap().clear();
                        //定义Maker坐标点
                        LatLng point = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
//构建Marker图标
                        BitmapDescriptor bitmap = BitmapDescriptorFactory
                          .fromResource(R.drawable.local);
//构建MarkerOption，用于在地图上添加Marker
                        OverlayOptions option = new MarkerOptions()
                          .position(point)
                          .icon(bitmap);
//在地图上添加Marker，并显示
                        mMapView.getMap().addOverlay(option);
                        mMapView.getMap().setMapStatus(MapStatusUpdateFactory.newLatLng(point));

                    }
                }

            }
        });
        hide = (Button) findViewById(R.id.hide);
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMapView != null) {

                    mMapView.setVisibility(showMap ? View.GONE : View.VISIBLE);
                    showMap = !showMap;
                    hide.setText(showMap ? "隐藏地图" : "显示地图");
                }
            }
        });
//        启动定位
        Log.d(TAG, "startService()");

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        tvTime.setText("启动时间："+sdf.format(date));
        intent = new Intent(MainActivity.this, ForegroundLocationService.class);
        intent.putExtra("name", name);
        startForegroundService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(locationBroadcast);
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();


        //注册广播
        locationBroadcast = new LocationBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FLAG);
        registerReceiver(locationBroadcast, intentFilter);
        locationBroadcast.SetOnUpdateUI(new LocationBroadcast.OnUpdateUI() {
            @Override
            public void updateUI(double latitude, double longitude,String data) {
                if (mMapView != null && mMapView.getVisibility() == View.VISIBLE) {
                    tvLocation.setText(latitude + "==" + longitude);
                    tvRefresh.setText("自动显示："+data);
                    mMapView.getMap().clear();
                    //定义Maker坐标点
                    LatLng point = new LatLng(latitude, longitude);
//构建Marker图标
                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                      .fromResource(R.drawable.local);
//构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions()
                      .position(point)
                      .icon(bitmap);
//在地图上添加Marker，并显示
                    mMapView.getMap().addOverlay(option);
                    mMapView.getMap().setMapStatus(MapStatusUpdateFactory.newLatLng(point));
                }
            }
        });
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();

        if (locationBinder != null) {
            BDLocation bdLocation = locationBinder.getLocation();
            if (mMapView != null && bdLocation != null && mMapView.getVisibility() == View.VISIBLE) {
                tvRefresh.setText("自动显示："+bdLocation.getTime()+"\n"+bdLocation.getLocType());
                mMapView.getMap().clear();
                //定义Maker坐标点
                LatLng point = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
//构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                  .fromResource(R.drawable.local);
//构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                  .position(point)
                  .icon(bitmap);
//在地图上添加Marker，并显示
                mMapView.getMap().addOverlay(option);
                mMapView.getMap().setMapStatus(MapStatusUpdateFactory.newLatLng(point));

            }
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        stopService(intent);
        Log.d(TAG, "onDestroy()");
        mMapView.onDestroy();
    super.onDestroy();
    }



    private class MyConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("call", "onServiceConnected");
            locationBinder = (ForegroundLocationService.LocationBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("call", "onServiceDisconnected");
        }
    }
}