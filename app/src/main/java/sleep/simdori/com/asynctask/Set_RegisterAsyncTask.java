package sleep.simdori.com.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.util.Locale;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.AppUI;
import sleep.simdori.com.R;
import sleep.simdori.com.util.JsonConnect;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;

/**
 * 서버에 장치공유 수락을 보낸다.
 * 네트워크 통신의 경우 별도의 쓰레드를 사용한다.
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class Set_RegisterAsyncTask extends AsyncTask<String, Void, Integer> {
    // API
    private SharedPrefUtil pref = null;
    private JsonConnect json	= null;
    private Locale locale		= null;

    // Values
    private String id, pw, email, phone = null;
    private int loginType = 0;
    private String language = null;
    private JSONObject job, responseJSON = null;
    private int retry_request = 0;
    private String response, resultCode, resultMsg = "";

    // Views
    private Context context;
    private ProgressBar pb;

    /**
     * @param context		: 호출한 액티비티
     * @param pb			: 프로그레스바
     * @param id			: 사용자 입력 아이디
     * @param email			: 사용자 입력 이메일
     * @param pw			: 사용자 입력 비밀번호
     */
    public Set_RegisterAsyncTask(Context context, ProgressBar pb, String id, String email, String pw, String phone) {
        this.context = context;
        this.pb = pb;
        this.id = id;
        this.pw = pw;
        this.email = email;
        this.phone = phone;
        System.out.println("Set_RegisterAsyncTask context : "+context.getPackageName());
        // 재시도 횟수
        pref = new SharedPrefUtil(context);
        retry_request = pref.getValue(SharedPrefUtil.RETRY_REQUEST+"Register", 0);
        System.out.println("retry_request : "+retry_request);
        // 연결 정보
        json = new JsonConnect(context, AppConst.Signup_host);

        // 현재언어 가져오기
        locale = context.getResources().getConfiguration().locale;
        language =  locale.getLanguage();
        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "회원가입 현재언어는 " + language);
    }

    /**
     * @param context		: 호출한 액티비티
     * @param pb			: 프로그레스바
     * @param id			: 사용자 입력 아이디
     * @param email			: 사용자 입력 이메일
     * @param pw			: 사용자 입력 비밀번호
     */
    public Set_RegisterAsyncTask(Context context, ProgressBar pb, String id, String email, String pw, String phone, int loginType) {
        this.context = context;
        this.pb = pb;
        this.id = id;
        this.pw = pw;
        this.email = email;
        this.phone = phone;
        this.loginType = loginType;

        // 재시도 횟수
        pref = new SharedPrefUtil(context);
        retry_request = pref.getValue(SharedPrefUtil.RETRY_REQUEST+"Register", 0);
        System.out.println("재시도 횟수 : " + retry_request);
        // 연결 정보
        json = new JsonConnect(context, AppConst.Signup_host);

        // 현재언어 가져오기
        locale = context.getResources().getConfiguration().locale;
        language =  locale.getLanguage();
        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "회원가입 현재언어는 " + language);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(pb!=null) pb.setVisibility(View.VISIBLE);
    }

    /**
     * <Request>
     * uri			: http://simdori.com:3000/signup;
     * id			: 사용자 입력 ID
     * eamil	 	: 사용자 입력 이메일
     * password 	: 사용자 입력 비밀번호
     * lang			: 현재 언어
     * <Response>
     * resultCode	: 1(성공)
     * resultMsg	: 결과 메시지
     * @see AsyncTask#doInBackground(Object[])
     */
    @Override
    protected Integer doInBackground(String... arg0) {
        // 재시도 횟수를 초과하는 경우, 사용자에게 알림 후 종료
        if(retry_request > AppConst.Network_Retry_Baseline) {
            System.out.println("재시도횟수초과!!!");
            return AppConst.Network_Failed_RetryOver;
        } else {
            try {
                job = new JSONObject();
                job.put("id", id);
                job.put("email", email);
                job.put("phone", phone);
                job.put("password", pw);
                job.put("lang", language);
                job.put("loginType", loginType);
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Singup_Data Request = " + job.toString());

                // JSON 처리
                response = json.Connect(job);
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Singup_Data Response = " + response);

                if(response != null) {
                    responseJSON = new JSONObject(response);
                    resultCode = responseJSON.getString("resultCode");
                    resultMsg = responseJSON.getString("resultMsg");
                    System.out.println("resultCode : "+ resultCode);
                    if (resultCode.equals(AppConst.Network_ResultMSG_Success)) {
                        return AppConst.Network_Success;
                    } else {
                        return AppConst.Network_Failed_Data;
                    }
                } else {
                    System.out.println("response = null ");
                    return AppConst.Network_Failed_Response;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return AppConst.Network_Failed_Connection;
            }
        }
    }

    /**
     * 연결 성공한 경우, 장치를 추가한다.
     * 나머지의 경우에는 알림창을 보여준다.
     * @param status : 결과값
     * @see AsyncTask#onPostExecute(Object)
     */
    @Override
    protected void onPostExecute(Integer status) {
        super.onPostExecute(status);
        if(pb!=null) pb.setVisibility(View.GONE);

        switch (status) {
            case AppConst.Network_Success:
                // 로그인 ID 저장
                pref.put(SharedPrefUtil.USER_ID, id);
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "회원가입 성공: " + resultMsg);

                // 일반 회원가입인 경우
                if(AppUI.getRegisterActivity()!=null && context==AppUI.getRegisterActivity()) {
                    ToastUtils.ToastShow(context, resultMsg+"");
                    if(AppUI.getRegisterActivity()!=null) AppUI.getRegisterActivity().onBackPressed();

                } else {
                    // 다른 액티비티는 처리X
                }
                break;


            case AppConst.Network_Failed_Data:
                ToastUtils.ToastShow(context, resultMsg+"");
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "회원가입 실패: " + resultMsg);
                break;

            case AppConst.Network_Failed_Connection:
            case AppConst.Network_Failed_Response:
                // 재시도 횟수 증가
                pref.put(SharedPrefUtil.RETRY_REQUEST+"Register", retry_request+1);
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "회원가입 / 서버에서 결과값 획득실패: " + status);

                try {
                    Thread.sleep(100);
                    if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "회원가입 / 100ms 후 재연결");
                } catch (InterruptedException e) {
                    // 다른 곳에서 알림 처리
                    e.printStackTrace();
                }

                // 서버 연결 재시도
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    new Set_RegisterAsyncTask(context, pb, id, email, pw, phone, loginType).execute();
                } else {
                    new Set_RegisterAsyncTask(context, pb, id, email, pw, phone, loginType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                return;

            case AppConst.Network_Failed_RetryOver:
                // 재시도 횟수 초기화
                pref.put(SharedPrefUtil.RETRY_REQUEST+"Register", 0);
                ToastUtils.ToastShow(context, context.getString(R.string.msg_mqtt_fetch_error));
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "회원가입 / Retry Failed: " + retry_request);
                break;

            default:
                // 재시도 횟수 초기화
                pref.put(SharedPrefUtil.RETRY_REQUEST+"Register", 0);
                ToastUtils.ToastShow(context, context.getString(R.string.msg_wall_fetch_error));
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "회원가입 / 네트워크 연결 오류");
                break;
        }
    }
}