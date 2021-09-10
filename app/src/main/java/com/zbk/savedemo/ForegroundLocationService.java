package com.zbk.savedemo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ZBK on 2021-08-04.
 *
 * @function
 */
public class ForegroundLocationService extends Service {

    private static final String TAG = ForegroundLocationService.class.getSimpleName();
    String CHANNEL_ONE_ID = "CHANNEL_ONE_ID";
    String CHANNEL_ONE_NAME = "CHANNEL_ONE_ID";
    public int anHour = 1000 * 60 * 5; //记录间隔时间
    public LocationClient mLocationClient = null;
    public BDLocation mlocation;
    public  long preTime=0;
    String name;
    private MyLocationListener myListener = new MyLocationListener(new MyLocationListener.OnListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.e(TAG, "onReceiveLocation()"+location.getLatitude()+"=="+
              location.getLongitude()+"ss"+location.getLocTypeDescription());

            mlocation = location;

            if (location.getLocType()!=61&&location.getLocType()!=161) {
                return;
            }
            preTime = System.currentTimeMillis();
            Intent it = new Intent();
            it.setAction(MainActivity.FLAG);
            it.putExtra("latitude", location.getLatitude());
            it.putExtra("longitude", location.getLongitude());
            it.putExtra("data", location.getTime()+"\n"+location.getLocType());

            sendBroadcast(it);

            setMsg(location );
            if(mLocationClient.isStarted()){
                mLocationClient.stop();
            }
        }
    });

        private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    mLocationClient.stop();
                    mLocationClient.start();
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，设置定位模式，默认高精度
//LocationMode.Hight_Accuracy：高精度；
//LocationMode. Battery_Saving：低功耗；
//LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
//可选，设置返回经纬度坐标类型，默认GCJ02
//GCJ02：国测局坐标；
//BD09ll：百度经纬度坐标；
//BD09：百度墨卡托坐标；
//海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(0);
//可选，设置发起定位请求的间隔，int类型，单位ms
//如果设置为0，则代表单次定位，即仅定位一次，默认为0
//如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
//可选，设置是否使用gps，默认false
//使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(false);
//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.SetIgnoreCacheException(false);
//可选，设置是否收集Crash信息，默认收集，即参数为false


        option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setNeedNewVersionRgc(true);
//可选，设置是否需要最新版本的地址信息。默认需要，即参数为true

        mLocationClient.setLocOption(option);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        name = intent.getStringExtra("name");

        NotificationChannel notificationChannel;
//进行8.0的判断
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
              CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
           NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }

        }


        Intent newintent = new Intent(this, MainActivity.class);
        newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, newintent, 0);
        Notification notification;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this).setAutoCancel(true).
              setSmallIcon(R.mipmap.ic_launcher).setTicker("前台Service启动").setContentTitle("前台Service运行中").
              setContentText("这是一个正在运行的前台Service").setWhen(System.currentTimeMillis()).
              setContentIntent(pendingIntent).build();
        } else {
            notification = new Notification.Builder(this, CHANNEL_ONE_ID).setChannelId(CHANNEL_ONE_ID)
              .setTicker("Nature")
              .setSmallIcon(R.mipmap.ic_launcher)
              .setContentTitle("这是一个测试标题")
              .setContentIntent(pendingIntent)
              .setContentText("前台服务正在运行")
              .build();
        }
        notification.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(1, notification);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTask(), 1, anHour);

        return START_STICKY;
    }
    private class MyTask extends TimerTask {
        @Override
        public void run() {
            Log.e(TAG, "executed at " + new Date().toString());

            mHandler.sendEmptyMessage(1);

        }
    }


    @Override
    public void onDestroy() {
//        mLocationClient.stop();
        mLocationClient.disableLocInForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("call", "onBind");
        LocationBinder mbind = new LocationBinder();
        Log.e("call", mbind.toString());
        return mbind;
    }

    public class LocationBinder extends Binder {
        public BDLocation getLocation() {
            return mlocation;
        }
    }

    public void setMsg(BDLocation location) {
        double latitude = location.getLatitude();
        double longitude =  location.getLongitude();
        Log.d(TAG, latitude + "==" + longitude);
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String requestBody = "{\"app_name\":\"0000\",\"time_int\":\"" + System.currentTimeMillis() +
          "\",\"original_data\":\""+location.toString()+
          "\",\"lng\":" + longitude + ",\"lat\":" + latitude + ",\"extra\":\"" + name + "\"}";
        Log.d("ss",requestBody);
        Request request = new Request.Builder()
          .url("http://wangpro.cn/api/user/location/monitor")
          .post(RequestBody.create(mediaType, requestBody))
//          .post(requestBody)
          .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();

                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });
    }
}