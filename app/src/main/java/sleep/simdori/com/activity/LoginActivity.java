package sleep.simdori.com.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import java.security.MessageDigest;
import java.util.Locale;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.AppUI;
//import sleep.simdori.com.DatabaseOpenHelper;
import sleep.simdori.com.asynctask.Set_RegisterAsyncTask;
import sleep.simdori.com.util.DatabaseHandler;

import sleep.simdori.com.R;
import sleep.simdori.com.asynctask.Set_IDAsyncTask;
import sleep.simdori.com.asynctask.Set_LoginAsyncTask;
import sleep.simdori.com.asynctask.Set_PasswordAsyncTask;
import sleep.simdori.com.asynctask.TestAsyncTask;
import sleep.simdori.com.dialog.CustomDialog_Search;
import sleep.simdori.com.mqtt.MQTTservice;
import sleep.simdori.com.util.Network;
import sleep.simdori.com.util.PopupUtils;
import sleep.simdori.com.util.SNSLogin;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;

public class LoginActivity extends AppCompatActivity {
    private Intent intent = null;

    // database
    DatabaseHandler helper;
    SQLiteDatabase database;
    String sql;
    Cursor cursor;

    EditText idEditText;
    EditText pwdEditText;

    Button loginButton, btnSearch;
    Button btn_dialog;

    LinearLayout naverLoginButton;
    LinearLayout googleLoginButton;
    LinearLayout kakaoLoginButton;
    Button signupButton;

    // Views
    Activity mActivity;
    private ProgressBar pb, pb2;
    private TextView id_search, pw_search;

    // API
    private SharedPrefUtil pref = null;
    private AsyncTask<String, Void, Integer> mAsyncTask_Version, mAsyncTask_Login, mAsyncTask_Register,
            mAsyncTask_WIFI, mAsyncTask_ID, mAsyncTask_PW = null;
    private SNSLogin snsLogin;//로그인기능 모듈화하기 위한 시도
    private RegisterActivity register;//sns로그인 유저정보 db에 넣기위함
    // Values
    private String id, email, phone = null;
    private String token = null;
    private String user_id, user_pw = null;
    private int login_pw_save = 0, auto_login_save = 0;
    private CustomDialog_Search dialog_search;

    OAuthLogin mOAuthLoginModule;
    Context mContext;

    int RC_SIGN_IN = 9001;

    //Google login
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onResume() {
        super.onResume();
        if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - onResume()");
        AppUI.setLoginActivity(this);

		/*// 세션을 초기화 한다. 카카오톡으로만 로그인을 유도하고 싶다면 Session.initializeSession(this, mySessionCallback, AuthType.KAKAO_TALK)
		if(com.kakao.Session.initializeSession(this, mySessionCallback)){
			// 1. 세션을 갱신 중이면, 프로그레스바를 보이거나 버튼을 숨기는 등의 액션을 취한다
			kakao_login_button.setVisibility(View.GONE);
		} else if (com.kakao.Session.getCurrentSession().isOpened()){
			// 2. 세션이 오픈된 상태이면, 다음 activity로 이동한다.
			onSessionOpened();
		}
		// 3. else 로그인 창이 보인다.
		Log.i("LOG4 ", "session: "+com.kakao.Session.getCurrentSession().isOpened());*/
    }

    /*/**
     * Kakao Login API
     *//*
	private class MySessionStatusCallback implements com.kakao.SessionCallback {
		@Override
		public void onSessionOpened() {
			// 프로그레스바를 보이고 있었다면 중지하고 세션 오픈후 보일 페이지로 이동
			LoginActivity.this.onSessionOpened();
		}
		@Override
		public void onSessionClosed(final KakaoException exception) {
			// 프로그레스바를 보이고 있었다면 중지하고 세션 오픈을 못했으니 다시 로그인 버튼 노출.
			kakao_login_button.setVisibility(View.VISIBLE);
		}
	}
	protected void onSessionOpened(){
		final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}*/

    @Override
    protected void onPause() {
        super.onPause();
        AppUI.setLoginActivity(null);
        if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - onPause()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - onStop()");
    }

    /**
     * Wi-Fi 스캔 브로드캐스트 리시버 해제
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onDestroy() {
        // 실행중인 팝업 닫기
        if (PopupUtils.getNotity() != null) PopupUtils.getNotity().dismiss();
        if (pb != null) pb.setVisibility(View.GONE);

//        // 브로드캐스트 리시버 해제
//        if (wifiReceiver != null) unregisterReceiver(wifiReceiver);

        // 실행되고 있는 AsyncTask 정지
        if (mAsyncTask_Version != null) mAsyncTask_Version.cancel(true);
        if (mAsyncTask_WIFI != null) mAsyncTask_WIFI.cancel(true);
        if (mAsyncTask_Login != null) mAsyncTask_Login.cancel(true);
        if (mAsyncTask_Register != null) mAsyncTask_Register.cancel(true);
        if (mAsyncTask_ID != null) mAsyncTask_ID.cancel(true);
        if (mAsyncTask_PW != null) mAsyncTask_PW.cancel(true);

        super.onDestroy();
        if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - onDestory()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mActivity = this;
        if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - onCreate()");

        getAppKeyHash();//해쉬키얻기

        // 등록된 로그인정보 가져오기
        pref = new SharedPrefUtil(mActivity);
        user_id = pref.getValue(SharedPrefUtil.USER_ID, user_id);
        user_pw = pref.getValue(SharedPrefUtil.USER_PASSWORD, user_pw);
        if (AppConst.DEBUG_ALL)
            Log.i(AppConst.TAG, "LoginActivity - 로그인 등록정보: ID = " + user_id + " / PW = " + user_pw);
        // 비밀번호 저장여부
        login_pw_save = pref.getValue(SharedPrefUtil.LOGIN_PW_SAVE, 0);


        //initview 로 빼자------------------------//
        idEditText = (EditText) findViewById(R.id.loginInputId);
        pwdEditText = (EditText) findViewById(R.id.loginInputPwd);

        loginButton = (Button) findViewById(R.id.loginButton);
        naverLoginButton = (LinearLayout) findViewById(R.id.naverLoginButton) ;
        googleLoginButton = (LinearLayout) findViewById(R.id.googleLoginButton);
        kakaoLoginButton = (LinearLayout) findViewById(R.id.kakaoLoginButton);
        signupButton = (Button) findViewById(R.id.gotoSignupButton);
        btnSearch = (Button) findViewById(R.id.findPwdButton);

        // 아이디/비밀번호 찾기 팝업
        dialog_search = new CustomDialog_Search(mActivity);
        // 아이디 찾기
        id_search = dialog_search.getID();
        id_search.setTextColor(getResources().getColor(R.color.white));
        // 비밀번호 찾기
        pw_search = dialog_search.getPW();
        //-------------------------------------------


        // database
        helper = new DatabaseHandler(LoginActivity.this);
        database = helper.getWritableDatabase();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

//        // 플레이 스토어 버전을 확인하여 사용자에게 알림
//        if (Network.getConnectivityStatus(mActivity)) Version_Check();
//
//        // 네트워트 연결 실패한 경우
//        if (!pref.getValue(SharedPrefUtil.CONNECTION_POPUP, true)) {
//            onDisConnection();
//            // 동시 로그인된 경우
//        } else if (pref.getValue(SharedPrefUtil.LOGOUT_MQTT, false)) {
//            onLogoutMQTT();
//            // 로그아웃된 경우
//        } else if (pref.getValue(SharedPrefUtil.LOGOUT_POPUP, false)) {
//            onLogoutStatus();
//            // WI-FI 설정이 변경된 경우
//        } else if (pref.getValue(SharedPrefUtil.LuCI_WiFi_CHANGED, false)) {
//            onChanged_WIFI();
//            // WI-FI 설정이 실패한 경우
//        } else if (pref.getValue(SharedPrefUtil.LuCI_WiFi_Popup, false)) {
//            onDisConnection_WIFI();
//        } else {
//            // 알림내용이 없어 알림X
//        }
//
//        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        loginButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                user_id = idEditText.getText().toString().trim();
                user_pw = pwdEditText.getText().toString().trim();
                if (AppConst.DEBUG_ALL)
                    Log.i(AppConst.TAG, "LoginActivity - 로그인 입력정보: ID = " + user_id + " / PW = " + user_pw);

                // 1-1. 아이디를 입력하지 않는 경우
                if (user_id.equals("")) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.toast_id_null));
                    btn_dialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            idEditText.requestFocus();
                        }
                    });

                    // 1-2. 비밀번호를 입력하지 않은 경우
                } else if (user_pw.equals("")) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.toast_pw_null));
                    btn_dialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            pwdEditText.requestFocus();
                        }
                    });

                    // 1-3. Super User 연결
                } else if (user_id.equals(AppConst.Super_ID) && user_pw.equals(AppConst.Supwr_PW)) {
                    user_id = AppConst.Super_ID;
                    user_pw = AppConst.Supwr_PW;
                    onLoginStatus(user_id, user_pw, "", "");

                    // 1-3-2. 슈퍼 아이디가 입력된 경우
                } else if (user_id.equals(AppConst.Super_ID)) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.not_find_id));
                    btn_dialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            idEditText.requestFocus();
                        }
                    });

                } else {
                    login(user_id, user_pw, AppConst.LoginType_Default);
                }
            }
        });
        //네이버로그인
        naverLoginButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext = getApplicationContext();

                mOAuthLoginModule = OAuthLogin.getInstance();
                mOAuthLoginModule.init(mContext ,getString(R.string.naver_client_id) ,getString(R.string.naver_client_secret) ,getString(R.string.naver_client_name));

                @SuppressLint("HandlerLeak")
                OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
                    @Override
                    public void run(boolean success) {
                        if (success) {
                            String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                            String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                            long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                            String tokenType = mOAuthLoginModule.getTokenType(mContext);

                            Log.i("LoginData","accessToken : "+ accessToken);
                            Log.i("LoginData","refreshToken : "+ refreshToken);
                            Log.i("LoginData","expiresAt : "+ expiresAt);
                            Log.i("LoginData","tokenType : "+ tokenType);

                            Intent intent = new Intent(mContext, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorCode = mOAuthLoginModule
                                    .getLastErrorCode(mContext).getCode();
                            String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                            Toast.makeText(mContext, "errorCode:" + errorCode
                                    + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
                        }
                    };
                };

                mOAuthLoginModule.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);


            }
        });
        //구글로그인
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        googleLoginButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                signIn();

            }
        });
        //카카오로그인
        // 초기화
//        kakaoLoginButton.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                LoginClient.getInstance().loginWithKakaoTalk(context, (token, loginError) -> {
//                    if (loginError != null) {
//                        Log.e(TAG, "로그인 실패", loginError);
//                    } else {
//                        Log.d(TAG, "로그인 성공");
//
//                        // 사용자 정보 요청
//                        UserApiClient.getInstance().me((user, meError) -> {
//                            if (meError != null) {
//                                Log.e(TAG, "사용자 정보 요청 실패", meError);
//                            } else {
//                                Log.i(TAG, user.toString());
//                            }
//                            return null;
//                        });
//                    }
//                    return null;
//                });
//            }
//        });
        //회원가입 버튼 클릭
        signupButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(LoginActivity.this, "회원가입 화면으로 이동", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
                //finish();
            }
        });


        //회원 정보 찾기 팝업
        btnSearch.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                //아이디/비밀번호 찾기 버튼 클릭
                dialog_search.show();
            }
        });

        // 4-1. 아이디 찾기 선택
        dialog_search.getID().setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_search.setBackground(getResources().getDrawable(R.drawable.round_top_left_black));
                pw_search.setBackground(getResources().getDrawable(R.drawable.round_top_right_white));
                id_search.setTextColor(getResources().getColor(R.color.white));
                pw_search.setTextColor(getResources().getColor(R.color.hint));
                dialog_search.getID_Linear().setVisibility(View.VISIBLE);
                dialog_search.getPW_Linear().setVisibility(View.INVISIBLE);
                if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - 아이디 찾기를 선택하셨습니다.");
            }
        });
        // 4-1. 아이디 찾기 취소
        dialog_search.getID_Btn_Cancel().setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_search.dismiss();
            }
        });
        // 4-1. 아이디 찾기 확인
        dialog_search.getID_Btn_Confirm().setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = dialog_search.getID_Edit().getText().toString().trim();
                if (AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - 아이디 찾기 이메일: " + email);

                // 4-1-1. Email 입력하지 않은 경우
                if (email.equals("")) {
                    ToastUtils.ToastShow(mActivity, getString(R.string.toast_email_null));

                    // 4-1-2. WI-FI 설정 페이지
                } else if (!Network.getConnectivityStatus(mActivity)) {
                    // 팝업 닫기
                    dialog_search.getID_Edit().setText("");
                    dialog_search.dismiss();

                    // RPC 연결 플래그
                    pref.put(SharedPrefUtil.WIFI_CONNECTION, false);
                    connection_Popup();

                    // 4-1-3. ID찾기 연결
                } else {
                    // 팝업 닫기
                    dialog_search.getID_Edit().setText("");
                    dialog_search.dismiss();

                    id_search(email);
                }
            }
        });

        // 4-2. 비밀번호 찾기 선택
        dialog_search.getPW().setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_search.setBackground(getResources().getDrawable(R.drawable.round_top_left_white));
                pw_search.setBackground(getResources().getDrawable(R.drawable.round_top_right_black));
                id_search.setTextColor(getResources().getColor(R.color.hint));
                pw_search.setTextColor(getResources().getColor(R.color.white));
                dialog_search.getID_Linear().setVisibility(View.INVISIBLE);
                dialog_search.getPW_Linear().setVisibility(View.VISIBLE);
                if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - 비밀번호 찾기를 선택하셨습니다.");
            }
        });
        // 4-2. 비밀번호 찾기 취소
        dialog_search.getPW_Btn_Cancel().setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_search.dismiss();
            }
        });
        // 4-2. 비밀번호 찾기 확인
        dialog_search.getPW_Btn_Confirm().setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = dialog_search.getPW_id().getText().toString().trim();
                if (AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - 비밀번호 찾기 아이디: " + id);

                // 4-2-1. ID 입력하지 않은 경우
                if (id.equals("")) {
                    ToastUtils.ToastShow(mActivity, getString(R.string.toast_id_null));

                    // 4-2-2. 슈퍼 아이디가 입력된 경우
                } else if (id.equals(AppConst.Super_ID)) {
                    ToastUtils.ToastShow(mActivity, getString(R.string.not_find_id));

                    // 4-2-2. WIFI 설정 페이지
                } else if (!Network.getConnectivityStatus(mActivity)) {
                    // 팝업 닫기
                    dialog_search.getPW_id().setText("");
                    dialog_search.dismiss();

                    // RPC 연결 플래그
                    pref.put(SharedPrefUtil.WIFI_CONNECTION, false);
                    connection_Popup();

                    // 4-2-3. 비밀번호찾기 연결
                } else {
                    // 팝업 닫기
                    dialog_search.getPW_id().setText("");
                    dialog_search.dismiss();

                    password_search(id);
                }
            }
        });

    }
    //해쉬키얻기
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }
    /**
     * 사용자가 입력한 아이디와 비밀번호로 로그인한다.
     *
     * @param id : 사용자 아이디
     * @param pw : 사용자 비밀번호
     * @return None
     * @throws None
     */
    public void login(String id, String pw, int loginType) {
        // ksjung20170624 : removed since we dont' need it.
//		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        mAsyncTask_Login = new Set_LoginAsyncTask(mActivity, pb, id, pw, loginType).execute();
//		} else {
//			mAsyncTask_Login = new Set_LoginAsyncTask(mActivity, pb, id, pw, loginType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		}
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    // Configure sign-in to request the user's ID, email address, and basic
    // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

    //RegisterActivity에서 그대로 가져옴 추후 깔끔히 수정필요
    /**
     * 회원가입 연결
     * @param id	: 사용자 입력 아이디
     * @param email : 사용자 입력 이메일
     * @param pw	: 사용자 입력 비밀번호
     * @throws None
     * @return None
     */
    public void register(String id, String email, String pw, String phone) {
        if (Build.VERSION.SDK_INT < AppConst.HONEYCOMB) {
            mAsyncTask_Register = new Set_RegisterAsyncTask(mActivity, pb, id, email, pw, phone).execute();
            System.out.println("첫번째register");
        } else {
            mAsyncTask_Register = new Set_RegisterAsyncTask(mActivity, pb, id, email, pw, phone).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            System.out.println("두번째register");
        }
    }
    //로그인결과
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {//로그인이 성공했다면
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            System.out.println("구글로그인 성공");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);//여기서 구글유저정보를 받아옴
            id = account.getEmail();//구글에서 가져올 id, 추후 적절한 값으로 수정필요
            id = id.substring(0,id.indexOf("@"));
            email = account.getEmail();//구글에서 가져올 email
            token = account.getIdToken();
            System.out.println("id : "+id+", email : "+email);
            //db에 정보저장
            register(id,email,"","");
            // Signed in successfully, show authenticated UI.

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);//구글로그인하면 홈으로 화면 전환
            startActivity(intent);
            finish();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            // google 로그인 실패시 처리
        }
    }

//    /**
//     * 현재 앱의 버전을 플레이 스토어와 비교하여, 사용자에게 알림을 준다.
//     *
//     * @param NONE
//     * @return NONE
//     * @throws NONE
//     */
//    public void Version_Check() {
//        //20170331 DY 버전체크 처리
//        //사용자가 이전에 메세지 표시하지 않음을 체크했는지 확인
//        //현재의 millisecond와 저장된 millisecond를 비교하여 버전체크창의 유무를 확인한다.
//        long now = System.currentTimeMillis();
//        long version_check = pref.getValue(SharedPrefUtil.VERSION_CHECK_STATUS, 0L);
//        if (
//                (version_check != 0) &&//초기 Default값이 아니고
//                        ((now - version_check) < 7 * 24 * 60 * 60 * 1000)//현재 - 설정시간값 < 1주일(7일*24시간*60분*60초*1000mils)
//        ) {
//            // 버전체크를 보이지 않는다.
////			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Version_Check() - 1주일간 안보기 : 작동중");
//            return;
//        } else if (version_check == 0) {
//            //0이면 초기화상태이므로 버전체크 시작
////			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Version_Check() - 1주일간 안보기 : 설정값이 없음");
//        } else {
//            //현재 - 설정시간값 > 1주일 이므로
//            // 버전체크를 보인다.
////			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Version_Check() - 1주일간 안보기 : 설정한 날짜가 지남");
//        }
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//            mAsyncTask_Version = new Get_Version_Check_AsyncTask(mActivity, pb).execute();
//        } else {
//            mAsyncTask_Version = new Get_Version_Check_AsyncTask(mActivity, pb).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
//    }

    /**
     * 로그인 성공하면 기본화면으로 이동한다.
     *
     * @param login_id:  아이디
     * @param login_pw:  비밀번호
     * @param user_email : 이메일
     * @param user_phone : 전화번호
     * @return None
     */
    public void onLoginStatus(String login_id, String login_pw, String user_email, String user_phone) {
        // 1. MQTTservice 시작
        try {
            startService(new Intent(mActivity, MQTTservice.class));
            if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - MQTT Service를 시작하였습니다!!!");
        } catch (Exception e) {
            // 서버에 연결하지 못하였습니다. 네트워크를 확인하거나 잠시 후 다시 시도하십시오.
            ToastUtils.ToastShow(mActivity, R.string.msg_mqtt_fetch_error);
            if (AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "LoginActivity - MQTT Service 시작 실패!!!");
            e.printStackTrace();
        }

        // 1-1. MQTT 재연결 여부 저장
        if (login_id.equals(user_id)) {
            pref.put(SharedPrefUtil.MQTT_Connection, true);
            if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - MQTT 동일한 아이디입니다. ");
        } else {
            pref.put(SharedPrefUtil.MQTT_Connection, false);
            if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - MQTT 다른 아이디입니다. ");
        }

        // 2. 로그인 정보 저장
        pref.put(SharedPrefUtil.USER_ID, login_id);
        pref.put(SharedPrefUtil.USER_PASSWORD, login_pw);
        pref.put(SharedPrefUtil.USER_EMAIL, user_email);
        pref.put(SharedPrefUtil.USER_PHONE, user_phone);
        pref.put(SharedPrefUtil.LOGIN_STATUS, 1);

        // 3. 로그인상태 체크
        int auto_login_satus = pref.getValue(SharedPrefUtil.AUTO_LOGIN_STATUS, AppConst.LOGIN_NO_AUTOLOGIN);
        if (pref.getValue(SharedPrefUtil.FACEBOOK_LOGIN, false)) {
            pref.put(SharedPrefUtil.LOGIN_STATUS, AppConst.LoginStatus_Facebook);
            if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - 페이스북 로그인 되었습니다.");
        } else if (auto_login_satus == AppConst.LOGIN_AUTOLOGIN) {
            pref.put(SharedPrefUtil.LOGIN_STATUS, AppConst.LoginStatus_Auto);
            if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - 자동 로그인 되었습니다.");
        } else {
            pref.put(SharedPrefUtil.LOGIN_STATUS, AppConst.LoginStatus_Default);
            if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - 로그인 되었습니다.");
        }

//        // 4. 장치목록 로딩 초기화
//        pref.put(SharedPrefUtil.RELOADING_LIST, false);

//        //20170316 DY 로그인시 복원기능 구현
//        if (AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - 로그인시 복원기능실행");
//        String user_ID = pref.getValue(SharedPrefUtil.USER_ID, "");
//        AsyncTask<String, Void, Integer> mAsyncTask_Restore;
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//            mAsyncTask_Restore = new Get_Group_Restore_AsyncTask(mActivity, pb, user_ID).execute();
//        } else {
//            mAsyncTask_Restore = new Get_Group_Restore_AsyncTask(mActivity, pb, user_ID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }

        // 5. 기본화면으로 이동
        intent = new Intent(mActivity, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * 네트워크 연결이 원할하지 않는 경우, Wi-Fi 연결 알림창을 보여준다.
     *
     * @param None
     * @return None
     * @throws None
     */
    public void connection_Popup() {
        // Wi-Fi 설정 플래그
        pref.put(SharedPrefUtil.LuCI_WiFi_Setting, true);
        pref.put(SharedPrefUtil.CONNECTION_POPUP, true);

        // Wi-Fi 연결
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            btn_dialog = PopupUtils.Notify(mActivity, R.string.msg_wifi_login_fail, R.string.Btn_wifi);
            btn_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 2-1. 알림창 닫기
                    PopupUtils.getNotity().dismiss();

                    // 2-2. Wi-Fi 설정페이지로 이동
                    intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

            // 마시멜로 이하인 경우에는 Wi-Fi 목록 보여주기
        } else {
//            setApList(getString(R.string.login_btn_local), AppConst.WIFI_Setting_Connect);
        }

    }

    /**
     * 사용한 입력한 이메일를 서버에 보내, ID 찾기를 요청한다.
     *
     * @param email : 사용자 입력 이메일
     * @return None
     * @throws None
     */
    public void id_search(String email) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mAsyncTask_ID = new Set_IDAsyncTask(LoginActivity.this, pb, email).execute();
        } else {
            mAsyncTask_ID = new Set_IDAsyncTask(LoginActivity.this, pb, email).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * 사용한 입력한 이메일를 서버에 보내, 비밀번호 찾기를 요청한다.
     *
     * @param id    : 사용자 입력 아이디
     * @param email : 사용자 입력 이메일
     * @return None
     * @throws None
     */
    public void password_search(String id) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//            mAsyncTask_PW = new Set_PasswordAsyncTask(LoginActivity.this, pb, id).execute();
//
//        } else {
//            mAsyncTask_PW = new Set_PasswordAsyncTask(LoginActivity.this, pb, id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            AsyncTask<String, Void, Integer> b = new TestAsyncTask(LoginActivity.this, pb, id).execute();

        } else {
            AsyncTask<String, Void, Integer> b = new TestAsyncTask(LoginActivity.this, pb, id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

}
