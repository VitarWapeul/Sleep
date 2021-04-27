package sleep.simdori.com.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Objects;

import sleep.simdori.com.R;

public class MyReceiver extends BroadcastReceiver {

    public  static final String MY_ACTION = "sleep.simdori.com.util.action.ACTION_WALK_BRODCAST";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if(Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())){
            //동작코드
        }
        else if(MY_ACTION.equals(intent.getAction())){
            //동작코드
            Toast.makeText(context,"walk_broadcast",Toast.LENGTH_SHORT).show();
            System.out.println("MY_ACTION_broadcast 동작");
            // Notification 세팅
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            }


        }
//        else if(Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")){ //재부팅되면 리시버를 다시 등록하는 코드
//            // on device boot complete, reset the alarm
//            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
//
//            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
////
//
//            SharedPreferences sharedPreferences = context.getSharedPreferences("daily alarm", MODE_PRIVATE);
//            long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis());
//
//
//            Calendar current_calendar = Calendar.getInstance();
//            Calendar nextNotifyTime = new GregorianCalendar();
//            nextNotifyTime.setTimeInMillis(sharedPreferences.getLong("nextNotifyTime", millis));
//
//            if (current_calendar.after(nextNotifyTime)) {
//                nextNotifyTime.add(Calendar.DATE, 1);
//            }
//
//            Date currentDateTime = nextNotifyTime.getTime();
//            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
//            Toast.makeText(context.getApplicationContext(),"[재부팅후] 다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();
//
//
//            if (manager != null) {
//                manager.setRepeating(AlarmManager.RTC_WAKEUP, nextNotifyTime.getTimeInMillis(),
//                        AlarmManager.INTERVAL_DAY, pendingIntent);
//            }
//        }
    }
}