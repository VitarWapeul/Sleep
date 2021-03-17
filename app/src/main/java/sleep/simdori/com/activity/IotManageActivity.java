package sleep.simdori.com.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import sleep.simdori.com.R;
import sleep.simdori.com.asynctask.Get_IotAsynctask;
import sleep.simdori.com.asynctask.Set_IotAsyncTask;
import sleep.simdori.com.asynctask.Set_IotDeleteAsyncTask;
import sleep.simdori.com.util.SharedPrefUtil;

public class IotManageActivity extends Activity {

    Intent intent;

    // API
    private SharedPrefUtil pref = null;
    private AsyncTask<String, Void, Integer> mAsyncTask_registDevice, mAsyncTask_deleteDevice, mAsyncTask_getDevice = null;

    // View
    Button addButton;
    Button deleteButton;
    Button backButton;
    TextView userNameText;
    TextView registeredIot;

    // Values
    String userName;
    String device_mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iotmanage);

        pref = new SharedPrefUtil(this);
        userName = pref.getValue(SharedPrefUtil.USER_ID, "");
        device_mac = pref.getValue(SharedPrefUtil.DEVICE_MAC, "");

        addButton = (Button) findViewById(R.id.iotmanageadd);
        deleteButton = (Button) findViewById(R.id.iotmanagedelete);
        backButton = (Button) findViewById(R.id.iotmanagebackbutton);
        userNameText = (TextView) findViewById(R.id.iotmangeusername);
        userNameText.setText(userName);
        registeredIot = (TextView) findViewById(R.id.registered_iot);
        registeredIot.setText(device_mac);

        getDevice(userName);

        // QR코드 인식을 통해 장치 등록
        addButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(IotManageActivity.this);
                intentIntegrator.setBeepEnabled(false); //QR 인식시 소리
                intentIntegrator.initiateScan();
            }
        });

        deleteButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                deleteDevice(userName);
            }
        });

        backButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // 메인 화면으로 이동
        intent = new Intent(IotManageActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    // QR 코드 인식 후
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                    Toast.makeText(this, "기기가 등록되었습니다.", Toast.LENGTH_LONG).show();
                device_mac = result.getContents();
                registerDevice(userName, device_mac);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void registerDevice(String id, String unique_id){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mAsyncTask_registDevice = new Set_IotAsyncTask(this, id, unique_id).execute();
        } else {
            mAsyncTask_registDevice = new Set_IotAsyncTask(this, id, unique_id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        pref.put(SharedPrefUtil.UNIQUE_ID, unique_id);
        pref.put(SharedPrefUtil.DEVICE_MAC, unique_id);
        registeredIot.setText(unique_id);
    }

    public void deleteDevice(String id){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mAsyncTask_deleteDevice = new Set_IotDeleteAsyncTask(this, id).execute();
        } else {
            mAsyncTask_deleteDevice = new Set_IotDeleteAsyncTask(this, id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        pref.put(SharedPrefUtil.DEVICE_MAC, "");
        device_mac = "";
        registeredIot.setText(device_mac);
    }

    public void getDevice(String id){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mAsyncTask_getDevice = new Get_IotAsynctask(this, id).execute();
        } else {
            mAsyncTask_getDevice = new Get_IotAsynctask(this, id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        device_mac = pref.getValue(SharedPrefUtil.DEVICE_MAC, "");
        registeredIot.setText(device_mac);
    }
}
