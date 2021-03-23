package sleep.simdori.com.fragment;

import java.util.ArrayList;
import java.util.Calendar;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.OrientationHelper;


import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.List;

import sleep.simdori.com.R;
import sleep.simdori.com.activity.HomeActivity;
import sleep.simdori.com.asynctask.Get_SleepDays;
import sleep.simdori.com.util.SharedPrefUtil;

public class MonitoringStatisticsFragment extends Fragment implements View.OnClickListener {

    // API
    private SharedPrefUtil pref = null;
    private AsyncTask<String, Void, Integer> mAsyncTask_getSleepDays = null;

    // View
    Button backButton;
    Button clearButton;
    Button showButton;
    TextView userName;
    TextView monitoringStatisticsPeriod;

    // Chart
    private LineChart qualityLineChart;
    private LineChart startTimeLineChart;
    private BarChart totalSleepTimeBarChart;

    // Values
    String userNameValue;
    CalendarView calendarView;
    String device_mac;
    String selected_period;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = new SharedPrefUtil(getActivity());
        userNameValue = pref.getValue(SharedPrefUtil.USER_ID, "");
        device_mac = pref.getValue(SharedPrefUtil.DEVICE_MAC, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_monitoring_statistics, container, false);

        userName = (TextView) v.findViewById(R.id.monitoringStatisticsusername);
        userName.setText(userNameValue);
        backButton = (Button) v.findViewById(R.id.monitoringStatisticsBackButton);
        backButton.setOnClickListener(this);
        showButton = (Button) v.findViewById(R.id.show_selections);
        showButton.setOnClickListener(this);
        clearButton = (Button) v.findViewById(R.id.clear_selections);
        clearButton.setOnClickListener(this);
        monitoringStatisticsPeriod = v.findViewById(R.id.monitoringStatisticsPeriod);
        qualityLineChart = v.findViewById(R.id.sleepQualityChart);
        startTimeLineChart = v.findViewById(R.id.sleepStartTimeChart);
        totalSleepTimeBarChart = v.findViewById(R.id.totalSleepTimeChart);

        calendarView = (CalendarView) v.findViewById(R.id.calendar_view);
        calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);
        calendarView.setSelectionType(SelectionType.RANGE);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.monitoringStatisticsBackButton:
            {
                ((HomeActivity)getActivity()).replaceFragment(RecordFragment.newInstance());
                break;
            }
            case R.id.clear_selections:
                clearSelectionsMenuClick();
                break;

            case R.id.show_selections:
                List<Calendar> days = calendarView.getSelectedDates();
                System.out.println( "calendarView.getSelectedDates()" + days);
                String result="";
                //i = 0 부터 i = days.size() - 1 까지
                Calendar calendar = days.get(0);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                if(month.length() == 1){//1-9월 이면 앞에 0을 붙임 ex. 01월
                    month = "0" + month;
                }
                int year = calendar.get(Calendar.YEAR);
                String startDate = year + "-"+ (month)  + "-" + day;

                calendar = days.get(days.size() - 1);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                if(month.length() == 1){
                    month = "0" + month;
                }
                year = calendar.get(Calendar.YEAR);
                String endDate = year + "-"+ (month)  + "-" + day;

                Toast.makeText(getActivity(), startDate + " ~ " + endDate, Toast.LENGTH_LONG).show();
                monitoringStatisticsPeriod.setText(startDate+" ~ " +endDate+" 통계");
                getSleepDays(userNameValue, device_mac, startDate, endDate);

                drawGraph();

                break;
        }
    }



    public static sleep.simdori.com.fragment.MonitoringStatisticsFragment newInstance() {
        return new sleep.simdori.com.fragment.MonitoringStatisticsFragment();
    }


    private void clearSelectionsMenuClick() {
        calendarView.clearSelections();

    }

    public void getSleepDays(String id, String device_mac, String startDate, String endDate){
        pref.put(SharedPrefUtil.SLEEPDAYS, "");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mAsyncTask_getSleepDays = new Get_SleepDays(getContext(), id, device_mac, startDate, endDate).execute();
        } else {
            mAsyncTask_getSleepDays = new Get_SleepDays(getContext(), id, device_mac, startDate, endDate).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        // db에서 데이터를 가져오는 순간 값을 가져오는 딜레이 시간이 필요
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void drawGraph(){
        String sleepDays = pref.getValue(SharedPrefUtil.SLEEPDAYS, "");
        sleepDays = sleepDays.replace("\"", "");
        System.out.println("sleepDays : "+sleepDays);
        String[] daysData = sleepDays.split(",");
        daysData[0] = daysData[0].replace("[", "");
        daysData[daysData.length - 1] = daysData[daysData.length - 1].replace("]", "");

        //수면질 분석 차트
        qualityLineChart.setPinchZoom(false);
        qualityLineChart.setBackgroundColor(Color.parseColor("#00ff0000"));
        qualityLineChart.setDrawGridBackground(false);

        LineDataSet qualityLineDataSet = new LineDataSet(sleepQualityDaysData(daysData), "");
        qualityLineDataSet.setDrawIcons(false);

        ArrayList<ILineDataSet> qualityLineDataSets = new ArrayList<>();
        qualityLineDataSets.add(qualityLineDataSet);
        LineData qualityLineData = new LineData(qualityLineDataSets);

        qualityLineChart.setData(qualityLineData);

        qualityLineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        qualityLineDataSet.setDrawCircles(false);
        qualityLineDataSet.setHighLightColor(Color.WHITE);
        qualityLineDataSet.setColor(Color.WHITE);
        qualityLineDataSet.setFillColor(Color.WHITE);
        qualityLineDataSet.setFillAlpha(100);
        qualityLineDataSet.setDrawFilled(false);
        qualityLineDataSet.setDrawHorizontalHighlightIndicator(false);

        qualityLineData.setDrawValues(true);

        qualityLineChart.setViewPortOffsets(0, 0, 0, 0);

        qualityLineChart.setVisibleYRangeMaximum(5, YAxis.AxisDependency.LEFT);
        qualityLineChart.setVisibleYRangeMaximum(-1, YAxis.AxisDependency.LEFT);

        qualityLineChart.getDescription().setEnabled(false);

        qualityLineChart.invalidate();


        //수면 시작 시간 분석 차트
        startTimeLineChart.setPinchZoom(false);
        startTimeLineChart.setBackgroundColor(Color.parseColor("#00ff0000"));
        startTimeLineChart.setDrawGridBackground(false);

        LineDataSet startTimeLineDataSet = new LineDataSet(sleepStartTimeDaysData(daysData), "");
        startTimeLineDataSet.setDrawIcons(false);

        ArrayList<ILineDataSet> startTimeLineDataSets = new ArrayList<>();
        startTimeLineDataSets.add(startTimeLineDataSet);
        LineData startTimeLineData = new LineData(startTimeLineDataSets);

        startTimeLineChart.setData(startTimeLineData);

        startTimeLineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        startTimeLineDataSet.setDrawCircles(false);
        startTimeLineDataSet.setHighLightColor(Color.WHITE);
        startTimeLineDataSet.setColor(Color.WHITE);
        startTimeLineDataSet.setFillColor(Color.WHITE);
        startTimeLineDataSet.setFillAlpha(100);
        startTimeLineDataSet.setDrawFilled(false);
        startTimeLineDataSet.setDrawHorizontalHighlightIndicator(false);

        startTimeLineData.setDrawValues(true);

        startTimeLineChart.setViewPortOffsets(0, 0, 0, 0);

        startTimeLineChart.setVisibleYRangeMaximum(5, YAxis.AxisDependency.LEFT);
        startTimeLineChart.setVisibleYRangeMaximum(-1, YAxis.AxisDependency.LEFT);

        startTimeLineChart.getDescription().setEnabled(false);

        startTimeLineChart.invalidate();


        //총 수면 시간 분석 차트

        BarDataSet depenses = new BarDataSet (totalSleepTimeDaysData(daysData), ""); // 변수로 받아서 넣어줘도 됨
        depenses.setAxisDependency(YAxis.AxisDependency.LEFT);

//        ArrayList<String> labels = new ArrayList<String>();
//        for(int i=0; i < labelList.size(); i++){
//            labels.add((String) labelList.get(i));
//        }

        BarData data = new BarData(depenses); // 라이브러리 v3.x 사용하면 에러 발생함
        depenses.setColors(ColorTemplate.COLORFUL_COLORS); //

        totalSleepTimeBarChart.setData(data);
        totalSleepTimeBarChart.animateXY(1000,1000);
        totalSleepTimeBarChart.invalidate();

    }

    private ArrayList<Entry> sleepQualityDaysData(String[] daysData){
        ArrayList<Entry> qualityDataValue = new ArrayList<>();

        if(!(daysData.length < 4)){
            for(int i = 0; i < daysData.length; i = i + 4){

                float qualityData = Float.parseFloat(daysData[i + 3].substring(5,10).replace("-", "."));
                qualityDataValue.add(new Entry(qualityData, Float.parseFloat(daysData[i])));

            }
        }
        return qualityDataValue;
    }

    private ArrayList<Entry> sleepStartTimeDaysData(String[] daysData){
        ArrayList<Entry> startTimeDataValue = new ArrayList<>();

        if(!(daysData.length < 4)){
            for(int i = 0; i < daysData.length; i = i + 4){
                // 수면 시작시간 순서 맞게 수정 및 보여주는 데이터 형식 변환 필요
                float qualityData = Float.parseFloat(daysData[i + 3].substring(5,10).replace("-", "."));
                startTimeDataValue.add(new Entry(qualityData, Float.parseFloat(daysData[i])));

            }
        }
        return startTimeDataValue;
    }

    private ArrayList<BarEntry> totalSleepTimeDaysData(String[] daysData){
        ArrayList<BarEntry> totalSleepTimeDataValue = new ArrayList<>();

        if(!(daysData.length < 4)){
            for(int i = 0; i < daysData.length; i = i + 4){
                // 총 수면 시간 순서 맞게 수정 및 보여주는 데이터 형식 변환 필요
                float qualityData = Float.parseFloat(daysData[i + 3].substring(5,10).replace("-", "."));
                totalSleepTimeDataValue.add(new BarEntry(qualityData, Float.parseFloat(daysData[i])));

            }
        }else{
            Toast.makeText(getContext(),"측정된 수면데이터가 없습니다.",Toast.LENGTH_LONG);
        }
        return totalSleepTimeDataValue;
    }

}




