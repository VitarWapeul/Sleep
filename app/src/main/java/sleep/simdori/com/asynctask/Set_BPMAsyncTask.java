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

public class Set_BPMAsyncTask extends AsyncTask {
    // API
    private SharedPrefUtil pref = null;
    private JsonConnect json	= null;
    private Locale locale		= null;

    // Values
    private String id, green, red, BPM, dateTime = null;
    private JSONObject job, responseJSON = null;
    private int retry_request = 0;
    private String response, resultCode, resultMsg = "";

    private Context context;
    private ProgressBar pb;

    /**
     * @param context		: 호출한 액티비티
     * @param pb			: 프로그레스바
     * @param id			: 사용자 입력 아이디
     * @param green			: green 배열
     * @param red			: red 배열
     * @param BPM	    : 측정 날짜
     */
    public Set_BPMAsyncTask(Context context, ProgressBar pb, String id, String green, String red, String BPM) {
        this.context = context;
        this.pb = pb;
        this.id = id;
        this.green = green;
        this.red = red;
        this.BPM = BPM;

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(koreaTimeZone);
        this.dateTime = dateFormat.format(date);

        // 재시도 횟수
        pref = new SharedPrefUtil(context);
        retry_request = pref.getValue(SharedPrefUtil.RETRY_REQUEST+"InsertBPM", 0);

        // 연결 정보
        json = new JsonConnect(context, AppConst.InsertBPM_host);

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
                job.put("date_time", dateTime);
                job.put("green", green);
                job.put("red", red);
                job.put("lang", "ko");
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "InsertBPM_Data Request = " + job.toString());

                // JSON 처리
                response = json.Connect(job);
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "InsertBPM_Data Response = " + response);

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
        super.onPreExecute();
        if(pb!=null) pb.setVisibility(View.VISIBLE);

    }
}
