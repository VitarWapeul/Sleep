package sleep.simdori.com.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import sleep.simdori.com.R;
import sleep.simdori.com.activity.HomeActivity;
import sleep.simdori.com.asynctask.Get_BPMDetailAsynctask;
import sleep.simdori.com.util.SharedPrefUtil;

public class BPMDetailFragment extends Fragment implements View.OnClickListener {

    // API
    private SharedPrefUtil pref = null;
    private AsyncTask<String, Void, Integer> mAsyncTask_SelectBPMDetail = null;
    private Bundle bundle;
    // View
    Activity mActivity;

    TextView date;
    TextView topUserName;
    TextView userName;
    TextView userState;
    TextView bpm;
    TextView stress;
    TextView Arrhythmia;
    TextView tachycardia;
    TextView bloodoxygen;
    TextView diagnosis;
    ImageView bradycardia;
    ImageView stateImg;
    Button backButton;

    // Values
    String bpmDetail;
    String[] bpmDetailArr;
    String bpmId;
    String userNameValue;
    String dateValue ;
    String userStateValue ;
    String bloodoxygenValue;
    String ArrhythmiaValue;
    String diagnosisValue;
    int bpmValue;
    String stressValue ;

    @Override
    public void onStart() {
        super.onStart();
        mActivity = getActivity();
        // API
        pref = new SharedPrefUtil(mActivity);
        userNameValue = pref.getValue(SharedPrefUtil.USER_ID, "");
        bpmId = pref.getValue("bpmId", "");

    }

    public static sleep.simdori.com.fragment.BPMDetailFragment newInstance() {
        return new sleep.simdori.com.fragment.BPMDetailFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            bundle = new Bundle();
            bundle=this.getArguments();
            userNameValue = bundle.getString("userId");
            dateValue = bundle.getString("date");
            userStateValue = bundle.getString("state");
            bloodoxygenValue = bundle.getString("blood_oxygen");
            ArrhythmiaValue = bundle.getString("arrhythmia");
            diagnosisValue = bundle.getString("diagnosis");
            bpmValue = Integer.parseInt(bundle.getString("BPM"));
            stressValue  = bundle.getString("stress");
            System.out.println("stressValue  = bundle.getString(\"stress\") : "+stressValue);
        }else{
            System.out.println("getArguments() == null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bpmdetail, container, false);

        mActivity = getActivity();

        date = (TextView) v.findViewById(R.id.bpmdetaildate);
        topUserName = (TextView) v.findViewById(R.id.bpmdetailtopusername);
        userName = (TextView) v.findViewById(R.id.bpmdetailusername);
        userState = (TextView) v.findViewById(R.id.bpmdetailstate);
        bpm = (TextView) v.findViewById(R.id.bpmdetailbpmvalue);
        stress = (TextView) v.findViewById(R.id.bpmstress);
        Arrhythmia = (TextView) v.findViewById(R.id.Arrhythmia);
        tachycardia = (TextView) v.findViewById(R.id.tachycardia);
        bloodoxygen = (TextView) v.findViewById(R.id.bloodoxygen);
        diagnosis = (TextView) v.findViewById(R.id.diagnosis);
        stateImg = (ImageView) v.findViewById(R.id.bpmdetailstateimg);
        bradycardia = (ImageView) v.findViewById(R.id.bradycardia);
        backButton = (Button) v.findViewById(R.id.bpmdetailbackbutton);
        backButton.setOnClickListener(this);
        setText();
        return v;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bpmdetailbackbutton:
            {
                ((HomeActivity)getActivity()).replaceFragment(BPMResultFragment.newInstance());
                break;
            }
        }
    }

    public void setText(){

        mActivity = getActivity();
        // API
//        bundle = new Bundle();
//        bundle=this.getArguments();
//        bpmId=pref.getValue("bpmId","");
//        userNameValue = bundle.getString("userId");
//        dateValue = bundle.getString("date");
//        userStateValue = bundle.getString("state");
//        bloodoxygenValue = bundle.getString("blood_oxygen");
//        ArrhythmiaValue = bundle.getString("arrhythmia");
//        diagnosisValue = bundle.getString("diagnosis");
//        bpmValue = Integer.parseInt(bundle.getString("BPM"));
//        stressValue  = bundle.getString("stress");
// ----------------------------------------------------------------------------------------------
//        bpmDetail = pref.getValue("bpmDetail", "");//bpmDetail이란 key값이 없는데 말이지
//        System.out.println("bpmDetailtest : "+bpmDetail);
//        bpmDetailArr = bpmDetail.split(",");
//        System.out.println("bpmDetailArrtest : "+bpmDetailArr[0]);
//        userNameValue = pref.getValue(SharedPrefUtil.USER_ID, "");
//        bpmValue = Integer.parseInt(bpmDetailArr[0]);
//        stressValue = bpmDetailArr[3];
//        dateValue = bpmDetailArr[1];
//        userStateValue = bpmDetailArr[2];
//        bloodoxygenValue = bpmDetailArr[4];
//        ArrhythmiaValue = bpmDetailArr[5];
//        diagnosisValue = bpmDetailArr[6];

        userName.setText(userNameValue);
        topUserName.setText(userNameValue);
        bpm.setText(String.valueOf(bpmValue));
        date.setText(dateValue);
        userState.setText(userStateValue);
        stress.setText(stressValue);
        bloodoxygen.setText(bloodoxygenValue);
        Arrhythmia.setText(ArrhythmiaValue);
        diagnosis.setText(diagnosisValue);

        // state에 따라서 그림 다르게 해야함
        if(userStateValue.equals("걷기")){
            stateImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.walk));

        }else if(userStateValue.equals("휴식")){

        }else if(userStateValue.equals("이완")){
            stateImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.relaxation_black));
        }else if(userStateValue.equals("뛰기")){

        }else if(userStateValue.equals("과격한 운동")){

        }

        if(bpmValue <= 60){
            bradycardia.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bad));
        }else{
            bradycardia.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.smile));
        }
        if(bpmValue >= 95){
            tachycardia.setText("빈맥");
            tachycardia.setTextColor(Color.RED);
        }else{
            tachycardia.setText("정상");
            tachycardia.setTextColor(Color.GREEN);
        }
        if(stressValue.equals("낮음")){
            stress.setTextColor(Color.GREEN);
        }else{
            stress.setTextColor(Color.RED);
        }
        if(ArrhythmiaValue.equals("정상")){
            Arrhythmia.setTextColor(Color.GREEN);
        }else{
            Arrhythmia.setTextColor(Color.RED);
        }
    }
}
