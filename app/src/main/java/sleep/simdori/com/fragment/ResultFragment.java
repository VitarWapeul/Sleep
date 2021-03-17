package sleep.simdori.com.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.R;
import sleep.simdori.com.activity.HomeActivity;
import sleep.simdori.com.asynctask.Set_BPMAsyncTask;
import sleep.simdori.com.asynctask.Set_BPMDetailAsyncTask;
import sleep.simdori.com.util.SharedPrefUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link sleep.simdori.com.fragment.ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends Fragment implements View.OnClickListener {

    // View
    private Activity mActivity;
    Button finishButton;
    TextView stresstxt;
    TextView oxpercenttxt;
    TextView tachycardiatxt;
    TextView arrhythmiatxt;
    TextView RHR;

    private AsyncTask<String, Void, Integer> mAsyncTask_InsertBPMDetail=null;
    private SharedPrefUtil pref = null;

    // Values
    int HR; // HeartRate
    int SPO2;
    String id;
    String BPM;
    String state;
    String blood_oxygen;
    String stress;
    String arrhythmia;
    String date_time;
    String diagnosis;

    public static sleep.simdori.com.fragment.ResultFragment newInstance() {
        return new sleep.simdori.com.fragment.ResultFragment();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static sleep.simdori.com.fragment.ResultFragment newInstance(String param1, String param2) {
        sleep.simdori.com.fragment.ResultFragment fragment = new sleep.simdori.com.fragment.ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle bundle = this.getArguments();
            HR = bundle.getInt("bpm");
            SPO2 = bundle.getInt("o2");
            BPM = String.valueOf(HR);
        }
        System.out.println("SPO2 : " + SPO2);
        mActivity = getActivity();
        pref = new SharedPrefUtil(mActivity);
    }
//----------------------------------여기에 Set_BPMDetailAsuncTask써서 DB에 집어넣어야함
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_result, container, false);
        RHR = (TextView) v.findViewById(R.id.HRR);
        stresstxt = (TextView) v.findViewById(R.id.stresstxt);
        oxpercenttxt = (TextView) v.findViewById(R.id.oxpercenttxt);
        tachycardiatxt = (TextView) v.findViewById(R.id.tachycardiatxt);
        arrhythmiatxt = (TextView) v.findViewById(R.id.arrhythmiatxt);
        finishButton = (Button) v.findViewById(R.id.resultfinishbutton);
        finishButton.setOnClickListener(this);

        oxpercenttxt.setText(""+SPO2);
        RHR.setText(BPM);


        ImageButton imgprofileButton = v.findViewById(R.id.imgprofilebtn);
        imgprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).replaceFragment(SettingFragment.newInstance());
            }
        });
        id = pref.getValue(SharedPrefUtil.USER_ID,"");
        blood_oxygen = String.valueOf(SPO2);
        stress = String.valueOf(stresstxt.getText());
        arrhythmia = String.valueOf(arrhythmiatxt.getText());
        System.out.println("stress = String.valueOf(stresstxt.getText()) : "+stress);

        return v;
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.resultfinishbutton:
            {
                insertBPMDetail(id, BPM, "휴식", blood_oxygen, stress, arrhythmia, "정상입니다.");
                ((HomeActivity)getActivity()).replaceFragment(HomeFragment.newInstance());
                break;
            }
        }
    }
    /**
     * BPMDetail 데이터 삽입
     * @param id			: 사용자 입력 아이디
     * @param BPM	        : 심박 수
     * @param state	        : 현재상태(걷기, 뛰기, 이완, 휴식)
     * @param blood_oxygen	: 혈중산소농도
     * @param stress	    : 스트레스지수
     * @param arrhythmia	: 부정맥
     * @param diagnosis	    : 진단결과
     */
    public void insertBPMDetail( String id, String BPM, String state, String blood_oxygen, String stress, String arrhythmia, String diagnosis) {
        if (Build.VERSION.SDK_INT < AppConst.HONEYCOMB) {
            mAsyncTask_InsertBPMDetail = new Set_BPMDetailAsyncTask(mActivity, id, BPM, state, blood_oxygen, stress, arrhythmia, diagnosis).execute();
        } else {
            mAsyncTask_InsertBPMDetail = new Set_BPMDetailAsyncTask(mActivity, id, BPM, state, blood_oxygen, stress, arrhythmia, diagnosis).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}