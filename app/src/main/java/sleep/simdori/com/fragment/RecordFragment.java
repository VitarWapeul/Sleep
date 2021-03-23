package sleep.simdori.com.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.anychart.charts.Pie;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import sleep.simdori.com.R;
import sleep.simdori.com.activity.HomeActivity;
import sleep.simdori.com.asynctask.Get_SleepDailyAsynctask;
import sleep.simdori.com.util.SharedPrefUtil;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    private LineChart lineChart;
    private PieChart pieChart;

    // API
    private SharedPrefUtil pref = null;
    private Get_SleepDailyAsynctask mAsyncTask_getSleepDaily = null;

    int[] colorArray = new int[] {Color.parseColor("#7030A0"), Color.parseColor("#53595E")};

    // Values
    String userName;
    String device_mac;
    String bedtime;
    String wakeUpTime;
    String totalSleepTime;
    String remSleepTime;
    String normalSleepTime;
    String deepSleepTime;
    String awlSleepTime;
    String sleepElement;
    String sleepDiagnosis;
    String sleepQuality;
    String sleepDate;
    String date;
    String stageData;
    String[] stage;

    String sleepDataStr;
    String[] sleepData;


    // View
    TextView bedtimeTextView;
    TextView wakeUpTimeTextView;
    TextView totalSleepTimeTextView;
    TextView dateTextView;
    TextView userNameTextView;
    TextView remSleepTextView;
    TextView normalSleepTextView;
    TextView deepSleepTextView;
    TextView awlSleepTextView;
    TextView elementTextView;
    TextView diagnosisTextView;
    Button gotoMonitoringStatisticsButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    public RecordFragment() {
        // Required empty public constructor
    }

    public static sleep.simdori.com.fragment.RecordFragment newInstance() {
        return new sleep.simdori.com.fragment.RecordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = new SharedPrefUtil(getActivity());
        // Values
        userName = pref.getValue(SharedPrefUtil.USER_ID, "");
        device_mac = pref.getValue(SharedPrefUtil.DEVICE_MAC, "");
        date = pref.getValue(SharedPrefUtil.DATE, "");
        System.out.println("RecordFragment_device_mac : "+device_mac);
        if(device_mac==""){
            Toast.makeText(getContext(),"등록된 기기가 없습니다.",Toast.LENGTH_LONG).show();
            ((HomeActivity)getActivity()).replaceFragment(HomeFragment.newInstance());
        }

        getSleepDaily(userName, device_mac, date);
        sleepDataStr = pref.getValue(SharedPrefUtil.SLEEP, "");
        System.out.println("sleepDataStr : "+ sleepDataStr);
        sleepData = sleepDataStr.split(",");
        System.out.println("sleepData : "+ sleepData[0] );

        if(sleepData.length >= 11){
            //DB에서 가져온 데이터 보기 쉽게 처리
            bedtime = sleepData[0].replace("[","").replace("\"","");
            wakeUpTime = sleepData[1].replace("\"","");
            totalSleepTime = sleepData[2].replace("\"","");
            remSleepTime = sleepData[3].replace("\"","");
            normalSleepTime = sleepData[4].replace("\"","");
            deepSleepTime = sleepData[5].replace("\"","");
            awlSleepTime = sleepData[6].replace("\"","");
            sleepElement = sleepData[7].replace("\"","");
            sleepDiagnosis = sleepData[8].replace("\"","");
            sleepQuality = sleepData[9].replace("\"","");
            stageData = sleepData[10].replace("\"","")
                    .replace("(", "[")
                    .replace(")", "]")
                    .replace(".", ",");
            stage = stageData.split(",");
            sleepDate = sleepData[11].replace("\"","").substring(0, 10);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_record, container, false);

        bedtimeTextView = (TextView) v.findViewById(R.id.sleepTrackingResultStartSleepTime);
        wakeUpTimeTextView = (TextView) v.findViewById(R.id.sleepTrackingResultAwakeTime);
        totalSleepTimeTextView = (TextView) v.findViewById(R.id.sleepTrackingResultSleepingTime);
        dateTextView = (TextView) v.findViewById(R.id.sleepTrackingResultDate);
        userNameTextView = (TextView) v.findViewById(R.id.recordusername);
        remSleepTextView = (TextView) v.findViewById(R.id.resultRamSleepTimeValue);
        normalSleepTextView = (TextView) v.findViewById(R.id.resultSleepTimeValue);
        deepSleepTextView = (TextView) v.findViewById(R.id.resultDeepSleepTimeValue);
        awlSleepTextView = (TextView) v.findViewById(R.id.resultAwakeTimeValue);
        elementTextView = (TextView) v.findViewById(R.id.sleepElement);
        diagnosisTextView = (TextView) v.findViewById(R.id.sleepDiagnosisResult);
        gotoMonitoringStatisticsButton = (Button) v.findViewById(R.id.gotoMonitoringStatisticsButton);
        gotoMonitoringStatisticsButton.setOnClickListener(this);

        bedtimeTextView.setText("취침 " + bedtime);
        wakeUpTimeTextView.setText("기상 " + wakeUpTime);
        totalSleepTimeTextView.setText("총 수면시간\n" + totalSleepTime);
        dateTextView.setText(sleepDate);
        userNameTextView.setText(userName);
        remSleepTextView.setText("■" + remSleepTime);
        normalSleepTextView.setText("■" + normalSleepTime);
        deepSleepTextView.setText("■" + deepSleepTime);
        awlSleepTextView.setText("■" + awlSleepTime);
        elementTextView.setText(sleepElement);
        diagnosisTextView.setText(sleepDiagnosis);
        if(sleepDataStr.length()==4) {//가져온 수면데이터가 없으면
            Toast.makeText(getContext(),"측정된 수면데이터가 없습니다.",Toast.LENGTH_LONG);
        }else{
            //수면 질 차트
            pieChart = v.findViewById(R.id.pieChart);

            PieDataSet pieDataSet = new PieDataSet(sleepTimeData(), "");
            pieDataSet.setColors(colorArray);
            pieDataSet.setValueTextColor(Color.parseColor("#00ff0000"));
            PieData pieData = new PieData(pieDataSet);


            if (sleepQuality.equals("3")) {
                pieChart.setCenterText("PERFECT\nSleep");
            } else if (sleepQuality.equals("2")) {
                pieChart.setCenterText("GOOD\nSleep");
            } else if (sleepQuality.equals("1")) {
                pieChart.setCenterText("BAD\nSleep");
            }

            pieChart.setCenterTextSize(18);
            pieChart.setCenterTextColor(Color.WHITE);
            pieChart.setHoleRadius(75);
            pieChart.setData(pieData);
            pieChart.setHoleColor(Color.parseColor("#00ff0000"));
            pieChart.setDrawEntryLabels(false);

            //수면 분석 차트
            lineChart = v.findViewById(R.id.lineChart);

            lineChart.setPinchZoom(false);
            lineChart.setBackgroundColor(Color.parseColor("#00ff0000"));
            lineChart.setDrawGridBackground(false);

            LineDataSet lineDataSet = new LineDataSet(sleepAnalysisData(stage), "");
            lineDataSet.setDrawIcons(false);

            ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
            lineDataSets.add(lineDataSet);
            LineData lineData = new LineData(lineDataSets);

            lineChart.setData(lineData);

            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setDrawFilled(false);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setHighLightColor(Color.WHITE);
            lineDataSet.setColor(Color.WHITE);
            lineDataSet.setFillColor(Color.WHITE);
            lineDataSet.setFillAlpha(100);
            lineDataSet.setDrawHorizontalHighlightIndicator(false);


            lineData.setDrawValues(false);

            lineChart.setViewPortOffsets(0, 0, 0, 0);

            lineChart.getDescription().setEnabled(false);

            lineChart.invalidate();
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.gotoMonitoringStatisticsButton:
            {
                ((HomeActivity)getActivity()).replaceFragment(MonitoringStatisticsFragment.newInstance());
                break;
            }
        }
    }


    private ArrayList<PieEntry> sleepTimeData(){
        ArrayList<PieEntry> dataValue = new ArrayList<>();
        int deep;
        int total;

        if(deepSleepTime.length() == 6){//수면시간이 10시간 미만이면
            deep = Integer.parseInt(deepSleepTime.substring(0,1)) * 60 + Integer.parseInt(deepSleepTime.substring(3,5));
        }else{//수면시간이 10시간 이상이면
            deep = Integer.parseInt(deepSleepTime.substring(0,2)) * 60 + Integer.parseInt(deepSleepTime.substring(4,6));
        }
        if(totalSleepTime.length() == 6){
            total = Integer.parseInt(totalSleepTime.substring(0,1)) * 60 + Integer.parseInt(totalSleepTime.substring(3,5));
        }else{
            total = Integer.parseInt(totalSleepTime.substring(0,2)) * 60 + Integer.parseInt(totalSleepTime.substring(4,6));
        }

        dataValue.add(new PieEntry(Float.valueOf(deep)));
        dataValue.add(new PieEntry(Float.valueOf(total)));

        return dataValue;
    }

    private ArrayList<Entry> sleepAnalysisData(String[] stage){
        ArrayList<Entry> dataValue = new ArrayList<>();

        for(int i = 0; i < stage.length; i++){
            dataValue.add(new Entry(i, Float.parseFloat(stage[i])));
        }

        return dataValue;
    }

    public void getSleepDaily(String id, String device_mac, String date){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mAsyncTask_getSleepDaily = new Get_SleepDailyAsynctask(getContext(), id, device_mac, date);
        } else {
            mAsyncTask_getSleepDaily = new Get_SleepDailyAsynctask(getContext(), id, device_mac, date);
        }
        // db에서 데이터를 가져오는 순간 값을 가져오는 딜레이 시간이 필요
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
//    public void getSleepDaily(String id, String device_mac, String date){
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//            mAsyncTask_getSleepDaily = new Get_SleepDailyAsynctask(getContext(), id, device_mac, date).execute();
//        } else {
//            mAsyncTask_getSleepDaily = new Get_SleepDailyAsynctask(getContext(), id, device_mac, date).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
//        // db에서 데이터를 가져오는 순간 값을 가져오는 딜레이 시간이 필요
//        try {
//            Thread.sleep(50);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

}