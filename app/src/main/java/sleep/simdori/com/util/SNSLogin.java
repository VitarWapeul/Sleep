package sleep.simdori.com.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import sleep.simdori.com.R;
import sleep.simdori.com.activity.HomeActivity;
import sleep.simdori.com.activity.LoginActivity;

import static com.nhn.android.naverlogin.OAuthLogin.mOAuthLoginHandler;

//로그인관련기능들 관리하는 클래스
public class SNSLogin extends Application {
    OAuthLogin mOAuthLoginModule;
    Context mContext;
    Activity mActivity;
    //Google login
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    private String id, email, phone = null;
    private String token = null;
    int RC_SIGN_IN = 9001;

    public SNSLogin(Context context, Activity activity, String sns_type){
        mContext = context;
        mActivity = activity;

        switch (sns_type){
            case "naver":{
                Naver_Login();
            }

            case "google":{
                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                mOAuthLoginModule.startOauthLoginActivity((Activity) mContext, mOAuthLoginHandler);
                // Build a GoogleSignInClient with the options specified by gso.
                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                // Check for existing Google Sign In account, if the user is already signed in
                // the GoogleSignInAccount will be non-null.
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                Google_signIn();
            }

        }
    }

    private void Naver_Login(){
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
                    mActivity.finish();
                } else {
                    String errorCode = mOAuthLoginModule
                            .getLastErrorCode(mContext).getCode();
                    String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                    Toast.makeText(mContext, "errorCode:" + errorCode
                            + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();

                }
            };
        };
    }

    private void Google_signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);

    }



    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);//여기서 구글유저정보를 받아옴
            id = account.getEmail();//구글에서 가져올 id, 추후 적절한 값으로 수정필요
            id = id.substring(0,id.indexOf("@"));
            email = account.getEmail();//구글에서 가져올 email
            token = account.getIdToken();
            System.out.println("id : "+id+", email : "+email);
            //db에 정보저장 부분 구현해야함

            // Signed in successfully, show authenticated UI.

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);//구글로그인하면 홈으로 화면 전환
            startActivity(intent);


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            // google 로그인 실패시 처리
        }
    }
}



