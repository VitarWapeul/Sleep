package sleep.simdori.com.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.util.JsonConnect;
import sleep.simdori.com.util.SharedPrefUtil;

public class Set_BPMDetailAsyncTask extends AsyncTask {

    // API
    private SharedPrefUtil pref = null;
    private JsonConnect json	= null;
    private Locale locale		= null;

    // Values
    String id;
    String BPM;
    String state;
    String blood_oxygen;
    String stress;
    String arrhythmia;
    String date_time;
    String diagnosis;

    private Context context;

    private JSONObject job, responseJSON = null;
    private int retry_request = 0;
    private String response, resultCode, resultMsg = "";

    /**
     * @param context		: 호출한 액티비티
     * @param id			: 사용자 입력 아이디
     * @param BPM	        : 심박 수
     * @param state	        : 현재상태(걷기, 뛰기, 이완, 휴식)
     * @param blood_oxygen	: 혈중산소농도
     * @param stress	    : 스트레스지수
     * @param arrhythmia	: 부정맥
     * @param diagnosis	    : 진단결과
     */

    public Set_BPMDetailAsyncTask(Context context, String id, String BPM, String state, String blood_oxygen, String stress, String arrhythmia, String diagnosis) {
        this.context = context;
        this.id = id;
        this.BPM = BPM;
        this.state = state;
        this.blood_oxygen = blood_oxygen;
        this.stress = stress;
        this.arrhythmia = arrhythmia;
        this.diagnosis = diagnosis;
        System.out.println("this.stress = stress; : "+stress);
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(koreaTimeZone);
        this.date_time = dateFormat.format(date);

        // 재시도 횟수
        pref = new SharedPrefUtil(context);
        retry_request = pref.getValue(SharedPrefUtil.RETRY_REQUEST+"InsertBPMDetail", 0);

        // 연결 정보
        json = new JsonConnect(context, AppConst.InsertBPMDetail_host);

    }
//    public Set_BPMAsyncTask(String id, String green, String red, String BPM) {
//        this.context = context;
//        this.pb = pb;
//        this.id = id;
//        this.green = green;
//        this.red = red;
//        this.BPM = BPM;
//
//        Date date = new Date();
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        TimeZone koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul");
//        dateFormat.setTimeZone(koreaTimeZone);
//        this.dateTime = dateFormat.format(date);
//
//        // 재시도 횟수
//        pref = new SharedPrefUtil(context);
//        retry_request = pref.getValue(SharedPrefUtil.RETRY_REQUEST+"InsertBPM", 0);
//
//        // 연결 정보
//        json = new JsonConnect(context, AppConst.InsertBPM_host);
//
//    }

    @Override
    protected Object doInBackground(Object[] objects) {
        // 재시도 횟수를 초과하는 경우, 사용자에게 알림 후 종료
        if(retry_request > AppConst.Network_Retry_Baseline) {
            return AppConst.Network_Failed_RetryOver;
        } else {
            try {
                job = new JSONObject();
                job.put("id", id);
                job.put("BPM", BPM);
                job.put("state", state);
                job.put("blood_oxygen", blood_oxygen);
                job.put("stress", stress);
                job.put("arrhythmia", arrhythmia);
                job.put("date_time", date_time);
                job.put("diagnosis", diagnosis);
                job.put("lang", "ko");

                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "InsertBPMDetail_Data Request = " + job.toString());

                // JSON 처리
                response = json.Connect(job);
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "InsertBPMDetail_Data Response = " + response);

                if(response != null) {
                    responseJSON = new JSONObject(response);
                    resultCode = responseJSON.getString("resultCode");
                    resultMsg = responseJSON.getString("resultMsg");
                    if (resultCode.equals(AppConst.Network_ResultMSG_Success)) {
                        return AppConst.Network_Success;
                    } else {
                        return AppConst.Network_Failed_Data;
                    }
                } else {
                    return AppConst.Network_Failed_Response;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return AppConst.Network_Failed_Connection;
            }
        }
    }

    @Override
    protected void onPreExecute() {

    }
}
