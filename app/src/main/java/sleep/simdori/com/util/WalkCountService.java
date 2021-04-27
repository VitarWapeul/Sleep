package sleep.simdori.com.util;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import sleep.simdori.com.R;
import sleep.simdori.com.activity.HomeActivity;

public class WalkCountService extends Service implements SensorEventListener {

    NotificationChannel channel;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder notification;
    SharedPrefUtil pref;
    public MyBinder mMyBinder = new MyBinder();

    public class MyBinder extends Binder { //바인드 클래스를 생성
        public WalkCountService getService() { // 서비스 객체를 리턴
            return WalkCountService.this;
        }
    }

    private int mStepDetector;
    private int mStepCountor;
    private WalkCallback callback;

    public void setCallback(WalkCallback callback) {
        this.callback = callback;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("WalkCountService - onBind");
        return mMyBinder;
    }

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private Sensor stepCountSensor;

    @Override
    public void onCreate() {
        super.onCreate();
        pref = new SharedPrefUtil(getApplicationContext());
        pref.put("step",-1);
        System.out.println("WalkCountService - onCreate호출");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepDetectorSensor == null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
// COUNTER

        if (stepCountSensor == null) {
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("WalkCountService - onStartCommand호출");

        // PendingIntent를 이용하면 포그라운드 서비스 상태에서 알림을 누르면 앱의 MainActivity를 다시 열게 된다.
        Intent testIntent = new Intent(getApplicationContext(), HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, testIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // 오래오 윗버젼일 때는 아래와 같이 채널을 만들어 Notification과 연결해야 한다.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("channel", "play!!", NotificationManager.IMPORTANCE_DEFAULT);

            // Notification과 채널 연걸
            mNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            mNotificationManager.createNotificationChannel(channel);

            // Notification 세팅
            notification = new NotificationCompat.Builder(getApplicationContext(), "channel")
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("심도리")
                    .setContentIntent(pendingIntent)
                    .setContentText("걸음 수 : 0");

            // id 값은 0보다 큰 양수가 들어가야 한다.
            mNotificationManager.notify(1, notification.build());
            // foreground에서 시작
            startForeground(1, notification.build());
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepDetectorSensor == null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
// COUNTER
        if (stepCountSensor == null) {
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("WalkCountService - onUnbind호출");

        unRegistManager();
        if (callback != null)
            callback.onUnbindService();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        }
        super.onDestroy();
    }

    //센서 코드
    public void unRegistManager() { //혹시 모를 에러상황에 트라이 캐치
        try {
            sensorManager.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        System.out.println("WalkCountService - onSensorChanged호출");
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0f) {
                mStepDetector += event.values[0];
                Intent intent = new Intent(MyReceiver.MY_ACTION);
                intent.putExtra("step",mStepDetector);
                sendBroadcast(intent);
                notification.setContentText("걸음 수 : "+mStepDetector);
                mNotificationManager.notify(1, notification.build());
                pref.put("step",mStepDetector);
                if (callback != null)
                    callback.onWalkCallback(mStepDetector);
                else System.out.println("WalkCountService - onSensorChanged : callback객체가 null입니다.");
                Log.e("스텝 디텍터", "" + event.values[0]);
            }
        }
        else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            mStepDetector += event.values[0];
            if (callback != null)
                callback.onWalkCallback(mStepCountor);
            else System.out.println("WalkCountService - onSensorChanged : callback객체가 null입니다.");
            Log.e("스텝 카운트", "" + event.values[0]);
        }


    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



}


//=====================================================================================================


//public class WalkCountService extends Service implements SensorEventListener {
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        Log.d("WalkCountService","onCreate호출");
//        super.onCreate();
//    }
//
//    @Override
//    public void onDestroy() {
//
//        super.onDestroy();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d("WalkCountService","onStartCommand호출");
//        if(intent==null){
//            return Service.START_STICKY;
//        }else{
//            //기능 넣기
//        }
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }
//}
