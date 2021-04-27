package sleep.simdori.com.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.R;
import sleep.simdori.com.activity.HomeActivity;
import sleep.simdori.com.activity.LoginActivity;
import sleep.simdori.com.dialog.CustomDialog_State;
import sleep.simdori.com.mqtt.MQTTservice;
import sleep.simdori.com.util.DatabaseHandler;
import sleep.simdori.com.util.MyReceiver;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;
import sleep.simdori.com.util.WalkCallback;
import sleep.simdori.com.util.WalkCountService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    TimeZone koreaTimeZone;
    Date date = new Date();
    DateFormat dateFormat;

    // View
    private TextView userNameTextView;
    private TextView currentTime;
    private TextView countWalk;
    private TextView customDialog_state_walk_tv;
    private TextView customDialog_state_relax_tv;
    private TextView customDialog_state_exercise_tv;
    private TextView customDialog_state_chill_tv;
    Button cameraBpmMeasureStartButton;
    Button mmWaveBpmMeasureStartButton;
    Button sleepMeasureStartButton;
    Button mmWaveBackupButton;
    Button chk_walkButton;

    // Values
    String device_mac;
    String userNameValue;

    // API
    private CustomDialog_State customDialog_state;
    private SharedPrefUtil pref = null;
    private SharedPrefUtil pref2 = null;
    private LinkedList<Integer> queue = null;
    private Activity mActivity;
    private Context mContext;
    private Package mPackage;
    // MQTT
    private IntentFilter intentFilter 		= null;
    private Messenger service = null;
    private final Messenger serviceHandler = new Messenger(new ServiceHandler());
    private boolean mBound = false;
    private Message msg = null;
    private Bundle data = null;
    private int status = 0, mid_device = 0 ;

    //만보기
    private DatabaseHandler dbhandler;
    private SQLiteDatabase sqlDB;
    private BroadcastReceiver mReciver;
    boolean WalkBtnToggle = false;
    int step;
    boolean isWalkService = false; // 서비스 중인 확인용
    private WalkCountService walkCountService; // 서비스 클래스 객체를 선언
    private Intent wlak_service_intent; //서비스 객체를 가지고 있는 인텐트 객체
    private WalkCallback WalkCallback = new WalkCallback() { //서비스 내부로 Set되어 스텝카운트의 변화와 Unbind의 결과를 전달하는 콜백 객체의 구현체
        @Override
        public void onWalkCallback(int step) {

            System.out.println("HomeFragment - onWalkCallback호출");
            countWalk.setText("오늘 걸음 수 : " + step);
        }

        @Override
        public void onUnbindService() {
            System.out.println("HomeFragment - onUnbindService호출");
            isWalkService = false;

            Toast.makeText(mActivity, "디스바인딩", Toast.LENGTH_SHORT).show();
        }
    };
    //만보기서비스시작
    private ServiceConnection WalkserviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            System.out.println("===================onServiceConnected=====================");
            Toast.makeText(mActivity, "예스바인딩", Toast.LENGTH_SHORT).show();
            WalkCountService.MyBinder mb = (WalkCountService.MyBinder) binder;
            walkCountService = mb.getService(); //
            walkCountService.setCallback(WalkCallback);

        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            System.out.println("===================onServiceDisconnected=====================");
        }
    };

    void init_walk(Calendar calendar)
    {
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        Boolean dailyNotify = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION, true);
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = mActivity.getPackageManager();
        ComponentName receiver = new ComponentName(mActivity, MyReceiver.class);
        Intent alarmIntent = new Intent(mActivity, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) mActivity.getSystemService(Context.ALARM_SERVICE);


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {


            if (alarmManager != null) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        }
//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            //MQTT서비스연결
            service = new Messenger(binder);
            mBound = true;
            msg = Message.obtain(null, MQTTservice.REGISTER);
            try {
                service.send(msg);
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - MQTT ServiceConnection onServiceConnected()");
            } catch (RemoteException e) {
                // MQTT 토픽을 처리하지 못하였습니다.
                e.printStackTrace();
                ToastUtils.ToastShow(getActivity(), getString(R.string.msg_mqtt_fetch_error));
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - MQTT ServiceConnection Failed!!!");
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            service = null;
            mBound = false;
            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - MQTT ServiceConnection onServiceDisconnected()");
        }
    };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static sleep.simdori.com.fragment.HomeFragment newInstance() {
        return new sleep.simdori.com.fragment.HomeFragment();
    }

    /**
     * MQTT 서비스에 연결하여, ServiceConnection를 전달한다.
     * @see Activity
     */
    @Override
    public void onStart() {
        super.onStart();
        mActivity = getActivity();
        mContext = getContext();
        mPackage = Package.getPackage("sleep.simdori.com.fragment");
        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - onStart()");

        if (!mBound) {
            if (getActivity().bindService(new Intent(getContext(), MQTTservice.class), serviceConnection, 0)) {
                mBound = true;
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - MQTT Bind Successfullly 1: " + serviceConnection.toString());
            } else {
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - MQTT Bind Failed 2: " + serviceConnection.toString());
            }
        } else {
            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - MQTT Binded Already but into here 3: " + serviceConnection.toString());
        }
    }

    /**
     * MQTT 서비스에 연결한 ServiceConnection를 unBind 처리한다.
     * @see Activity
     */
    @Override
    public void onStop() {
        super.onStop();
        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - onStop()");

        if (mBound) {
            getActivity().unbindService(serviceConnection);
            mBound = false;
            if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - MQTT Unbinded Successfullly 1: " + serviceConnection.toString());
        } else {
            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - MQTT Unbinded Already but into here 3: " + serviceConnection.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

        // API
        customDialog_state = new CustomDialog_State(getActivity());
        queue = new LinkedList<Integer>();
        pref = new SharedPrefUtil(getContext());
        device_mac = pref.getValue(SharedPrefUtil.DEVICE_MAC, "");

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(koreaTimeZone);
        date = new Date();

        dbhandler = new DatabaseHandler(mContext);
//        sqlDB = dbhandler.getReadableDatabase();
//        dbhandler.onUpgrade(sqlDB,1,2);
//        sqlDB.close();
        pref.put(SharedPrefUtil.DATE, dateFormat.format(date));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        userNameTextView = (TextView) v.findViewById(R.id.homeFragmentusername);
        userNameValue = pref.getValue(SharedPrefUtil.USER_ID, "");
        userNameTextView.setText(userNameValue);

        currentTime = (TextView) v.findViewById(R.id.currentTime);
        countWalk = (TextView) v.findViewById(R.id.countWalk);
        dateFormat = new SimpleDateFormat("HH:mm");
        koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(koreaTimeZone);
        if(isWalkService) {
            countWalk.setText(pref.getValue("step", -1));
        }
        final Handler handler = new Handler(){
            public void handleMessage(Message msg){
                date = new Date();
                currentTime.setText(dateFormat.format(date));
            }
        };
        Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }

            @Override
            public boolean cancel() {
                return super.cancel();
            }
        };
        timer.schedule(timerTask, 0, 1000);

        cameraBpmMeasureStartButton = (Button) v.findViewById(R.id.bpmMeasureButton);
        cameraBpmMeasureStartButton.setOnClickListener(this);
        mmWaveBpmMeasureStartButton = (Button) v.findViewById(R.id.mmWavebpmMeasureButton);
        mmWaveBpmMeasureStartButton.setOnClickListener(this);
        sleepMeasureStartButton = (Button) v.findViewById(R.id.sleepMeasureButton);
        sleepMeasureStartButton.setOnClickListener(this);
        mmWaveBackupButton = (Button) v.findViewById(R.id.mmWaveBackupButton);
        mmWaveBackupButton.setOnClickListener(this);
        chk_walkButton = (Button) v.findViewById(R.id.chk_walkButton);
        chk_walkButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        Button trackingButton = (Button) v;
        switch(trackingButton.getId()){
            case R.id.bpmMeasureButton:
            {
//                //아래 코드 소통의 부재로인한 필요없는 기능으로 주석처리
//                customDialog_state.show();
//                // 걷기클릭 시
//                customDialog_state.getCustomDialog_state_walk_tv().setOnClickListener(new TextView.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        customDialog_state.getCustomDialog_state_walk_tv().setBackground(getResources().getDrawable(R.drawable.round_top_left_black));
//                        customDialog_state.getCustomDialog_state_relax_tv().setBackground(getResources().getDrawable(R.drawable.round_top_right_white));
//                        customDialog_state.getCustomDialog_state_exercise_tv().setBackground(getResources().getDrawable(R.drawable.round_top_right_white));
//                        customDialog_state.getCustomDialog_state_chill_tv().setBackground(getResources().getDrawable(R.drawable.round_top_right_white));
//                        //글자색설정
//                        customDialog_state.getCustomDialog_state_walk_tv().setTextColor(getResources().getColor(R.color.white));
//                        customDialog_state.getCustomDialog_state_relax_tv().setTextColor(getResources().getColor(R.color.hint));
//                        customDialog_state.getCustomDialog_state_exercise_tv().setTextColor(getResources().getColor(R.color.hint));
//                        customDialog_state.getCustomDialog_state_chill_tv().setTextColor(getResources().getColor(R.color.hint));
//                        if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "HomeFragment - 현재상태 입력");
//                    }
//                });
//                // 휴식클릭 시
//                customDialog_state.getCustomDialog_state_relax_tv().setOnClickListener(new Button.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        customDialog_state.getCustomDialog_state_walk_tv().setBackground(getResources().getDrawable(R.drawable.round_top_right_white));
//                        customDialog_state.getCustomDialog_state_relax_tv().setBackground(getResources().getDrawable(R.drawable.round_top_left_black));
//                        customDialog_state.getCustomDialog_state_exercise_tv().setBackground(getResources().getDrawable(R.drawable.round_top_right_white));
//                        customDialog_state.getCustomDialog_state_chill_tv().setBackground(getResources().getDrawable(R.drawable.round_top_right_white));
//                        //글자색설정
//                        customDialog_state.getCustomDialog_state_walk_tv().setTextColor(getResources().getColor(R.color.hint));
//                        customDialog_state.getCustomDialog_state_relax_tv().setTextColor(getResources().getColor(R.color.white));
//                        customDialog_state.getCustomDialog_state_exercise_tv().setTextColor(getResources().getColor(R.color.hint));
//                        customDialog_state.getCustomDialog_state_chill_tv().setTextColor(getResources().getColor(R.color.hint));
//                        if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "HomeFragment - 현재상태 입력");
//                    }
//                });
//                // 운동클릭 시
//                customDialog_state.getCustomDialog_state_exercise_tv().setOnClickListener(new Button.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //배경색설정
//                        customDialog_state.getCustomDialog_state_walk_tv().setBackground(getResources().getDrawable(R.drawable.round_top_right_white));
//                        customDialog_state.getCustomDialog_state_relax_tv().setBackground(getResources().getDrawable(R.drawable.round_top_right_white));
//                        customDialog_state.getCustomDialog_state_exercise_tv().setBackground(getResources().getDrawable(R.drawable.round_top_left_black));
//                        customDialog_state.getCustomDialog_state_chill_tv().setBackground(getResources().getDrawable(R.drawable.round_top_right_white));
//                        //글자색설정
//                        customDialog_state.getCustomDialog_state_walk_tv().setTextColor(getResources().getColor(R.color.hint));
//                        customDialog_state.getCustomDialog_state_relax_tv().setTextColor(getResources().getColor(R.color.hint));
//                        customDialog_state.getCustomDialog_state_exercise_tv().setTextColor(getResources().getColor(R.color.white));
//                        customDialog_state.getCustomDialog_state_chill_tv().setTextColor(getResources().getColor(R.color.hint));
//
//                        if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "HomeFragment - 현재상태 입력");
//                    }
//                });
//                // 이완클릭 시
//                customDialog_state.getCustomDialog_state_chill_tv().setOnClickListener(new Button.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        customDialog_state.getCustomDialog_state_walk_tv().setBackground(getResources().getDrawable(R.drawable.round_top_right_white));
//                        customDialog_state.getCustomDialog_state_relax_tv().setBackground(getResources().getDrawable(R.drawable.round_top_right_white));
//                        customDialog_state.getCustomDialog_state_exercise_tv().setBackground(getResources().getDrawable(R.drawable.round_top_right_white));
//                        customDialog_state.getCustomDialog_state_chill_tv().setBackground(getResources().getDrawable(R.drawable.round_top_left_black));
//                        //글자색설정
//                        customDialog_state.getCustomDialog_state_walk_tv().setTextColor(getResources().getColor(R.color.hint));
//                        customDialog_state.getCustomDialog_state_relax_tv().setTextColor(getResources().getColor(R.color.hint));
//                        customDialog_state.getCustomDialog_state_exercise_tv().setTextColor(getResources().getColor(R.color.hint));
//                        customDialog_state.getCustomDialog_state_chill_tv().setTextColor(getResources().getColor(R.color.white));
//                        if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "HomeFragment - 현재상태 입력");
//                    }
//                });
//                // 닫기클릭 시
//                customDialog_state.getCustomDialog_state_btn_Cancel().setOnClickListener(new Button.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        customDialog_state.dismiss();
//                    }
//                });
//                // 확인클릭 시
//                customDialog_state.getCustomDialog_state_btn_Confirm().setOnClickListener(new Button.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        customDialog_state.dismiss();
//                        ((HomeActivity)getActivity()).replaceFragment(MeasureFragment.newInstance());
//                    }
//                });
                ((HomeActivity)getActivity()).replaceFragment(MeasureFragment.newInstance());
//                getActivity().startActivity(new Intent(getActivity(), SleepTrackingActivity.class));
                break;
            }
            case R.id.mmWavebpmMeasureButton:
            {
                if(device_mac==""){
                    Toast.makeText(getContext(),"등록된 기기가 없습니다.",Toast.LENGTH_LONG).show();
                    ((HomeActivity)getActivity()).replaceFragment(HomeFragment.newInstance());
                }
                // mmWave를 통한 bpm 측정 추가 예정
                break;
            }
            case R.id.sleepMeasureButton:
            {
                if(device_mac==""){
                    Toast.makeText(getContext(),"등록된 기기가 없습니다.",Toast.LENGTH_LONG).show();
                    ((HomeActivity)getActivity()).replaceFragment(HomeFragment.newInstance());
                }else{
                    publishTopic(AppConst.Topic + device_mac, AppConst.mmWaveStart);

                    ((HomeActivity)getActivity()).replaceFragment(mmWaveSleepMeasureFragment.newInstance());
                }

                break;
            }
            case R.id.mmWaveBackupButton:
            {
                if(device_mac==""){
                    Toast.makeText(getContext(),"등록된 기기가 없습니다.",Toast.LENGTH_LONG).show();
                    ((HomeActivity)getActivity()).replaceFragment(HomeFragment.newInstance());
                }else{
                    publishTopic(AppConst.Topic + device_mac, AppConst.mmWaveBackUp);

                    Toast toast = Toast.makeText(getContext(), "mmWave BackUp Start", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }
                break;
            }
            case R.id.chk_walkButton: {
                if (!isWalkService) {
                    System.out.println("===================만보기 서비스시작=====================");
                    isWalkService = true;
                    wlak_service_intent = new Intent(mContext, WalkCountService.class);
//                    mContext.startService(wlak_service_intent);
//                    walkCountService = new WalkCountService();
//                    walkCountService.setCallback(WalkCallback);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mContext.startForegroundService(wlak_service_intent);
                    } else {
                        mContext.startService(wlak_service_intent);
                    }
//                    mActivity.bindService(wlak_service_intent, WalkserviceConnection, Context.BIND_AUTO_CREATE);
                    countWalk.setVisibility(View.VISIBLE);
                    countWalk.setText("걸음 수 : 0");
                    chk_walkButton.setText("걸음 수 측정 종료");

                } else if (isWalkService) {
                    isWalkService = false;
//                    mActivity.unbindService(WalkserviceConnection);
                    mContext.stopService(wlak_service_intent);
                    countWalk.setVisibility(View.INVISIBLE);
                    countWalk.setText("");
                    chk_walkButton.setText("걸음 수 측정 시작");
                    System.out.println("===================만보기 서비스종료=====================");
                    System.out.println("걸음 수 초기화");
                }
                break;
            }
        }
    }




    /**
     * MQTT 서버에 연결하여 Topic을 발행한다.
     * @param 	topic		: 발행할 토픽
     * @param	message 	: 발행할 메시지
     * @return 	None
     */
    public void publishTopic(String topic, String message) {
        if (!mBound) return;
        if (!topic.isEmpty() && !message.isEmpty()) {
            data = new Bundle();
            data.putCharSequence(MQTTservice.TOPIC, topic);
            data.putCharSequence(MQTTservice.MESSAGE, message);

            msg = Message.obtain(null, MQTTservice.PUBLISH);
            msg.setData(data);
            msg.replyTo = serviceHandler;
            try {

                service.send(msg);
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - PublishTopic() / MQTT Publish Attemping...");
            } catch (RemoteException e) {
                e.printStackTrace();
                ToastUtils.ToastShow(getActivity(), getString(R.string.msg_mqtt_fetch_error));
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - PublishTopic() / MQTT Publish Failed with exception:" + e.getMessage());
            }
        } else {
            // MQTT 토픽을 처리하지 못하였습니다.
            ToastUtils.ToastShow(getActivity(), getString(R.string.msg_mqtt_fetch_error));
            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - PublishTopic() / MQTT Topic and Message Required.");
        }
    }

    /**
     * 바인드된 서비스에서 보내는 메시지를 처리한다.
     */
    @SuppressLint("HandlerLeak")
    class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // MQTT 상태여부 가져오기
            data = msg.getData();
            status = data.getInt(MQTTservice.STATUS);

            // MQTT 상태에 따라 처리
            switch(status) {
                case AppConst.MQTT_Success:
                    queue.clear();
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler / MQTT - Message=" + msg.what + " / Status=" + status);

                case AppConst.MQTT_Failed_Connection:
                    queue.offer(status);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT 연결 실패: " + queue.size());
                    break;

                case AppConst.MQTT_Failed_Topic_Null:
                    queue.offer(status);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT TOPIC 처리 실패: " + queue.size());
                    break;

                case AppConst.MQTT_Failed_Bundle_Null:
                    queue.offer(status);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT Bundle 처리 실패: " + queue.size());
                    break;

                case AppConst.MQTT_Failed_Client_Null:
                    queue.offer(status);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT 클라이언트 연결 실패: " + queue.size());
                    break;

                case AppConst.MQTT_Failed_Data:
                    queue.offer(status);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT DATA 처리 실패: " + queue.size());
                    break;

                default :
                    super.handleMessage(msg);
                    break;
            }

            // 응답 큐가 6개(30초)를 넘기는 경우
            if(queue.size() > AppConst.Queue_GPIO_BaseLine) {
                // 2. MQTT 상태 저장
                pref.put(SharedPrefUtil.MQTT_STATUS, false);

                // 3. 서버에 연결하지 못하였습니다. 네트워크를 확인하거나 잠시 후 다시 시도하십시오.
                ToastUtils.ToastShow(getActivity(), R.string.msg_mqtt_fetch_error);
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "Main/GroupActivity - ServiceHandler() / 네트워크 연결 실패!!");
            } else {
                // 1. MQTT 상태 저장
                pref.put(SharedPrefUtil.MQTT_STATUS, true);
            }
        }
    }
}

