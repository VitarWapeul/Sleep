package sleep.simdori.com.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.Math.Fft;
import sleep.simdori.com.R;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import sleep.simdori.com.activity.HomeActivity;
import sleep.simdori.com.asynctask.Set_BPMAsyncTask;
import sleep.simdori.com.mqtt.MQTTservice;
import sleep.simdori.com.util.DatabaseHandler;
import sleep.simdori.com.util.ImageProcessing;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;


public class MeasureFragment extends Fragment implements View.OnClickListener {

    // MQTT
    private IntentFilter intentFilter 		= null;
    private Messenger service = null;
    private final Messenger serviceHandler = new Messenger(new ServiceHandler());
    private boolean mBound = false;
    private Message msg = null;
    private Bundle data = null;
    private int status = 0, mid_device = 0 ;

    // API
    private SharedPrefUtil pref = null;
    private LinkedList<Integer> queue = null;

    // Views
    private Activity mActivity;
    private ProgressBar pb;
    private AsyncTask<String, Void, Integer> mAsyncTask_InsertBPM = null;


    // Variables Initialization
    private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean processing = new AtomicBoolean(false);
    private SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static PowerManager.WakeLock wakeLock = null;

    // View
    TextView userName;
    Button backButton;

    GraphView graph;

    // Time
    TimeZone koreaTimeZone;
    Date date;
    DateFormat dateFormat;

    // Values
    // lns 20200303
//    String userNameValue = "vitarbest";
//    String id = "vitarbest";
    // lns 20200303수정
    String userNameValue;
    String id;
    private String topic, message, userTopic, deviceTopic = null;

    //Toast
    private Toast mainToast;

    //Beats variable
    public int Beats = 0;
    public double bufferAvgB = 0;

    // SPO2 variables
    private static final double RedBlueRatio = 0;
    double Stdr = 0;
    double Stdb = 0;
    double sumred = 0;
    double sumblue = 0;
    public int o2;

    //DataBase
    DatabaseHandler helper;
    SQLiteDatabase db;
    String sql;

    //ProgressBar
    private ProgressBar ProgHeart;
    public int ProgP = 0;
    public int inc = 0;
    private TextView tvProgressCircle = null;

    //Freq + timer variable
    private static long startTime = 0;
    private double SamplingFreq;

    //Arraylist
    public ArrayList<Double> GreenAvgList = new ArrayList<Double>();
    public ArrayList<Double> RedAvgList = new ArrayList<Double>();
    public ArrayList<Double> BlueAvgList = new ArrayList<Double>();
    public ArrayList<Double> timestamp = new ArrayList<Double>();
    public String green;
    public String red;
    public int counter = 0;


    public static sleep.simdori.com.fragment.MeasureFragment newInstance() {
        return new sleep.simdori.com.fragment.MeasureFragment();
    }

    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    public boolean isRunning = true;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            service = new Messenger(binder);
            System.out.println("바운드");
            mBound = true;

            msg = Message.obtain(null, MQTTservice.REGISTER);
            try {
                service.send(msg);
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - MQTT ServiceConnection onServiceConnected()");
            } catch (RemoteException e) {
                // MQTT 토픽을 처리하지 못하였습니다.
                e.printStackTrace();
                ToastUtils.ToastShow(mActivity, getString(R.string.msg_mqtt_fetch_error));
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

    /**
     * MQTT 서비스에 연결하여, ServiceConnection를 전달한다.
     * @see Activity
     */
    @Override
    public void onStart() {
        super.onStart();
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


    @SuppressLint("InvalidWakeLockTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_measure, container, false);
        mActivity = getActivity();

        // API
        queue = new LinkedList<Integer>();
        pref = new SharedPrefUtil(mActivity);
        userNameValue = pref.getValue(SharedPrefUtil.USER_ID, "");
        id = pref.getValue(SharedPrefUtil.USER_ID,"");// lns 20200303추가

        userName = (TextView) v.findViewById(R.id.measureusername);
        backButton = (Button) v.findViewById(R.id.measurebackbutton);
        backButton.setOnClickListener(this);

        // 진행바
        pb = (ProgressBar) v.findViewById(R.id.pb);
        userName.setText(userNameValue);


//        //추후 google, naver, kakao login 추가시 상황에 따라 유저 이름 가져와 보여주기
//        if(GoogleSignIn.getLastSignedInAccount(getActivity()) != null){
//            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
//            if (acct != null) {
//                String personName = acct.getDisplayName();
//
//                userName.setText(personName);
//            }
//        }else if(OAuthLoginState.OK.equals(OAuthLogin.getInstance().getState(getActivity()))){
//            //회원정보 가져오기
//        }else{
//            //sqlite에서 정보 가져와서 보여주기
//        }

        // XML - Java Connecting
        preview = (SurfaceView) v.findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        ProgHeart = (ProgressBar) v.findViewById(R.id.HRPB);
        ProgHeart.setProgress(0);
        tvProgressCircle = (TextView) v.findViewById(R.id.tv_progress_circle);

        // WakeLock Initialization : Forces the phone to stay On
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");


        /*LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(new Date().getTime(), bpm)
        });
        graph.addSeries(series);
        series.setColor(Color.RED);*/

        /* Real-time Graph */
        // Using the GGraphView Library
        graph = (GraphView) v.findViewById(R.id.graph);
        graph.setBackgroundColor(Color.parseColor("#aaffffff"));

        //Create the Datapoints
        series = new LineGraphSeries<DataPoint>();
        series.setColor(Color.RED);
        graph.addSeries(series);

        // customize the viewport
        Viewport viewport = graph.getViewport();
//        viewport.setYAxisBoundsManual(true);
//        viewport.setMinY(0);
//        viewport.setMaxY(60);
        viewport.setScrollable(true);
        viewport.setMinX(0);

//      viewport.setXAxisBoundsManual(true);
//        viewport.setMinX(0);
//        viewport.setMaxX(5);

        ImageButton imgprofileButton = v.findViewById(R.id.imgprofilebtn);

        imgprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).replaceFragment(SettingFragment.newInstance());
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        startTime = System.currentTimeMillis();


        /* 그래프 그리기 시작 */
        isRunning = true;

        /*// Create a thread to run the graph
        new Thread(() -> {

            while (isRunning == true) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("뜨레드에 들어와벌임");
                        addEntry();
                    }
                });
                // using sleep to slow down the addition of new points
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.print(e.getMessage());
                }
            }
        }).start();*/
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;

        isRunning = false;
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
        insertBPM(id, green, red, String.valueOf(Beats));
    }


    /* Heart Rate 측정*/
    //Prevent the system from restarting your activity during certain configuration changes,
    // but receive a callback when the configurations do change, so that you can manually update your activity as necessary.
    //such as screen orientation, keyboard availability, and language

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //Wakelock + Open device camera + set orientation to 90 degree
    //store system time as a start time for the analyzing process
    //your activity to start interacting with the user.
    // This is a good place to begin animations, open exclusive-access devices (such as the camera)


    //call back the frames then release the camera + wakelock and Initialize the camera to null
    //Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed. The counterpart to onResume().
    //When activity B is launched in front of activity A,
    // this callback will be invoked on A. B will not be created until A's onPause() returns, so be sure to not do anything lengthy here.




    //getting frames data from the camera and start the heartbeat process
    private final Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @SuppressLint("LongLogTag")
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            //if data or size == null ****
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            //Atomically sets the value to the given updated value if the current value == the expected value.
            if (!processing.compareAndSet(false, true)) return;

            //put width + height of the camera inside the variables
            int width = size.width;
            int height = size.height;

            double GreenAvg;
            double RedAvg;
            double BlueAvg;
            GreenAvg = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 3); //1 stands for red intensity, 2 for blue, 3 for green
            RedAvg = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 1); //1 stands for red intensity, 2 for blue, 3 for green
            sumred = sumred + RedAvg;
            BlueAvg = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 2); //1 stands for red intensity, 2 for blue, 3 for green
            sumblue = sumblue + BlueAvg;
            //그래프에 좌표 넣기
            series.appendData(new DataPoint(lastX++,GreenAvg), true, 50);

//            //1초마다 raw 데이터 전송
//            TimerTask mqttSend = new TimerTask() {
//                public void run() {
//                    if(green != null){
//                    }
//                }
//
//            };
//            Timer timer = new Timer();
//            timer.schedule(mqttSend, 0, 1000); // 0초후 첫실행, 3초마다 계속실행

            GreenAvgList.add(GreenAvg);
            RedAvgList.add(RedAvg);
            BlueAvgList.add(BlueAvg);

            //MQTT를 통해 전송
//            publishTopic("simdori/test", "테스트");

            //SQlite 데이터베이스
            helper = new DatabaseHandler(getContext());
            db = helper.getWritableDatabase();
            helper.onCreate(db);


            ++counter; //countes number of frames in 30 seconds


            //To check if we got a good red intensity to process if not return to the condition and set it again until we get a good red intensity
            if (RedAvg < 200) {
                startTime = System.currentTimeMillis();
                inc = 0;
                ProgP = (inc++) * 1 / 4;
                counter = 0;
                ProgHeart.setProgress(ProgP);
                String strProgress = ProgP + " %";
                tvProgressCircle.setText(strProgress);
                processing.set(false);
            }
            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d; //to convert time to seconds
            Log.d("chcho currentTimeMillis", endTime+"\t"+GreenAvg+"\t"+RedAvg+"\t"+totalTimeInSecs);
            timestamp.add(totalTimeInSecs);
            if (totalTimeInSecs >= 30) { //when 30 seconds of measuring passes do the following " we chose 30 seconds to take half sample since 60 seconds is normally a full sample of the heart beat

                Double[] Green = GreenAvgList.toArray(new Double[GreenAvgList.size()]);
                Double[] Red = RedAvgList.toArray(new Double[RedAvgList.size()]);
                Double[] Blue = BlueAvgList.toArray(new Double[BlueAvgList.size()]);
                Log.d("ppg green", Arrays.toString(Green));
                Log.d("ppg red", Arrays.toString(Red));
                Log.d("ppg blue", Arrays.toString(Blue));
                green = Arrays.toString(Green);
                red = Arrays.toString(Red);
                SamplingFreq = (counter / totalTimeInSecs); //calculating the sampling frequency

                double HRFreq = Fft.FFT(Green, counter, SamplingFreq); // send the green array and get its fft then return the amount of heartrate per second
                double bpm = (int) ceil(HRFreq * 60);
                double HR1Freq = Fft.FFT(Red, counter, SamplingFreq);  // send the red array and get its fft then return the amount of heartrate per second
                double bpm1 = (int) ceil(HR1Freq * 60);
                double meanr = sumred / counter;
                double meanb = sumblue / counter;

                Log.d("chcho green", Arrays.toString(Green));
                List<Double> peaks = PeakDetecion(GreenAvgList, timestamp,15);
//                analyzeDataForSignals(GreenAvgList, timestamp,5, 3.5, 0.3);
//                Log.d("chcho red", Arrays.toString(Red));
//                PeakDetecion(RedAvgList, timestamp,15);
//                analyzeDataForSignals(RedAvgList, timestamp, 5, 3.5, 0.3); // send the green array and get its fft then return the amount of heartrate per second
                Log.d("chcho peaks", peaks.toString());

                for (int i = 0; i < counter - 1; i++) {

                    Double bufferb = Blue[i];

                    Stdb = Stdb + ((bufferb - meanb) * (bufferb - meanb));

                    Double bufferr = Red[i];

                    Stdr = Stdr + ((bufferr - meanr) * (bufferr - meanr));

                }
                // The following code is to make sure that if the heartrate from red and green intensities are reasonable
                // take the average between them, otherwise take the green or red if one of them is good
                double varr = sqrt(Stdr / (counter - 1));
                double varb = sqrt(Stdb / (counter - 1));

                double R = (varr / meanr) / (varb / meanb);

                double spo2 = 100 - 5 * (R);
                o2 = (int) (spo2);
//                if ((o2 < 80 || o2 > 99) || (bpm < 45 || bpm > 200)) {
//                    inc = 0;
//                    ProgP = inc;
//                    ProgO2.setProgress(ProgP);
//                    mainToast = Toast.makeText(getApplicationContext(), "Measurement Failed", Toast.LENGTH_SHORT);
//                    mainToast.show();
//                    startTime = System.currentTimeMillis();
//                    counter = 0;
//                    processing.set(false);
//                    return;
//                }
                if ((bpm > 45 || bpm < 200)) {
                    if ((bpm1 > 45 || bpm1 < 200)) {

                        bufferAvgB = (bpm + bpm1) / 2;
                    } else {
                        bufferAvgB = bpm;
                    }
                } else if ((bpm1 > 45 || bpm1 < 200)) {
                    bufferAvgB = bpm1;
                }
//                if (bufferAvgB < 45 || bufferAvgB > 200) { //if the heart beat wasn't reasonable after all reset the progresspag and restart measuring
//                    inc = 0;
//                    ProgP = inc;
//                    ProgHeart.setProgress(ProgP);
//                    String strProgress = ProgP + " %";
//                    tvProgressCircle.setText(strProgress);
//                    mainToast = Toast.makeText(getActivity().getApplicationContext(), "Measurement Failed", Toast.LENGTH_SHORT);
//                    mainToast.show();
//                    startTime = System.currentTimeMillis();
//                    counter = 0;
//                    processing.set(false);
//                    return;
//                }

                o2 = (int) (spo2);
                System.out.println("o2 : "+o2);
                Beats = (int) bufferAvgB;
            }

            ProgHeart.setProgress(100);
            String strProgress = "100 %";
            tvProgressCircle.setText(strProgress);

            if (Beats != 0 && o2 != 0) { //if beasts were reasonable stop the loop and send HR with the username to results activity and finish this activity


//                ((HomeActivity) getActivity()).replaceFragment(ResultFragment.newInstance());

                Bundle bundle = new Bundle();
                bundle.putInt("bpm", Beats); // Put anything what you want
                bundle.putInt("o2",o2);
                ResultFragment resultFragment = new ResultFragment();
                resultFragment.setArguments(bundle);

                //데이터베이스에 시간 저장
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul");
                dateFormat.setTimeZone(koreaTimeZone);

                date = new Date();

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragment, resultFragment)
                        .commit();

                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "출력할 문자열", Toast.LENGTH_SHORT);
                toast.show();

                toast.cancel();
            }

            if (RedAvg != 0) { //increment the progressbar
                ProgP = (int) (totalTimeInSecs/30*100);
                ProgHeart.setProgress(ProgP);
                strProgress = ProgP + " %";
                tvProgressCircle.setText(strProgress);
            }

            //db.close();

            //keeps taking frames tell 30 seconds
            processing.set(false);
        }
    };

    private List<Double> PeakDetecion(List<Double> HeartWaveform, List<Double> timestamp, int lag) {
        List<Double> peaks = new ArrayList<Double>();
        List<Double> rol_mean = new ArrayList<Double>(Collections.nCopies(HeartWaveform.size(), 0.0d));

        // 이동평균 구하기
        Double sum = 0.0;
        for (int i=0; i<lag; i++) {
            sum = sum + HeartWaveform.get(i);
        }
        for (int i=0; i<lag; i++) {
            rol_mean.set(i, sum / lag);
        }

        for (int i=lag; i<HeartWaveform.size()-lag; i++) {
            sum = 0.0;
            for (int j=i-lag; j<i+lag; j++) {
                sum = sum + HeartWaveform.get(j);
            }
            rol_mean.set(i, sum / (lag * 2));
        }

        sum = 0.0;
        for (int i=HeartWaveform.size()-lag; i<HeartWaveform.size(); i++) {
            sum = sum + HeartWaveform.get(i);
        }
        for (int i=HeartWaveform.size()-lag; i<HeartWaveform.size(); i++) {
            rol_mean.set(i, sum / lag);
        }

        Double peak_x = 0.0;
        Double peak_y = 0.0;
        boolean peak_gruop = false;
        for (int i=0; i<HeartWaveform.size(); i++) {
            if (HeartWaveform.get(i) > rol_mean.get(i)) {
                peak_gruop = true;
                if (HeartWaveform.get(i) > peak_y) {
                    peak_x = timestamp.get(i);
                    peak_y = HeartWaveform.get(i);
                }
            } else {
                if (peak_gruop) {
                    peaks.add(peak_x);
                }
                peak_gruop = false;
                peak_x = 0.0;
                peak_y = 0.0;
            }
        }
        if (peak_gruop) {
            peaks.add(peak_x);
        }
        Log.d("chcho PeakDetecion", peaks+"");
        return peaks;
    }

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @SuppressLint("LongLogTag")
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }

            camera.setParameters(parameters);
            camera.startPreview();
        }


        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) result = size;
                }
            }
        }
        return result;
    }

    /**
     * BPM 데이터 삽입
     * @param id	: 사용자 아이디
     * @param green : green 배열
     * @param red	: red 배열
     * @param BPM	: BPM
     */
    public void insertBPM(String id, String green, String red, String BPM) {
        if (Build.VERSION.SDK_INT < AppConst.HONEYCOMB) {
            mAsyncTask_InsertBPM = new Set_BPMAsyncTask(mActivity, pb, id, green, red, BPM).execute();
        } else {
            mAsyncTask_InsertBPM = new Set_BPMAsyncTask(mActivity, pb, id, green, red, BPM).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    /**
     * MQTT 서버에 연결하여 Topic을 발행한다.
     * @param 	topic		: 발행할 토픽
     * @param	message 	: 발행할 메시지
     * @return 	None
     */
    public void publishTopic(String topic, String message) {
        if (!topic.isEmpty() && !message.isEmpty()) {
            data = new Bundle();
            data.putCharSequence(MQTTservice.TOPIC, topic);
            data.putCharSequence(MQTTservice.MESSAGE, message);

            msg = Message.obtain(null, MQTTservice.PUBLISH);
            msg.setData(data);
            try {
                service.send(msg);
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - PublishTopic() / MQTT Publish Attemping...");
            } catch (RemoteException e) {
                e.printStackTrace();
                ToastUtils.ToastShow(mActivity, getString(R.string.msg_mqtt_fetch_error));
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - PublishTopic() / MQTT Publish Failed with exception:" + e.getMessage());
            }
        } else {
            // MQTT 토픽을 처리하지 못하였습니다.
            ToastUtils.ToastShow(mActivity, getString(R.string.msg_mqtt_fetch_error));
            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - PublishTopic() / MQTT Topic and Message Required.");
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.measurebackbutton:
            {
                ((HomeActivity)getActivity()).replaceFragment(HomeFragment.newInstance());
                break;
            }
        }
    }

    /**
     *  바인드된 서비스에 메시지를 전달한다.
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
                ToastUtils.ToastShow(mActivity, R.string.msg_mqtt_fetch_error);
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / 네트워크 연결 실패!!");
            } else {
                // 1. MQTT 상태 저장
                pref.put(SharedPrefUtil.MQTT_STATUS, true);
            }
        }
    }


}

