package sleep.simdori.com.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.R;
import sleep.simdori.com.activity.HomeActivity;
import sleep.simdori.com.dialog.CustomDialog_State;
import sleep.simdori.com.mqtt.MQTTservice;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, SensorEventListener {

    TimeZone koreaTimeZone;
    Date date = new Date();
    Date yesterday = new Date(date.getTime()+(1000*60*60*24*-1));
    DateFormat todaydateFormat;
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
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private Sensor stepCountSensor;
    private int mStepDetector;
    private int mStepCountor;
    int step=0;

    public void setTextCountWalk( int step) {
        countWalk.setText("걸음 수 : " + step);
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
        System.out.println("HomeFragment - onCreate호출");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

        // API
        customDialog_state = new CustomDialog_State(getActivity());
        queue = new LinkedList<Integer>();
        pref = new SharedPrefUtil(getContext());
        device_mac = pref.getValue(SharedPrefUtil.DEVICE_MAC, "");

        todaydateFormat = new SimpleDateFormat("yyyy-MM-dd");
        koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul");
        todaydateFormat.setTimeZone(koreaTimeZone);
        date = new Date();
//        sqlDB = dbhandler.getReadableDatabase();
//        dbhandler.onUpgrade(sqlDB,1,2);
//        sqlDB.close();
        System.out.println("HomeFragment - onCreate_dateFormat.format(date) : " + todaydateFormat.format(date));
        pref.put(SharedPrefUtil.DATE, todaydateFormat.format(date));

//센서리스너등록
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("HomeFragment - onCreateView호출");

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

        setTextCountWalk(step);

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        int y_step;
        System.out.println("WalkCountService - onSensorChanged호출");
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0f) {
                mStepDetector += event.values[0];
                Log.e("스텝 디텍터", "" + event.values[0]);
            }
        }
        else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            mStepCountor = (int) event.values[0];
            try {
                y_step = pref.getValue("step"+(String)todaydateFormat.format(yesterday),-1);
                pref.put("step"+todaydateFormat.format(date), mStepCountor);
                System.out.println("HomeFragment_onSensorChanged - yesterday : "+ todaydateFormat.format(yesterday));
                System.out.println("HomeFragment_onSensorChanged - date : "+ todaydateFormat.format(date));
                System.out.println("HomeFragment_onSensorChanged - pref.getValue : "+ pref.getValue("step"+dateFormat.format(date),-1));
                System.out.println("HomeFragment_onSensorChanged - y_step : "+ y_step);
                System.out.println("HomeFragment_onSensorChanged - mStepCountor : "+ mStepCountor);
                System.out.println("HomeFragment_onSensorChanged - pref.getValue(SharedPrefUtil.DATE, -1) : "+ pref.getValue(SharedPrefUtil.DATE, -1));

                if(y_step > mStepCountor && y_step!= -1){
                    step = mStepCountor;
                    setTextCountWalk(step);
                }else if(y_step<=mStepCountor && y_step != -1){
                    step = mStepCountor-y_step;
                    setTextCountWalk(step);
                }else{
                    step = mStepCountor;
                    setTextCountWalk(step);
                }
            }catch (Exception e){

            }
            pref.put("step"+dateFormat.format(date),mStepCountor);
            Log.e("스텝 카운트", "" + event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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

