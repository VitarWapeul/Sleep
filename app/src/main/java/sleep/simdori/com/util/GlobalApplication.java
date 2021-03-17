package sleep.simdori.com.util;

//import android.app.Application;
//import android.content.Context;
//
//import com.kakao.auth.KakaoSDK;
//
//
//
//public class GlobalApplication extends Application {
//    private static volatile GlobalApplication instance = null;
//
//    public static GlobalApplication getGlobalApplicationContext() {
//        if(instance == null) {
//            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
//        }
//        return instance;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        instance = this;
//        GlobalApplication.init(new KakaoSDKAdapter());
//    }
//
//    @Override
//    public void onTerminate() {
//        super.onTerminate();
//        instance = null;
//    }
//}
