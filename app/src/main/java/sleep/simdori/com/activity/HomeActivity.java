package sleep.simdori.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import sleep.simdori.com.R;
import sleep.simdori.com.fragment.BPMResultFragment;
import sleep.simdori.com.fragment.HomeFragment;
import sleep.simdori.com.fragment.RecordFragment;
import sleep.simdori.com.fragment.SettingFragment;
import sleep.simdori.com.mqtt.MQTTservice;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction;

    HomeFragment homeFragment = new HomeFragment();
    RecordFragment recordFragment = new RecordFragment();
    SettingFragment settingFragment = new SettingFragment();
    BPMResultFragment bpmResultFragment = new BPMResultFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        startService(new Intent(this, MQTTservice.class));
        bottomNavigationView = findViewById(R.id.bottomNavBar);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragment, homeFragment).commitAllowingStateLoss();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.action_home:
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mainFragment, homeFragment).commitAllowingStateLoss();
                        return true;
                    case R.id.action_Sleep_record:
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mainFragment, recordFragment).commitAllowingStateLoss();
                        return true;
                    case R.id.action_BPM_record:
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mainFragment, bpmResultFragment).commitAllowingStateLoss();
                        return true;
                    case R.id.action_setting:
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mainFragment, settingFragment).commitAllowingStateLoss();
                        return true;
                }
                return false;
            }
        });

    }

    // Fragment로 사용할 MainActivity의 layout 선택
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragment, fragment).commit();
    }
    //데이터 전달과 함께 화면전환
    public void replaceFragment(Fragment fragment, Bundle bundle) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.mainFragment, fragment).commit();
    }

    //뒤로가기 버튼을 뺏어올 리스너 등록
    public interface onKeyBackPressedListener {
        void onBackKey();
    }
    private onKeyBackPressedListener mOnKeyBackPressedListener;
    public void setOnKeyBackPressedListener(onKeyBackPressedListener listener) {
        mOnKeyBackPressedListener = listener;
    }

    @Override public void onBackPressed() {
        if (mOnKeyBackPressedListener != null) {
            mOnKeyBackPressedListener.onBackKey();
        }else {
            if(getSupportFragmentManager().getBackStackEntryCount()==0){

            }else{

            }
        }

    }
}
