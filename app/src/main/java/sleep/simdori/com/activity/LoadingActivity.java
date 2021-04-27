package sleep.simdori.com.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.R;
import sleep.simdori.com.util.SharedPrefUtil;

public class LoadingActivity extends AppCompatActivity {

    String[] permission_list = {
            Manifest.permission.CAMERA, Manifest.permission.ACTIVITY_RECOGNITION
    };
//    String[] permission_list = {
//            Manifest.permission.CAMERA,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//    };

    // Values
    private String unique_ID 	= null;
    private int login_status = 0;
    private boolean aes_status, mqtt_ssl_status = false;

    // API
    private SharedPrefUtil pref = null;

    // MQTT
    private InputStream is_ca, is_crt, is_key = null;
    private FileOutputStream fos_ca, fos_crt, fos_key = null;
    private File outDir, file_ca, file_crt, file_key = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // 사용자 정보 가져오기
        pref = new SharedPrefUtil(this);
//        user_id = pref.getValue(SharedPrefUtil.USER_ID, "");
//        user_pw = pref.getValue(SharedPrefUtil.USER_PASSWORD, "");
//        // 기기 정보 가져오기
//        unique_ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//        pref.put(SharedPrefUtil.WIFI_CONNECTION, false);
//        // 화재알람 여부
//        pref.put(SharedPrefUtil.ALERT_FIRE_STATUS, true);
//
        // 로그인 상태 체크 (로그아웃: 0/ 로그인: 1/ 자동로그인: 2)
        login_status = pref.getValue(SharedPrefUtil.LOGIN_STATUS, AppConst.LoginStatus_Logout);
//
//        // 센서 처리 유무 - True
//        pref.put(SharedPrefUtil.SENSOR_STATUS, true);
//        // 응답 처리 여부 - True
//        pref.put(SharedPrefUtil.RESPONSE_PROCESSING, true);
//        // AES 암호화 여부 - True
//        pref.put(SharedPrefUtil.AES_STATUS, true);
//
//        // MQTT 암호화 여부 - True
//        pref.put(SharedPrefUtil.MQTT_SSL, true);
//        // MQTT 암호화 여부 - Azure
//        //pref.put(SharedPrefUtil.MQTT_SSL, false);
//
//        // MQTT 암호화 여부 - True
//        pref.put(SharedPrefUtil.MQTT_SSL, true);
//        // MQTT 암호화 여부 - Azure
//        //pref.put(SharedPrefUtil.MQTT_SSL, false);

        // 암호화 처리
//        aes_status = pref.getValue(SharedPrefUtil.AES_STATUS, false);
//        mqtt_ssl_status = pref.getValue(SharedPrefUtil.MQTT_SSL, false);
        mqtt_ssl_status = true;
        Log.i(AppConst.TAG, "SplashActivity - 로그인 여부: " + login_status + " / AES 암호화: " + aes_status + " / MQTT SSL 암호화: " + mqtt_ssl_status);
        if(mqtt_ssl_status) {
            // 파일 경로 저장하기
            if( Build.VERSION.SDK_INT < 29) outDir = new File(AppConst.MQTT_FilePath);
            else outDir = new File(AppConst.MQTT_FilePath);
//            outDir = new File(AppConst.MQTT_FilePath); //"/data/data/sleep.simdori.com/");
            boolean status = outDir.mkdirs();
            if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SplashActivity - 폴더 경로: " + outDir.getAbsolutePath() + " / 폴더 여부: " + status);

            // 인증서 파일 이동하기
            try {
                checkVerify();
                is_ca = getAssets().open("all-ca.crt");
                int size = is_ca.available();
                byte[] buffer = new byte[size];
                file_ca = new File(outDir + "/" + "all-ca.crt");
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SplashActivity - CA File 경로: " + file_ca.getAbsolutePath());

                fos_ca = new FileOutputStream(file_ca);
                for (int c=is_ca.read(buffer); c!=-1; c=is_ca.read(buffer)){
                    fos_ca.write(buffer, 0, c);
                }
                is_ca.close();
                fos_ca.close();

                is_crt = getAssets().open("client.crt");
                size = is_crt.available();
                buffer = new byte[size];
                file_crt = new File(outDir + "/" + "client.crt");
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SplashActivity - Clinet File 경로: " + file_crt.getAbsolutePath());

                fos_crt = new FileOutputStream(file_crt);
                for (int c=is_crt.read(buffer); c!=-1; c=is_crt.read(buffer)){
                    fos_crt.write(buffer, 0, c);
                }
                is_crt.close();
                fos_crt.close();

                is_key = getAssets().open("client.key");
                size = is_key.available();
                buffer = new byte[size];
                file_key = new File(outDir + "/" + "client.key");
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SplashActivity - KEY File 경로: " + file_key.getAbsolutePath());

                fos_key = new FileOutputStream(file_key);
                for (int c=is_key.read(buffer); c!=-1; c=is_key.read(buffer)){
                    fos_key.write(buffer, 0, c);
                }
                is_key.close();
                fos_key.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // SSL 암호화가 필요없으므로 처리X
        }

        startLoading();

    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkVerify();
                checkPermission();
            }
        }, 3000); //로딩화면 시간 3초
    }

    private void checkPermission(){
        System.out.println("LoadingActivity_checkPermission() 진입");
        int count=0;
        for(String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);
            System.out.println("LoadingActivity_chk : "+chk);
            System.out.println("LoadingActivity_PackageManager.PERMISSION_DENIED : "+PackageManager.PERMISSION_DENIED);
            if(chk == PackageManager.PERMISSION_DENIED){
                System.out.println("chk == PackageManager.PERMISSION_DENIED진입");
                Intent intent = new Intent(getApplicationContext(), Guide1stActivity.class);
                startActivity(intent);
                finish();
            }else{
//                if (login_status == 1) {//자동로그인상태라면
                if (true) {//자동로그인상태라면
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
//        if(count==permission_list.length) {//필요한 모든 권한이 승인되었다면
//
//        }
    }

    public void checkVerify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) { }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

}
