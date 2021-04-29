package sleep.simdori.com.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.item.GetDeviceList;
import sleep.simdori.com.item.GetGroupList;
import sleep.simdori.com.item.GetMessageList;

/**
 * 장치정보를 저장하는 로컬DB
 * <Database>
 * Version 	: 3
 * Name		: DeviceList
 * <Table>
 * Name					: device_list
 * KEY_NO 				: 목록 수
 * KEY_DEVICE_MAC 		: 장치 맥주소
 * KEY_DEVICE_NAME		: 장치 이름
 * KEY_DEVICE_STATUS	: 장치 상태 (0: OFFLINE /  1: ONLINE)
 * KEY_STATUS 			: 공유 여부 (0: 신규 / 1: 공유 요청 / 2: 공유 수락 / 3: 공유받은 기기)
 * KEY_SOCKET 	    	: 장치 소켓 (0: 본체 / 1~4: 소켓)
 * KEY_SOCKET_IMG 		: 소켓 아이콘
 * KEY_SOCKET_NAME 		: 소켓 이름
 * KEY_WAKEUP 			: 켜지는 시간
 * KEY_SLEEP			: 꺼지는 시간
 * KEY_GPIO 	    	: 소켓 상태	(0: OFF / 1: ON)
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class DatabaseHandler extends SQLiteOpenHelper {
	// 0-1. 데이터베이스 버전
	private static final int DATABASE_VERSION = 15;
	// 0-2. 데이터베이스 이름
	private static final String DATABASE_NAME = "simdori.db";



	// 1-1. 장치 테이블 이름
	private static final String TABLE_CONTACTS = "device_list";
	// 1-2. 장치 테이블 컬럼
	private static final String KEY_NO 				= "no";					// 0. 목록 수
	private static final String KEY_DEVICE_MAC 		= "device_mac";			// 1. 장치 맥주소 (02:00:00:00:00:00)
	private static final String KEY_DEVICE_NAME		= "device_name";		// 2. 장치 이름
	private static final String KEY_DEVICE_STATUS	= "device_status";		// 3. 장치 상태 여부 (0: OFFLINE /  1: ONLINE)
	private static final String KEY_STATUS 			= "status";				// 4. 공유한 장치 여부 (0: 신규 / 1: 공유 요청 / 2: 공유 수락 / 3: 공유받은 기기)
	private static final String KEY_SOCKET 	    	= "socket";				// 5. 장치 소켓 (0: 본체 / 1~4: 소켓)
	private static final String KEY_SOCKET_NAME 	= "socket_name";		// 6. 소켓 이름
	private static final String KEY_GPIO 	    	= "gpio";				// 7. 소켓 상태 (0: OFF / 1: ON)
	private static final String KEY_SOCKET_IMG 		= "socket_img";			// 8. 소켓 아이콘 (소켓 대기젼력)
	private static final String KEY_SOCKET_ACTIVE	= "socket_active";		// 9. 소켓 사용전력
	private static final String KEY_WAKEUP 			= "wakeup";				// 10. 켜지는 시간 (0000-00-00 00:00:00)
	private static final String KEY_SLEEP			= "sleep";				// 11. 꺼지는  시간 (0000-00-00 00:00:00)
	private static final String KEY_WAKEUPDAY 		= "wakeupday";			// 12. 켜지는 요일 (0000000 ~ 1111111)
	private static final String KEY_SLEEPDAY		= "sleepday";			// 13. 꺼지는 요일 (0000000 ~ 1111111)
	private static final String KEY_DEVICE_WAKEUP 	= "device_wakeup";		// 14. 장치 켜지는 시간 (0000-00-00 00:00:00)
	private static final String KEY_DEVICE_SLEEP	= "device_sleep";		// 15. 장치 꺼지는 시간 (0000-00-00 00:00:00)
	private static final String KEY_DEVICE_WAKEUPDAY = "device_wakeupday";	// 16. 장치 켜지는 요일 (0000000 ~ 1111111)
	private static final String KEY_DEVICE_SLEEPDAY	 = "device_sleepday";	// 17. 장치 꺼지는 요일 (0000000 ~ 1111111)
	private static final String KEY_MASTERPLUG    			= "masterplug";				// 18. 마스터 플러그 (0: OFF / 1: ON)
	private static final String KEY_PLUGCONTROL	    		= "plugcontrol";			// 19. 플러그 제어 (0: OFF / 1: ON)
	private static final String KEY_PLUGBLOCKER	    		= "plugblocker";			// 20. 플러그 차단 (0: OFF / 1: ON)
	private static final String KEY_STANBYPOWER    			= "stanbypower";			// 21. 대기전력 차단 (0: OFF / 1: ON)
	private static final String KEY_STANBYPOWER_THRESHOLD	= "stanbypower_threshold";	// 22. 대기전력 차단 기준치
	private static final String KEY_VOICEALERT	    		= "voicealert";				// 23. 음성 알람 (0: OFF / 1: ON)
	private static final String KEY_FIREALERT    			= "firealert";				// 24. 화재 알람 (0: OFF / 1: ON)
	private static final String KEY_FIREALERT_THRESHOLD		= "firealert_threshold";	// 25. 화재 알람 기준치(0: OFF / 1: ON)
	private static final String KEY_GASALERT	    		= "gasalert";				// 26. 가스 알람 (0: OFF / 1: ON)
	private static final String KEY_GASALERT_CO	    		= "co";						// 27. 가스 알람 기준치 - CO
	private static final String KEY_GASALERT_LPG			= "lpg";					// 28. 가스 알람 기준치 - LPG
	private static final String KEY_GASALERT_SMOKE			= "smoke";					// 29. 가스 알람 기준치 - SMOKE
	private static final String KEY_LATITUDE	    		= "latitude";				// 30. 위도
	private static final String KEY_LONGTITUDE	   			= "longtitude";				// 31. 경도
	private static final String KEY_VERSION	    			= "version";				// 32. 기기 버전
	private static final String KEY_FWTIME	    			= "fwtime";					// 33. FW 빌드시간

//	// 2-1. 공유기 테이블 이름
//	private static final String TABLE_ROUTER = "router_list";
//	// 2-2. 공유기 테이블 컬럼
//	private static final String KEY_ROUTER_NO 			= "no";			// 0. 목록 수
//	private static final String KEY_ROUTER_HOSTNAME 	= "hostname";	// 1. 장치 이름
//	private static final String KEY_ROUTER_IPADDR 		= "ipaddr";		// 2. 장치 IP
//	private static final String KEY_ROUTER_MACADDR 		= "macaddr";	// 3. 장치 MAC
//	private static final String KEY_ROUTER_EXPIRES 		= "expires";	// 4. 연결시간

	// 3-1. 그룹 테이블 이름
	private static final String TABLE_GROUP = "group_list";
	// 3-2. 그룹 테이블 컬럼
	private static final String KEY_GROUP_NO 			= "group_no";			// 0. 목록 수
	private static final String KEY_GROUP_NAME 			= "group_name";			// 1. 그룹 이름
	private static final String KEY_GROUP_DEVICES 		= "group_devices";		// 2. 장치 목록
	private static final String KEY_GROUP_STATUS 		= "group_status";		// 3. 그룹 상태
	private static final String KEY_GROUP_USER_ID 		= "group_user_id";		// 4. 사용자 아이디
	private static final String KEY_GROUP_SLEEPTIME	 	= "group_sleeptime";		// 5. 꺼지는 시간
	private static final String KEY_GROUP_WAKEUPTIME	= "group_wakeuptime";		// 6. 켜지는 시간
	private static final String KEY_GROUP_SLEEPTIME_DAY	 = "group_sleeptime_day";	// 7. 꺼지는 요일
	private static final String KEY_GROUP_WAKEUPTIME_DAY = "group_wakeuptime_day";	// 8. 켜지는 요일
	private static final String KEY_GROUP_INSOCKET		= "group_insocket";			// 9. AI 들어오는 경우
	private static final String KEY_GROUP_OUTSOCKET		= "group_outsocket";		// 10. AI 나가는 경우

//	// 4-1. 공유권한 테이블 이름
//	private static final String TABLE_SHARE = "share_list";
//	// 4-2. 공유권한 테이블 컬럼
//	private static final String KEY_SHARE_NO 			= "no";					// 0. 목록 수
//	private static final String KEY_SHARE_USER_ID		= "user_id";			// 1. 사용자 아이디
//	private static final String KEY_SHARE_DEVICE_MAC	= "device_mac";			// 2 장치 맥주소
//	private static final String KEY_SHARE_ID			= "share_id";			// 3. 공유해 준 아이디
//	private static final String KEY_SHARE_PLUGCONTROL	= "share_plugcontrol";	// 4. 플러그 제어 권한
//	private static final String KEY_SHARE_MASTERPLUG	= "share_masterplug";	// 5. 마스터플러그 설정 권한
//	private static final String KEY_SHARE_STANBYPOWER	= "share_stanbypower";	// 6. 대기전력 차단 권한
//	private static final String KEY_SHARE_GASALERT		= "share_gas_alert";	// 7. 가스 알람 설정 권한
//	private static final String KEY_SHARE_FIREALERT		= "share_fire_alert";	// 8. 화재 알람 설정 권한
//	private static final String KEY_SHARE_VOICEALERT	= "share_vocie_alert";	// 9. 음성 알람 설정 권한

	// 5-1. 공지사항 테이블 이름
	private static final String TABLE_NOTICE = "notice_list";
	// 5-2. 공지사항 테이블 컬럼
	private static final String KEY_NOTICE_NO 			= "no";					// 0. 목록 수
	private static final String KEY_NOTICE_TITLE		= "notice_title";		// 1. 타이틀
	private static final String KEY_NOTICE_DATE			= "notice_date";		// 2. 작성 날짜
	private static final String KEY_NOTICE_CONTENTS		= "notice_contents";	// 3. 콘텐츠
	private static final String KEY_NOTICE_USERID		= "notice_user_id";		// 4. 사용자 아이디

	// 6-1. 메시지박스 테이블 이름
	private static final String TABLE_MESSAGE = "message_list";
	// 6-2. 메시지박스 테이블 컬럼
	private static final String KEY_MESSAGE_NO 				= "message_no";				// 0. 목록 수
	private static final String KEY_MESSAGE_SHARINGID		= "message_sharing_id";		// 1. 공유한 사람
	private static final String KEY_MESSAGE_SHAREDID		= "message_shared_id";		// 2. 공유받은 사람
	private static final String KEY_MESSAGE_SHARINGMAC		= "message_shared_mac";		// 3. 공유한 장치
	private static final String KEY_MESSAGE_SHARINGPOPUP 	= "message_shared_popup";	// 4. 공유 팝업
	private static final String KEY_MESSAGE_USERID			= "message_user_id";		// 5. 공유받은 사람

	SQLiteDatabase db;

	// 7-1. BPM 테이블 이름
	private static final String TABLE_BPM = "bpm_data";

	// 7-2. BPM 테이블 컬럼
	private static final String KEY_BPM			= "bpm";			// 0. 목록 수
	// 8-1. STEP 테이블 이름
	private static final String TABLE_STEP = "step_data";
	// 8-2. STEP 테이블 칼럼
	private static final String KEY_STEP			= "step";
	private static final String STEP_DATE			= "step_date";
	private static final String STEP_COUNT			= "step_count";
	/**
	 * 데이터베이스 생성
	 * @param context : 호출된 액티비티
	 */
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * 테이블 추가
	 * @see SQLiteOpenHelper#onCreate(SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_STEPDATA_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_STEP + "("
				+ KEY_STEP  + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ STEP_DATE + " TEXT,"
				+ STEP_COUNT + "INTEGER" +")";
		db.execSQL(CREATE_STEPDATA_TABLE);

		String CREATE_BPMDATA_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_BPM + "("
				+ KEY_BPM + " TEXT" + ")";
		db.execSQL(CREATE_BPMDATA_TABLE);


		String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + "("
				+ KEY_NO 			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KEY_DEVICE_MAC 	+ " TEXT,"
				+ KEY_DEVICE_NAME 	+ " TEXT,"
				+ KEY_DEVICE_STATUS	+ " INTEGER,"
				+ KEY_STATUS		+ " INTEGER,"
				+ KEY_SOCKET		+ " INTEGER,"
				+ KEY_SOCKET_NAME	+ " TEXT,"
				+ KEY_GPIO			+ " INTEGER,"
				//+ KEY_SOCKET_IMG	+ " FLOAT,"
				+ KEY_SOCKET_IMG	+ " INTEGER,"
				+ KEY_SOCKET_ACTIVE	+ " INTEGER,"
				+ KEY_WAKEUP 		+ " TEXT,"
				+ KEY_SLEEP			+ " TEXT,"
				+ KEY_WAKEUPDAY 	+ " TEXT,"
				+ KEY_SLEEPDAY		+ " TEXT,"
				+ KEY_DEVICE_WAKEUP 		+ " TEXT,"
				+ KEY_DEVICE_SLEEP			+ " TEXT,"
				+ KEY_DEVICE_WAKEUPDAY 		+ " TEXT,"
				+ KEY_DEVICE_SLEEPDAY		+ " TEXT,"
				+ KEY_MASTERPLUG			+ " INTEGER,"
				+ KEY_PLUGCONTROL			+ " INTEGER,"
				+ KEY_PLUGBLOCKER			+ " INTEGER,"
				+ KEY_STANBYPOWER			+ " INTEGER,"
				+ KEY_STANBYPOWER_THRESHOLD	+ " INTEGER,"
				+ KEY_VOICEALERT			+ " INTEGER,"
				+ KEY_FIREALERT				+ " INTEGER,"
				+ KEY_FIREALERT_THRESHOLD	+ " INTEGER,"
				+ KEY_GASALERT				+ " INTEGER,"
				+ KEY_GASALERT_CO			+ " INTEGER,"
				+ KEY_GASALERT_LPG			+ " INTEGER,"
				+ KEY_GASALERT_SMOKE		+ " INTEGER,"
				+ KEY_LATITUDE				+ " DOUBLE,"
				+ KEY_LONGTITUDE			+ " DOUBLE,"
				+ KEY_VERSION				+ " INTEGER,"
				+ KEY_FWTIME				+ " TEXT" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);

//		String CREATE_ROUTER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTER + "("
//				+ KEY_ROUTER_NO 		+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
//				+ KEY_ROUTER_HOSTNAME 	+ " TEXT,"
//				+ KEY_ROUTER_IPADDR 	+ " TEXT,"
//				+ KEY_ROUTER_MACADDR	+ " TEXT,"
//				+ KEY_ROUTER_EXPIRES	+ " TEXT" + ")";
//		db.execSQL(CREATE_ROUTER_TABLE);
//
//		String CREATE_GROUP_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_GROUP + "("
//				+ KEY_GROUP_NO 		+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
//				+ KEY_GROUP_NAME	+ " TEXT,"
//				+ KEY_GROUP_DEVICES	+ " TEXT,"
//				+ KEY_GROUP_STATUS	+ " INTEGER,"
//				+ KEY_GROUP_SLEEPTIME	+ " TEXT,"
//				+ KEY_GROUP_WAKEUPTIME	+ " TEXT,"
//				+ KEY_GROUP_SLEEPTIME_DAY	+ " TEXT,"
//				+ KEY_GROUP_WAKEUPTIME_DAY	+ " TEXT,"
//				+ KEY_GROUP_INSOCKET	+ " INTEGER,"
//				+ KEY_GROUP_OUTSOCKET	+ " INTEGER,"
//				+ KEY_GROUP_USER_ID	+ " TEXT" + ")";
//		db.execSQL(CREATE_GROUP_TABLE);
//
//		String CREATE_SHARE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SHARE + "("
//				+ KEY_SHARE_NO 			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
//				+ KEY_SHARE_USER_ID		+ " TEXT,"
//				+ KEY_SHARE_DEVICE_MAC	+ " TEXT,"
//				+ KEY_SHARE_ID			+ " TEXT,"
//				+ KEY_SHARE_PLUGCONTROL	+ " INTEGER,"
//				+ KEY_SHARE_MASTERPLUG	+ " INTEGER,"
//				+ KEY_SHARE_STANBYPOWER	+ " INTEGER,"
//				+ KEY_SHARE_GASALERT	+ " INTEGER,"
//				+ KEY_SHARE_FIREALERT	+ " INTEGER,"
//				+ KEY_SHARE_VOICEALERT	+ " INTEGER" + ")";
//		db.execSQL(CREATE_SHARE_TABLE);

		String CREATE_NOTICE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTICE + "("
				+ KEY_NOTICE_NO 		+ " INTEGER,"
				+ KEY_NOTICE_TITLE		+ " TEXT,"
				+ KEY_NOTICE_DATE		+ " TEXT,"
				+ KEY_NOTICE_CONTENTS	+ " TEXT,"
				+ KEY_NOTICE_USERID 	+ " TEXT" + ")";
		db.execSQL(CREATE_NOTICE_TABLE);

		String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGE + "("
				+ KEY_MESSAGE_NO 			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KEY_MESSAGE_SHARINGID		+ " TEXT,"
				+ KEY_MESSAGE_SHAREDID		+ " TEXT,"
				+ KEY_MESSAGE_SHARINGMAC	+ " TEXT,"
				+ KEY_MESSAGE_SHARINGPOPUP 	+ " INTEGER,"
				+ KEY_MESSAGE_USERID 		+ " TEXT" + ")";
		db.execSQL(CREATE_MESSAGE_TABLE);
	}

	/**
	 * 데이터베이스 업그레이드
	 * @see SQLiteOpenHelper#onUpgrade(SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			// 이전 버전이 존재하면 테이블 삭제
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
//			db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTER);
//			db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
//			db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHARE);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTICE);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_BPM);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEP);
			// 테이블 새로 추가
			onCreate(db);
			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "데이터베이스 업데이트!!");
		} else {
			// 로컬 DB를 변경할 필요가 없으므로 알림X
		}
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operation - 장치추가
	 * @param 	contact : 장치 정보
	 * @throws 	None
	 * @return 	None
	 */
	public void addDevice(GetDeviceList contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_DEVICE_MAC, contact.getKeyDeviceMac());
		values.put(KEY_DEVICE_NAME, contact.getKeyDeviceName());
		values.put(KEY_DEVICE_STATUS, contact.getKeyDeviceStatus());
		values.put(KEY_STATUS, contact.getKeyStatus());
		values.put(KEY_SOCKET, contact.getKeySocket());
		values.put(KEY_SOCKET_NAME, contact.getKeySocketName());
		values.put(KEY_GPIO, contact.getKeyGPIO());
		values.put(KEY_SOCKET_IMG, contact.getKeySocketImg());
		values.put(KEY_SOCKET_ACTIVE, contact.getKeySocketActive());
		values.put(KEY_WAKEUP, contact.getKeyWakeUp());
		values.put(KEY_SLEEP, contact.getKeySleep());
		values.put(KEY_WAKEUPDAY, contact.getKeyWakeUpDay());
		values.put(KEY_SLEEPDAY, contact.getKeySleepDay());
		values.put(KEY_DEVICE_WAKEUP, contact.getKeyDevice_WakeUp());
		values.put(KEY_DEVICE_SLEEP, contact.getKeyDevice_Sleep());
		values.put(KEY_DEVICE_WAKEUPDAY, contact.getKeyDevice_WakeUpDay());
		values.put(KEY_DEVICE_SLEEPDAY, contact.getKeyDevice_SleepDay());
		values.put(KEY_PLUGCONTROL, contact.getKeyPlugControl());
		values.put(KEY_PLUGBLOCKER, contact.getKeyPlugBlocker());
		values.put(KEY_MASTERPLUG, contact.getKeyMasterPlug());
		values.put(KEY_STANBYPOWER, contact.getKeyStanByPower());
		values.put(KEY_STANBYPOWER_THRESHOLD, contact.getKeyStanByPower_Threshold());
		values.put(KEY_GASALERT, contact.getKeyGasAlert());
		values.put(KEY_GASALERT_CO, contact.getKeyGasAlertCO());
		values.put(KEY_GASALERT_LPG, contact.getKeyGasAlertLPG());
		values.put(KEY_GASALERT_SMOKE, contact.getKeyGasAlertSMOKE());
		values.put(KEY_FIREALERT, contact.getKeyFireAlert());
		values.put(KEY_FIREALERT_THRESHOLD, contact.getKeyFireAlertThreshold());
		values.put(KEY_VOICEALERT, contact.getKeyVoiceAlert());
		values.put(KEY_LATITUDE, contact.getKeyLatitude());
		values.put(KEY_LONGTITUDE, contact.getKeyLongtitude());
		values.put(KEY_VERSION, contact.getKeyVersion());
		values.put(KEY_FWTIME, contact.getKeyFWtime());

		// 장치 테이블에 추가
		db.insert(TABLE_CONTACTS, null, values);
		// 데이터베이스 커넥션 닫기
		db.close();
	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 장치편집
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public int updateDevice(GetDeviceList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_DEVICE_MAC, contact.getKeyDeviceMac());
//		values.put(KEY_DEVICE_NAME, contact.getKeyDeviceName());
//		values.put(KEY_DEVICE_STATUS, contact.getKeyDeviceStatus());
//		values.put(KEY_STATUS, contact.getKeyStatus());
//		values.put(KEY_SOCKET, contact.getKeySocket());
//		values.put(KEY_SOCKET_NAME, contact.getKeySocketName());
//		values.put(KEY_GPIO, contact.getKeyGPIO());
//		values.put(KEY_SOCKET_IMG, contact.getKeySocketImg());
//		values.put(KEY_SOCKET_ACTIVE, contact.getKeySocketActive());
//		values.put(KEY_WAKEUP, contact.getKeyWakeUp());
//		values.put(KEY_SLEEP, contact.getKeySleep());
//		values.put(KEY_WAKEUPDAY, contact.getKeyWakeUpDay());
//		values.put(KEY_SLEEPDAY, contact.getKeySleepDay());
//		values.put(KEY_DEVICE_WAKEUP, contact.getKeyDevice_WakeUp());
//		values.put(KEY_DEVICE_SLEEP, contact.getKeyDevice_Sleep());
//		values.put(KEY_DEVICE_WAKEUPDAY, contact.getKeyDevice_WakeUpDay());
//		values.put(KEY_DEVICE_SLEEPDAY, contact.getKeyDevice_SleepDay());
//		values.put(KEY_MASTERPLUG, contact.getKeyMasterPlug());
//		values.put(KEY_PLUGCONTROL, contact.getKeyPlugControl());
//		values.put(KEY_PLUGBLOCKER, contact.getKeyPlugBlocker());
//		values.put(KEY_VOICEALERT, contact.getKeyVoiceAlert());
//		values.put(KEY_FIREALERT, contact.getKeyFireAlert());
//		values.put(KEY_FIREALERT_THRESHOLD, contact.getKeyFireAlertThreshold());
//		values.put(KEY_STANBYPOWER, contact.getKeyStanByPower());
//		values.put(KEY_STANBYPOWER_THRESHOLD, contact.getKeyStanByPower_Threshold());
//		values.put(KEY_GASALERT, contact.getKeyGasAlert());
//		values.put(KEY_GASALERT_CO, contact.getKeyGasAlertCO());
//		values.put(KEY_GASALERT_LPG, contact.getKeyGasAlertLPG());
//		values.put(KEY_GASALERT_SMOKE, contact.getKeyGasAlertSMOKE());
//		values.put(KEY_LATITUDE, contact.getKeyLatitude());
//		values.put(KEY_LONGTITUDE, contact.getKeyLongtitude());
//		values.put(KEY_VERSION, contact.getKeyVersion());
//		values.put(KEY_FWTIME, contact.getKeyFWtime());
//		if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "장치편집 성공: " + contact.getKeyDeviceMac());
//
//		// 장치 테이블 편집
//		return db.update(TABLE_CONTACTS, values, KEY_NO + " = ?", new String[] { String.valueOf(contact.getKeyNo()) });
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 장치편집
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	int : 저장 성공여부
//	 */
//	public int updateDevice2(GetDeviceList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_DEVICE_NAME, contact.getKeyDeviceName());
//
//		// 장치 테이블 편집
//		return db.update(TABLE_CONTACTS, values, KEY_DEVICE_MAC + " = ?", new String[] { contact.getKeyDeviceMac() });
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 장치삭제
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void deleteDevice(String device_MAC) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		int success = db.delete(TABLE_CONTACTS, KEY_DEVICE_MAC + " = ?", new String[] { device_MAC });
//		// 데이터베이스 커넥션 닫기
//		db.close();
//
//		if(success == 0) if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "장치삭제 실패: " + device_MAC);
//		else if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "장치삭제 성공: " + device_MAC);
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 장치공유
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	int : 저장 성공여부
//	 */
//	public int updateSharing(GetDeviceList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_STATUS, contact.getKeyStatus());
//
//		// 장치 테이블 편집
//		return db.update(TABLE_CONTACTS, values, KEY_DEVICE_MAC + " = ?", new String[] { contact.getKeyDeviceMac() });
//	}


	/**
	 * All CRUD(Create, Read, Update, Delete) Operation - 장치상태
	 * @param 	contact : 장치 정보
	 * @throws 	None
	 * @return 	None
	 */
	public void updateStatus(GetDeviceList device) {
		SQLiteDatabase db = this.getWritableDatabase();

		String UPDATE_STATUS = "UPDATE " + TABLE_CONTACTS
							+ " SET " 	+ KEY_DEVICE_STATUS + "=" + device.getKeyDeviceStatus()
							+ " WHERE " + KEY_DEVICE_MAC 	+ "=\"" + device.getKeyDeviceMac()	+ "\""
							+ " AND " 	+ KEY_SOCKET		+ "=" + device.getKeySocket();
		db.execSQL(UPDATE_STATUS);
		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "DEVICE STATUS : " + UPDATE_STATUS);
	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 장치상태
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void updateGPIOStatus(GetDeviceList device) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		String UPDATE_STATUS = "UPDATE " + TABLE_CONTACTS
//							+ " SET " 	+ KEY_GPIO 			+ "=" + device.getKeyGPIO()
//							+ " WHERE " + KEY_DEVICE_MAC 	+ "=\"" + device.getKeyDeviceMac()	+ "\""
//							+ " AND " 	+ KEY_SOCKET		+ "=0";
//		db.execSQL(UPDATE_STATUS);
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "GPIO STATUS : " + UPDATE_STATUS);
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - GPIO 수정
//	 * GPIO 상태를 16진수로 받아 소켓별로 저장한다.
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void updateGPIO(GetDeviceList contact) {
//		int[] gpio = new int[4];
//		SQLiteDatabase db = this.getWritableDatabase();
//		int value = contact.getKeyGPIO();
//		int value_remainder = value;
//
//		for(int i=0;i<4;i++){
//			value= (int)(value_remainder / (Math.pow(2,(3-i))));
//			value_remainder%=(Math.pow(2,(3-i)));
//			if (value==1) {
//				gpio[i] = 1;
//			} else {
//				gpio[i] = 0;
//			}
//
//			String UPDATE_GPIO = "UPDATE " + TABLE_CONTACTS
//					+ " SET " + KEY_GPIO + "=" + gpio[i]
//					+ " WHERE " + KEY_DEVICE_MAC + "=\"" + contact.getKeyDeviceMac() + "\""
//					+ " AND " + KEY_SOCKET	+ "=" + (i+1);
//			db.execSQL(UPDATE_GPIO);
//			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "DatabaseHandler - GPIO : " + UPDATE_GPIO);
//		}
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 소켓 추가
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void addSocket(GetDeviceList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_DEVICE_MAC, contact.getKeyDeviceMac());
//		values.put(KEY_SOCKET, contact.getKeySocket());
//		values.put(KEY_SOCKET_NAME, contact.getKeySocketName());
//		values.put(KEY_GPIO, contact.getKeyGPIO());
//		values.put(KEY_SOCKET_IMG, contact.getKeySocketImg());
//		values.put(KEY_SOCKET_ACTIVE, contact.getKeySocketActive());
//
//		// Inserting Row
//		db.insert(TABLE_CONTACTS, null, values);
//		db.close(); // Closing database connection
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 소켓 편집
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public int updateSocket(GetDeviceList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_SOCKET_NAME, contact.getKeySocketName());
//		values.put(KEY_SOCKET_IMG, contact.getKeySocketImg());
//		values.put(KEY_SOCKET_ACTIVE, contact.getKeySocketActive());
//		// local DB dump
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "편집된 소켓: " + contact.getKeySocketName());
//
//		// updating row
//		return db.update(TABLE_CONTACTS, values, KEY_NO + " = ?", new String[] { String.valueOf(contact.getKeyNo()) });
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 꺼지는 시간 저장
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public int updateSleep(GetDeviceList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_DEVICE_MAC, 	contact.getKeyDeviceMac());
//		values.put(KEY_SOCKET, 		contact.getKeySocket());
//		values.put(KEY_SLEEP, 		contact.getKeySleep());
//		values.put(KEY_SLEEPDAY, 	contact.getKeySleepDay());
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "sleepTime: " + contact.getKeySleep());
//
//		// updating row
//		return db.update(TABLE_CONTACTS, values, KEY_NO + " = ?", new String[] { String.valueOf(contact.getKeyNo()) });
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 꺼지는 시간 저장
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public int updateSleep2(GetDeviceList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_SLEEP, 		contact.getKeySleep());
//		values.put(KEY_SLEEPDAY, 	contact.getKeySleepDay());
//		values.put(KEY_DEVICE_SLEEP, 	contact.getKeyDevice_Sleep());
//		values.put(KEY_DEVICE_SLEEPDAY, contact.getKeyDevice_SleepDay());
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "sleepTime: " + contact.getKeySleep());
//
//		// updating row
//		return db.update(TABLE_CONTACTS, values, KEY_DEVICE_MAC + " = ?", new String[] { String.valueOf(contact.getKeyDeviceMac()) });
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 켜지는 시간 저장
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public int updateWakeup(GetDeviceList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_DEVICE_MAC, 	contact.getKeyDeviceMac());
//		values.put(KEY_SOCKET, 		contact.getKeySocket());
//		values.put(KEY_WAKEUP, 		contact.getKeyWakeUp());
//		values.put(KEY_WAKEUPDAY, 	contact.getKeyWakeUpDay());
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "wakeupTime: " + contact.getKeyWakeUp());
//
//		// updating row
//		return db.update(TABLE_CONTACTS, values, KEY_NO + " = ?", new String[] { String.valueOf(contact.getKeyNo()) });
//	}

	/**
	 * shlee
	 * All CRUD(Create, Read, Update, Delete) Operation - 켜지는 시간 저장
	 * @param 	contact : 장치 정보
	 * @throws 	None
	 * @return 	None
	 */
	public int updateWakeup2(GetDeviceList contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
//		values.put(KEY_WAKEUP, 		contact.getKeyWakeUp());
//		values.put(KEY_WAKEUPDAY, 	contact.getKeyWakeUpDay());
		values.put(KEY_DEVICE_WAKEUP, 		contact.getKeyDevice_WakeUp());
		values.put(KEY_DEVICE_WAKEUPDAY, 	contact.getKeyDevice_WakeUpDay());
		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "DatabaseHandler - Update WakeupTime: " + contact.getKeyWakeUp());

		// updating row
		return db.update(TABLE_CONTACTS, values, KEY_DEVICE_MAC + " = ?", new String[] { String.valueOf(contact.getKeyDeviceMac()) });
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operation - 마스터 플러그 수정
	 * @param 	device_Mac : 장치 맥주소
	 * @param 	slave_plugs : 마스터 플러그 설정
	 * @throws 	None
	 * @return 	None
	 */
	public void updateMasterPlug(String device_Mac, int[] slave_plugs) {
		SQLiteDatabase db = this.getWritableDatabase();

		for(int i=0;i<4;i++){
			String UPDATE_MASTERPLUG = "UPDATE " + TABLE_CONTACTS
					+ " SET " + KEY_MASTERPLUG + "=" + slave_plugs[i]
					+ " WHERE " + KEY_DEVICE_MAC + "=\"" + device_Mac + "\""
					+ " AND " + KEY_SOCKET	+ "=" + (i+1);
			db.execSQL(UPDATE_MASTERPLUG);
			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "DatabaseHandler - updateMasterPlug : " + UPDATE_MASTERPLUG);
		}
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operation - 플러그 차단 설정 수정
	 * @param 	device_Mac : 장치 맥주소
	 * @param 	slave_plugs : 마스터 플러그 설정
	 * @throws 	None
	 * @return 	None
	 */
	public void updatePlugBlocker(String device_Mac, ArrayList<Integer> plugs) {
		SQLiteDatabase db = this.getWritableDatabase();

		for(int i=0;i<4;i++){
			String UPDATE_PLUGBLOCKER = "UPDATE " + TABLE_CONTACTS
					+ " SET " + KEY_PLUGBLOCKER + "=" + plugs.get(i)
					+ " WHERE " + KEY_DEVICE_MAC + "=\"" + device_Mac + "\""
					+ " AND " + KEY_SOCKET	+ "=" + (i+1);
			db.execSQL(UPDATE_PLUGBLOCKER);
			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "DatabaseHandler - updatePlugBlocker : " + UPDATE_PLUGBLOCKER);
		}
	}


	/**
	 * All CRUD(Create, Read, Update, Delete) Operation - 대기전력 기준치 저장
	 * @param 	contact : 장치 정보
	 * @throws 	None
	 * @return 	None
	 */
	public int updateStanByPower_Threshold(GetDeviceList contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_DEVICE_MAC, 	contact.getKeyDeviceMac());
		values.put(KEY_SOCKET, 		contact.getKeySocket());
		values.put(KEY_STANBYPOWER_THRESHOLD, contact.getKeyStanByPower_Threshold());

		// updating row
		return db.update(TABLE_CONTACTS, values, KEY_NO + " = ?", new String[] { String.valueOf(contact.getKeyNo()) });
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operation - 음설알람 설정 저장
	 * @param 	contact : 음성 알람
	 * @throws 	None
	 * @return 	None
	 */
	public int updateVoiceAlert(GetDeviceList contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_DEVICE_MAC, 	contact.getKeyDeviceMac());
		values.put(KEY_VOICEALERT, contact.getKeyVoiceAlert());

		// updating row
		return db.update(TABLE_CONTACTS, values, KEY_NO + " = ?", new String[] { String.valueOf(contact.getKeyNo()) });
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operation - 화재알람 설정 여부
	 * @param 	contact : 가스 정보
	 * @throws 	None
	 * @return 	None
	 */
	public int updateFireAlert(GetDeviceList contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_DEVICE_MAC, 	contact.getKeyDeviceMac());
		values.put(KEY_FIREALERT, contact.getKeyFireAlert());

		// updating row
		return db.update(TABLE_CONTACTS, values, KEY_NO + " = ?", new String[] { String.valueOf(contact.getKeyNo()) });
	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 화재알람 기준치 저장
//	 * @param 	contact : 가스 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public int updateFireAlert_Threshold(GetDeviceList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_DEVICE_MAC, 	contact.getKeyDeviceMac());
//		values.put(KEY_FIREALERT_THRESHOLD, contact.getKeyFireAlertThreshold());
//
//		// updating row
//		return db.update(TABLE_CONTACTS, values, KEY_NO + " = ?", new String[] { String.valueOf(contact.getKeyNo()) });
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 가스알람 기준치 저장
//	 * @param 	contact : 가스 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public int updateGasAlert(GetDeviceList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_DEVICE_MAC, 	contact.getKeyDeviceMac());
//		values.put(KEY_GASALERT, contact.getKeyGasAlert());
//		values.put(KEY_GASALERT_CO, contact.getKeyGasAlertCO());
//		values.put(KEY_GASALERT_LPG, contact.getKeyGasAlertLPG());
//		values.put(KEY_GASALERT_SMOKE, contact.getKeyGasAlertSMOKE());
//
//		// updating row
//		return db.update(TABLE_CONTACTS, values, KEY_NO + " = ?", new String[] { String.valueOf(contact.getKeyNo()) });
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 장치목록 초기화
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void deleteDB(int socket) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		int success = db.delete(TABLE_CONTACTS, KEY_SOCKET + " >= ?", new String[] { String.valueOf(socket) });
//		db.close();
//
//		if(success == 0) if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "장치목록 초기화 실패");
//		else if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "장치목록 초기화 성공");
//	}

//	/**
//	 * 마스터 플러그 가져오기
//	 * @param 	device_Mac: 장치 맥주소
//	 * @throws	None
//	 * @return 	int[]: 마스터 플러그
//	 */
//	public int[] getMasterPlug(String device_Mac) {
//		// Select All Query
//		String selectQuery = "SELECT " + KEY_MASTERPLUG + " FROM " + TABLE_CONTACTS +
//							 " WHERE " + KEY_DEVICE_MAC + "=\"" + device_Mac + "\"" +
//							 " AND " + KEY_SOCKET + ">0";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		int data[]= new int[4];
//		try {
//			if (cursor != null) cursor.moveToFirst();
//
//			data[0] = cursor.getInt(0);
//			data[1] = cursor.getInt(1);
//			data[2] = cursor.getInt(2);
//			data[3] = cursor.getInt(3);
//		} finally {
//			cursor.close();
//		}
//		return data;
//	}

//	/**
//	 * 소켓별 대기전력 기준치 가져오기
//	 * @param 	device_Mac: 장치 맥주소
//	 * @param	socket: 소켓 번호
//	 * @return 	int: 대기전력 기준치
//	 */
//	public int getStanByPower_Threshold(String device_Mac, int socket) {
//		// Select All Query
//		String selectQuery = "SELECT " + KEY_STANBYPOWER_THRESHOLD + " FROM " + TABLE_CONTACTS +
//				" WHERE " + KEY_DEVICE_MAC + "=\"" + device_Mac + "\"" +
//				" AND " + KEY_SOCKET + "=" + socket;
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		// looping through all rows and adding to list
//		int stanbyPower=0;
//		try {
//			if (cursor.moveToFirst()) {
//				do {
//					stanbyPower = cursor.getInt(0);
//				} while (cursor.moveToNext());
//			} else {
//				// 테이블이 존재하지 않으므로 알림X
//			}
//		} finally {
//			cursor.close();
//		}
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get StanByPower: " + selectQuery + " --> " + stanbyPower);
//		return stanbyPower;
//	}

//	/**
//	 * 가스별 기준치 가져오기
//	 * @param 	device_Mac: 장치 맥주소
//	 * @return 	GetDeviceList: 가스 정보
//	 */
//	public GetDeviceList getGasAlert(String device_Mac) {
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.query(TABLE_CONTACTS, new String[] {
//				KEY_GASALERT,
//				KEY_GASALERT_CO,
//				KEY_GASALERT_LPG,
//				KEY_GASALERT_SMOKE }, KEY_DEVICE_MAC + "=?",
//				new String[] { String.valueOf(device_Mac) }, null, null, null, null);
//		GetDeviceList account = null;
//		try {
//			if (cursor != null) cursor.moveToFirst();
//
//			account = new GetDeviceList(
//					cursor.getInt(0),
//					cursor.getInt(1),
//					cursor.getInt(2),
//					cursor.getInt(3));
//		} finally {
//			cursor.close();
//		}
//		return account;
//	}

//	/**
//	 * 화재알람 설정 여부 가져오기
//	 * @param 	device_Mac: 장치 맥주소
//	 * @return 	int: 화재 알람 기준치
//	 */
//	public int getFireAlert(String device_Mac) {
//		// Select All Query
//		String selectQuery = "SELECT " + KEY_FIREALERT + " FROM " + TABLE_CONTACTS +
//							 " WHERE " + KEY_DEVICE_MAC + "=\"" + device_Mac + "\"" +
//							 " AND " + KEY_SOCKET + "=0";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		int data=0;
//		try {
//			if (cursor != null) cursor.moveToFirst();
//
//			data = cursor.getInt(0);
//		} finally {
//			cursor.close();
//		}
//		return data;
//	}

//	/**
//	 * 화재알람 기준치 가져오기
//	 * @param 	device_Mac: 장치 맥주소
//	 * @return 	int: 화재 알람 기준치
//	 */
//	public int getFireAlert_Threshold(String device_Mac) {
//		// Select All Query
//		String selectQuery = "SELECT " + KEY_FIREALERT_THRESHOLD + " FROM " + TABLE_CONTACTS +
//							 " WHERE " + KEY_DEVICE_MAC + "=\"" + device_Mac + "\"" +
//							 " AND " + KEY_SOCKET + "=0";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		int data=0;
//		try {
//			if (cursor != null) cursor.moveToFirst();
//
//			data = cursor.getInt(0);
//		} finally {
//			cursor.close();
//		}
//		return data;
//	}

//	/**
//	 * 음성알람 설정 여부 가져오기
//	 * @param 	device_Mac: 장치 맥주소
//	 * @return 	음성알람 설정 여부 (0: OFF / 1: ON)
//	 */
//	public int getVoiceAlert(String device_Mac) {
//		// Select All Query
//		String selectQuery = "SELECT " + KEY_VOICEALERT + " FROM " + TABLE_CONTACTS +
//							 " WHERE " + KEY_DEVICE_MAC + "=\"" + device_Mac + "\"" +
//							 " AND " + KEY_SOCKET + "=0";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		int data=0;
//		try {
//			if (cursor != null) cursor.moveToFirst();
//
//			data = cursor.getInt(0);
//		} finally {
//			cursor.close();
//		}
//		return data;
//	}


	/**
	 * 장치 MAC주소들 가져오기
	 * @param 	None
	 * @return 	ArrayList<String>: 저장된 장치 MAC주소들
	 */
	public ArrayList<String> getDeviceMac() {
		// Select All Query
		String selectQuery = "SELECT " + KEY_DEVICE_MAC + " FROM " + TABLE_CONTACTS +
							 " WHERE " + KEY_SOCKET + "=0";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		ArrayList<String> arr_account = new ArrayList<String>();
		try {
			if (cursor.moveToFirst()) {
				do {
					arr_account.add(cursor.getString(0));
				} while (cursor.moveToNext());
			} else {
				// 테이블이 존재하지 않으므로 알림X
			}
		} finally {
			cursor.close();
		}
		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get DeviceMac: " + selectQuery + " --> " + arr_account.size());
		return arr_account;
	}

//	/**
//	 * 특정 MAC주소의 장치목록 가져오기
//	 * @param 	None
//	 * @return 	GetDeviceList: 장치 목록
//	 */
//	public GetDeviceList getDeviceList(String device_Mac) {
//		// Select All Query
//		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS +
//							 " WHERE " + KEY_DEVICE_MAC + "=\"" + device_Mac + "\"" +
//							 " AND " + KEY_SOCKET + "=0";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		// looping through all rows and adding to list
//		GetDeviceList contact = new GetDeviceList();
//		try {
//			if (cursor.moveToFirst()) {
//				do {
//					contact.setKeyNo(Integer.parseInt(cursor.getString(0)));
//					contact.setKeyDeviceMac(cursor.getString(1));
//					contact.setKeyDeviceName(cursor.getString(2));
//					contact.setKeyDeviceStatus(Integer.parseInt(cursor.getString(3)));
//					contact.setKeyStatus(Integer.parseInt(cursor.getString(4)));
//					contact.setKeySocket(Integer.parseInt(cursor.getString(5)));
//					contact.setKeySocketName(cursor.getString(6));
//					contact.setKeyGPIO(Integer.parseInt(cursor.getString(7)));
//					contact.setKeySocketImg(Integer.parseInt(cursor.getString(8)));
//					contact.setKeySocketActive(Integer.parseInt(cursor.getString(9)));
//					contact.setKeyWakeUp(cursor.getString(10));
//					contact.setKeySleep(cursor.getString(11));
//					contact.setKeyWakeUpDay(cursor.getString(12));
//					contact.setKeySleepDay(cursor.getString(13));
//					contact.setKeyDevice_WakeUp(cursor.getString(14));
//					contact.setKeyDevice_Sleep(cursor.getString(15));
//					contact.setKeyDevice_WakeUpDay(cursor.getString(16));
//					contact.setKeyDevice_SleepDay(cursor.getString(17));
//					contact.setKeyMasterPlug(Integer.parseInt(cursor.getString(18)));
//					contact.setKeyPlugControl(Integer.parseInt(cursor.getString(19)));
//					contact.setKeyPlugBlocker(Integer.parseInt(cursor.getString(20)));
//					contact.setKeyStanByPower(Integer.parseInt(cursor.getString(21)));
//					contact.setKeyStanByPowerThreshold(Integer.parseInt(cursor.getString(22)));
//					contact.setKeyVoiceAlert(Integer.parseInt(cursor.getString(23)));
//					contact.setKeyFireAlert(Integer.parseInt(cursor.getString(24)));
//					contact.setKeyFireAlertThreshold(Integer.parseInt(cursor.getString(25)));
//					contact.setKeyGasAlert(Integer.parseInt(cursor.getString(26)));
//					contact.setKeyGasAlertCO(Integer.parseInt(cursor.getString(27)));
//					contact.setKeyGasAlertLPG(Integer.parseInt(cursor.getString(28)));
//					contact.setKeyGasAlertSMOKE(Integer.parseInt(cursor.getString(29)));
//					contact.setKeyLatitude(Double.parseDouble(cursor.getString(30)));
//					contact.setKeyLongtitude(Double.parseDouble(cursor.getString(31)));
//					contact.setKeyVersion(Integer.parseInt(cursor.getString(32)));
//					contact.setKeyFWtime(cursor.getString(33));
//				} while (cursor.moveToNext());
//			} else {
//				// 테이블이 존재하지 않으므로 알림X
//			}
//		} finally {
//			cursor.close();
//		}
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get DeviceList: " + selectQuery);
//		return contact;
//	}

//	/**
//	 * 특정 MAC주소의 소켓목록 가져오기
//	 * @param 	None
//	 * @return 	ArrayList<GetDeviceList>: 소켓 목록
//	 */
//	public ArrayList<GetDeviceList> getEachDevice(String device_Mac) {
//		// Select All Query
//		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS +
//							 " WHERE " + KEY_DEVICE_MAC + "=\"" + device_Mac + "\"" +
//							 " AND " + KEY_SOCKET + ">0";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		// looping through all rows and adding to list
//		ArrayList<GetDeviceList> arr_account = new ArrayList<GetDeviceList>();
//		try {
//			if (cursor.moveToFirst()) {
//				do {
//					GetDeviceList contact = new GetDeviceList();
//					contact.setKeyNo(Integer.parseInt(cursor.getString(0)));
//					contact.setKeyDeviceMac(cursor.getString(1));
//					contact.setKeyDeviceName(cursor.getString(2));
//					contact.setKeyDeviceStatus(Integer.parseInt(cursor.getString(3)));
//					contact.setKeyStatus(Integer.parseInt(cursor.getString(4)));
//					contact.setKeySocket(Integer.parseInt(cursor.getString(5)));
//					contact.setKeySocketName(cursor.getString(6));
//					contact.setKeyGPIO(Integer.parseInt(cursor.getString(7)));
//					contact.setKeySocketImg(Integer.parseInt(cursor.getString(8)));
//					contact.setKeySocketActive(Integer.parseInt(cursor.getString(9)));
//					contact.setKeyWakeUp(cursor.getString(10));
//					contact.setKeySleep(cursor.getString(11));
//					contact.setKeyWakeUpDay(cursor.getString(12));
//					contact.setKeySleepDay(cursor.getString(13));
//					contact.setKeyDevice_WakeUp(cursor.getString(14));
//					contact.setKeyDevice_Sleep(cursor.getString(15));
//					contact.setKeyDevice_WakeUpDay(cursor.getString(16));
//					contact.setKeyDevice_SleepDay(cursor.getString(17));
//					contact.setKeyMasterPlug(Integer.parseInt(cursor.getString(18)));
//					contact.setKeyPlugControl(Integer.parseInt(cursor.getString(19)));
//					contact.setKeyPlugBlocker(Integer.parseInt(cursor.getString(20)));
//					contact.setKeyStanByPower(Integer.parseInt(cursor.getString(21)));
//					contact.setKeyStanByPowerThreshold(Integer.parseInt(cursor.getString(22)));
//					contact.setKeyVoiceAlert(Integer.parseInt(cursor.getString(23)));
//					contact.setKeyFireAlert(Integer.parseInt(cursor.getString(24)));
//					contact.setKeyFireAlertThreshold(Integer.parseInt(cursor.getString(25)));
//					contact.setKeyGasAlert(Integer.parseInt(cursor.getString(26)));
//					contact.setKeyGasAlertCO(Integer.parseInt(cursor.getString(27)));
//					contact.setKeyGasAlertLPG(Integer.parseInt(cursor.getString(28)));
//					contact.setKeyGasAlertSMOKE(Integer.parseInt(cursor.getString(29)));
//					contact.setKeyLatitude(Double.parseDouble(cursor.getString(30)));
//					contact.setKeyLongtitude(Double.parseDouble(cursor.getString(31)));
//					contact.setKeyVersion(Integer.parseInt(cursor.getString(32)));
//					contact.setKeyFWtime(cursor.getString(33));
//					arr_account.add(contact);
//				} while (cursor.moveToNext());
//			} else {
//				// 테이블이 존재하지 않으므로 알림X
//			}
//		} finally {
//			cursor.close();
//		}
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get SocketeList: " + selectQuery + " --> " + arr_account.size());
//		return arr_account;
//	}

//	/**
//	 * 예약설정 장치목록 가져오기
//	 * @param 	None
//	 * @return 	ArrayList<GetDeviceList>: 장치 목록들
//	 */
//	public ArrayList<GetDeviceList> getSleepDevice() {
//		// Select All Query
//		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS +
//							 " WHERE " + KEY_SOCKET + ">0";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		// looping through all rows and adding to list
//		ArrayList<GetDeviceList> arr_account = new ArrayList<GetDeviceList>();
//		try {
//			if (cursor.moveToFirst()) {
//				do {
//					GetDeviceList contact = new GetDeviceList();
//					contact.setKeyNo(Integer.parseInt(cursor.getString(0)));
//					contact.setKeyDeviceMac(cursor.getString(1));
//					contact.setKeyDeviceName(cursor.getString(2));
//					contact.setKeyDeviceStatus(Integer.parseInt(cursor.getString(3)));
//					contact.setKeyStatus(Integer.parseInt(cursor.getString(4)));
//					contact.setKeySocket(Integer.parseInt(cursor.getString(5)));
//					contact.setKeySocketName(cursor.getString(6));
//					contact.setKeyGPIO(Integer.parseInt(cursor.getString(7)));
//					contact.setKeySocketImg(Integer.parseInt(cursor.getString(8)));
//					contact.setKeySocketActive(Integer.parseInt(cursor.getString(9)));
//					contact.setKeyWakeUp(cursor.getString(10));
//					contact.setKeySleep(cursor.getString(11));
//					contact.setKeyWakeUpDay(cursor.getString(12));
//					contact.setKeySleepDay(cursor.getString(13));
//					contact.setKeyDevice_WakeUp(cursor.getString(14));
//					contact.setKeyDevice_Sleep(cursor.getString(15));
//					contact.setKeyDevice_WakeUpDay(cursor.getString(16));
//					contact.setKeyDevice_SleepDay(cursor.getString(17));
//					contact.setKeyMasterPlug(Integer.parseInt(cursor.getString(18)));
//					contact.setKeyPlugControl(Integer.parseInt(cursor.getString(19)));
//					contact.setKeyPlugBlocker(Integer.parseInt(cursor.getString(20)));
//					contact.setKeyStanByPower(Integer.parseInt(cursor.getString(21)));
//					contact.setKeyStanByPowerThreshold(Integer.parseInt(cursor.getString(22)));
//					contact.setKeyVoiceAlert(Integer.parseInt(cursor.getString(23)));
//					contact.setKeyFireAlert(Integer.parseInt(cursor.getString(24)));
//					contact.setKeyFireAlertThreshold(Integer.parseInt(cursor.getString(25)));
//					contact.setKeyGasAlert(Integer.parseInt(cursor.getString(26)));
//					contact.setKeyGasAlertCO(Integer.parseInt(cursor.getString(27)));
//					contact.setKeyGasAlertLPG(Integer.parseInt(cursor.getString(28)));
//					contact.setKeyGasAlertSMOKE(Integer.parseInt(cursor.getString(29)));
//					contact.setKeyLatitude(Double.parseDouble(cursor.getString(30)));
//					contact.setKeyLongtitude(Double.parseDouble(cursor.getString(31)));
//					contact.setKeyVersion(Integer.parseInt(cursor.getString(32)));
//					contact.setKeyFWtime(cursor.getString(33));
//					arr_account.add(contact);
//				} while (cursor.moveToNext());
//			} else {
//				// 테이블이 존재하지 않으므로 알림X
//			}
//		} finally {
//			cursor.close();
//		}
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get SleepList: " + selectQuery + " --> " + arr_account.size());
//		return arr_account;
//	}

//	/**
//	 * 공유한 장치목록 가져오기
//	 * @param 	None
//	 * @return 	ArrayList<GetDeviceList>: 장치 목록들
//	 */
//	public ArrayList<GetDeviceList> getSharingDevice() {
//		// Select All Query
//		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS +
//							 " WHERE " + KEY_SOCKET + "=0" +
//							 " AND " + KEY_STATUS + "<3";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		// looping through all rows and adding to list
//		ArrayList<GetDeviceList> arr_account = new ArrayList<GetDeviceList>();
//		try {
//			if (cursor.moveToFirst()) {
//				do {
//					GetDeviceList contact = new GetDeviceList();
//					contact.setKeyNo(Integer.parseInt(cursor.getString(0)));
//					contact.setKeyDeviceMac(cursor.getString(1));
//					contact.setKeyDeviceName(cursor.getString(2));
//					contact.setKeyDeviceStatus(Integer.parseInt(cursor.getString(3)));
//					contact.setKeyStatus(Integer.parseInt(cursor.getString(4)));
//					contact.setKeySocket(Integer.parseInt(cursor.getString(5)));
//					contact.setKeySocketName(cursor.getString(6));
//					contact.setKeyGPIO(Integer.parseInt(cursor.getString(7)));
//					contact.setKeySocketImg(Integer.parseInt(cursor.getString(8)));
//					contact.setKeySocketActive(Integer.parseInt(cursor.getString(9)));
//					contact.setKeyWakeUp(cursor.getString(10));
//					contact.setKeySleep(cursor.getString(11));
//					contact.setKeyWakeUpDay(cursor.getString(12));
//					contact.setKeySleepDay(cursor.getString(13));
//					contact.setKeyDevice_WakeUp(cursor.getString(14));
//					contact.setKeyDevice_Sleep(cursor.getString(15));
//					contact.setKeyDevice_WakeUpDay(cursor.getString(16));
//					contact.setKeyDevice_SleepDay(cursor.getString(17));
//					contact.setKeyMasterPlug(Integer.parseInt(cursor.getString(18)));
//					contact.setKeyPlugControl(Integer.parseInt(cursor.getString(19)));
//					contact.setKeyPlugBlocker(Integer.parseInt(cursor.getString(20)));
//					contact.setKeyStanByPower(Integer.parseInt(cursor.getString(21)));
//					contact.setKeyStanByPowerThreshold(Integer.parseInt(cursor.getString(22)));
//					contact.setKeyVoiceAlert(Integer.parseInt(cursor.getString(23)));
//					contact.setKeyFireAlert(Integer.parseInt(cursor.getString(24)));
//					contact.setKeyFireAlertThreshold(Integer.parseInt(cursor.getString(25)));
//					contact.setKeyGasAlert(Integer.parseInt(cursor.getString(26)));
//					contact.setKeyGasAlertCO(Integer.parseInt(cursor.getString(27)));
//					contact.setKeyGasAlertLPG(Integer.parseInt(cursor.getString(28)));
//					contact.setKeyGasAlertSMOKE(Integer.parseInt(cursor.getString(29)));
//					contact.setKeyLatitude(Double.parseDouble(cursor.getString(30)));
//					contact.setKeyLongtitude(Double.parseDouble(cursor.getString(31)));
//					contact.setKeyVersion(Integer.parseInt(cursor.getString(32)));
//					contact.setKeyFWtime(cursor.getString(33));
//					arr_account.add(contact);
//				} while (cursor.moveToNext());
//			} else {
//				// 테이블이 존재하지 않으므로 알림X
//			}
//		} finally {
//			cursor.close();
//		}
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get SharingList: " + selectQuery + " --> " + arr_account.size());
//		return arr_account;
//	}

//	/**
//	 * 공유받은 장치목록 가져오기
//	 * @param 	None
//	 * @return 	ArrayList<GetDeviceList>: 장치 목록들
//	 */
//	public ArrayList<GetDeviceList> getSharedDevice() {
//		// Select All Query
//		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS +
//							 " WHERE " + KEY_SOCKET + "=0" +
//							 " AND " + KEY_STATUS + "=3";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		// looping through all rows and adding to list
//		ArrayList<GetDeviceList> arr_account = new ArrayList<GetDeviceList>();
//		try {
//			if (cursor.moveToFirst()) {
//				do {
//					GetDeviceList contact = new GetDeviceList();
//					contact.setKeyNo(Integer.parseInt(cursor.getString(0)));
//					contact.setKeyDeviceMac(cursor.getString(1));
//					contact.setKeyDeviceName(cursor.getString(2));
//					contact.setKeyDeviceStatus(Integer.parseInt(cursor.getString(3)));
//					contact.setKeyStatus(Integer.parseInt(cursor.getString(4)));
//					contact.setKeySocket(Integer.parseInt(cursor.getString(5)));
//					contact.setKeySocketName(cursor.getString(6));
//					contact.setKeyGPIO(Integer.parseInt(cursor.getString(7)));
//					contact.setKeySocketImg(Integer.parseInt(cursor.getString(8)));
//					contact.setKeySocketActive(Integer.parseInt(cursor.getString(9)));
//					contact.setKeyWakeUp(cursor.getString(10));
//					contact.setKeySleep(cursor.getString(11));
//					contact.setKeyWakeUpDay(cursor.getString(12));
//					contact.setKeySleepDay(cursor.getString(13));
//					contact.setKeyDevice_WakeUp(cursor.getString(14));
//					contact.setKeyDevice_Sleep(cursor.getString(15));
//					contact.setKeyDevice_WakeUpDay(cursor.getString(16));
//					contact.setKeyDevice_SleepDay(cursor.getString(17));
//					contact.setKeyMasterPlug(Integer.parseInt(cursor.getString(18)));
//					contact.setKeyPlugControl(Integer.parseInt(cursor.getString(19)));
//					contact.setKeyPlugBlocker(Integer.parseInt(cursor.getString(20)));
//					contact.setKeyStanByPower(Integer.parseInt(cursor.getString(21)));
//					contact.setKeyStanByPowerThreshold(Integer.parseInt(cursor.getString(22)));
//					contact.setKeyVoiceAlert(Integer.parseInt(cursor.getString(23)));
//					contact.setKeyFireAlert(Integer.parseInt(cursor.getString(24)));
//					contact.setKeyFireAlertThreshold(Integer.parseInt(cursor.getString(25)));
//					contact.setKeyGasAlert(Integer.parseInt(cursor.getString(26)));
//					contact.setKeyGasAlertCO(Integer.parseInt(cursor.getString(27)));
//					contact.setKeyGasAlertLPG(Integer.parseInt(cursor.getString(28)));
//					contact.setKeyGasAlertSMOKE(Integer.parseInt(cursor.getString(29)));
//					contact.setKeyLatitude(Double.parseDouble(cursor.getString(30)));
//					contact.setKeyLongtitude(Double.parseDouble(cursor.getString(31)));
//					contact.setKeyVersion(Integer.parseInt(cursor.getString(32)));
//					contact.setKeyFWtime(cursor.getString(33));
//					arr_account.add(contact);
//				} while (cursor.moveToNext());
//			} else {
//				// 테이블이 존재하지 않으므로 알림X
//			}
//		} finally {
//			cursor.close();
//		}
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get SharedList: " + selectQuery + " --> " + arr_account.size());
//		return arr_account;
//	}

	/**
	 * Getting multiple contact - 모든 장치목록 불러오기
	 * @param 	None
	 * @return 	ArrayList<GetDeviceList> : 저장된 장치목록들
	 */
	public ArrayList<GetDeviceList> getAllDevice() {
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS +
							 " WHERE " + KEY_SOCKET + "=0";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		ArrayList<GetDeviceList> arr_account = new ArrayList<GetDeviceList>();
		try {
			if (cursor.moveToFirst()) {
				do {
					GetDeviceList contact = new GetDeviceList();
					contact.setKeyNo(Integer.parseInt(cursor.getString(0)));
					contact.setKeyDeviceMac(cursor.getString(1));
					contact.setKeyDeviceName(cursor.getString(2));
					contact.setKeyDeviceStatus(Integer.parseInt(cursor.getString(3)));
					contact.setKeyStatus(Integer.parseInt(cursor.getString(4)));
					contact.setKeySocket(Integer.parseInt(cursor.getString(5)));
					contact.setKeySocketName(cursor.getString(6));
					contact.setKeyGPIO(Integer.parseInt(cursor.getString(7)));
					contact.setKeySocketImg(Integer.parseInt(cursor.getString(8)));
					contact.setKeySocketActive(Integer.parseInt(cursor.getString(9)));
					contact.setKeyWakeUp(cursor.getString(10));
					contact.setKeySleep(cursor.getString(11));
					contact.setKeyWakeUpDay(cursor.getString(12));
					contact.setKeySleepDay(cursor.getString(13));
					contact.setKeyDevice_WakeUp(cursor.getString(14));
					contact.setKeyDevice_Sleep(cursor.getString(15));
					contact.setKeyDevice_WakeUpDay(cursor.getString(16));
					contact.setKeyDevice_SleepDay(cursor.getString(17));
					contact.setKeyMasterPlug(Integer.parseInt(cursor.getString(18)));
					contact.setKeyPlugControl(Integer.parseInt(cursor.getString(19)));
					contact.setKeyPlugBlocker(Integer.parseInt(cursor.getString(20)));
					contact.setKeyStanByPower(Integer.parseInt(cursor.getString(21)));
					contact.setKeyStanByPowerThreshold(Integer.parseInt(cursor.getString(22)));
					contact.setKeyVoiceAlert(Integer.parseInt(cursor.getString(23)));
					contact.setKeyFireAlert(Integer.parseInt(cursor.getString(24)));
					contact.setKeyFireAlertThreshold(Integer.parseInt(cursor.getString(25)));
					contact.setKeyGasAlert(Integer.parseInt(cursor.getString(26)));
					contact.setKeyGasAlertCO(Integer.parseInt(cursor.getString(27)));
					contact.setKeyGasAlertLPG(Integer.parseInt(cursor.getString(28)));
					contact.setKeyGasAlertSMOKE(Integer.parseInt(cursor.getString(29)));
					contact.setKeyLatitude(Double.parseDouble(cursor.getString(30)));
					contact.setKeyLongtitude(Double.parseDouble(cursor.getString(31)));
					contact.setKeyVersion(Integer.parseInt(cursor.getString(32)));
					contact.setKeyFWtime(cursor.getString(33));
					arr_account.add(contact);
				} while (cursor.moveToNext());
			} else {
				// 테이블이 존재하지 않으므로 알림X
			}
		} finally {
			cursor.close();
		}
		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get AllDevice : " + selectQuery + " --> " + arr_account.size());

		//20170513 yjjeon : NullPointException 예외 처리
		if(arr_account == null)
			arr_account = new ArrayList<GetDeviceList>();

		return arr_account;
	}

	/**
	 * Getting single contact - 개별 장치목록 가져오기
	 * @param 	id : 목록 번호
	 * @return 	GetDeviceList: 장치목록
	 */
	public GetDeviceList GetDevice(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CONTACTS, new String[] {
				KEY_NO,
				KEY_DEVICE_MAC,
				KEY_DEVICE_NAME,
				KEY_DEVICE_STATUS,
				KEY_STATUS,
				KEY_SOCKET,
				KEY_SOCKET_NAME,
				KEY_GPIO,
				KEY_SOCKET_IMG,
				KEY_SOCKET_ACTIVE,
				KEY_WAKEUP,
				KEY_SLEEP,
				KEY_WAKEUPDAY,
				KEY_SLEEPDAY,
				KEY_DEVICE_WAKEUP,
				KEY_DEVICE_SLEEP,
				KEY_DEVICE_WAKEUPDAY,
				KEY_DEVICE_SLEEPDAY,
				KEY_MASTERPLUG,
				KEY_PLUGCONTROL,
				KEY_PLUGBLOCKER,
				KEY_STANBYPOWER,
				KEY_STANBYPOWER_THRESHOLD,
				KEY_VOICEALERT,
				KEY_FIREALERT,
				KEY_FIREALERT_THRESHOLD,
				KEY_GASALERT,
				KEY_GASALERT_CO,
				KEY_GASALERT_LPG,
				KEY_GASALERT_SMOKE,
				KEY_LATITUDE,
				KEY_LONGTITUDE,
				KEY_VERSION,
				KEY_FWTIME }, KEY_NO + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		GetDeviceList account = null;
		try {
			if (cursor != null) cursor.moveToFirst();

			account = new GetDeviceList(
					Integer.parseInt(cursor.getString(0)),
					cursor.getString(1),
					cursor.getString(2),
					Integer.parseInt(cursor.getString(3)),
					Integer.parseInt(cursor.getString(4)),
					Integer.parseInt(cursor.getString(5)),
					cursor.getString(6),
					Integer.parseInt(cursor.getString(7)),
					Integer.parseInt(cursor.getString(8)),
					Integer.parseInt(cursor.getString(9)),
					cursor.getString(10),
					cursor.getString(11),
					cursor.getString(12),
					cursor.getString(13),
					cursor.getString(14),
					cursor.getString(15),
					cursor.getString(16),
					cursor.getString(17),
					Integer.parseInt(cursor.getString(18)),
					Integer.parseInt(cursor.getString(19)),
					Integer.parseInt(cursor.getString(20)),
					Integer.parseInt(cursor.getString(21)),
					Integer.parseInt(cursor.getString(22)),
					Integer.parseInt(cursor.getString(23)),
					Integer.parseInt(cursor.getString(24)),
					Integer.parseInt(cursor.getString(25)),
					Integer.parseInt(cursor.getString(26)),
					Integer.parseInt(cursor.getString(27)),
					Integer.parseInt(cursor.getString(28)),
					Integer.parseInt(cursor.getString(29)),
					Double.parseDouble(cursor.getString(30)),
					Double.parseDouble(cursor.getString(31)),
					Integer.parseInt(cursor.getString(32)),
					cursor.getString(33));
		} finally {
			cursor.close();
		}
		return account;
	}

	/**
	 * 현재 테이블에 저장된 장치목록 갯수 가져오기
	 * @return int : 장치목록 갯수
	 */
	public int GetTotalCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		return cursor.getCount();
	}



//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 장치목록 초기화
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void deleteRouterDB() {
//		SQLiteDatabase db = this.getWritableDatabase();
//		int success = db.delete(TABLE_ROUTER, KEY_ROUTER_NO + " >= ?", new String[] { String.valueOf(0) });
//		db.close();
//
//		if(success == 0) {
//			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "공유기 목록 초기화 실패");
//		} else {
//			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "공유기 목록 초기화 성공");
//		}
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 공유기 추가
//	 * @param 	contact : 공유기 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void addRouter(GetRouterList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_ROUTER_HOSTNAME, contact.getKeyHostName());
//		values.put(KEY_ROUTER_IPADDR, contact.getKeyIPAddr());
//		values.put(KEY_ROUTER_MACADDR, contact.getKeyMACAddr());
//		values.put(KEY_ROUTER_EXPIRES, contact.getKeyExpires());
//
//		// 장치 테이블에 추가
//		int status = (int) db.insert(TABLE_ROUTER, null, values);
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, status+"");
//		// 데이터베이스 커넥션 닫기
//		db.close();
//	}

//	/**
//	 * Getting multiple contact - 모든 공유기목록 불러오기
//	 * @param 	None
//	 * @return 	ArrayList<GetDeviceList> : 저장된 장치목록들
//	 */
//	public ArrayList<GetRouterList> getAllRouter() {
//		// Select All Query
//		String selectQuery = "SELECT * FROM " + TABLE_ROUTER;
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		// looping through all rows and adding to list
//		ArrayList<GetRouterList> arr_account = new ArrayList<GetRouterList>();
//		try {
//			if (cursor.moveToFirst()) {
//				do {
//					GetRouterList contact = new GetRouterList();
//					contact.setKeyNo(Integer.parseInt(cursor.getString(0)));
//					contact.setKeyHostName(cursor.getString(1));
//					contact.setKeyIPAddr(cursor.getString(2));
//					contact.setKeyMACAddr(cursor.getString(3));
//					contact.setKeyExpires(cursor.getString(4));
//					arr_account.add(contact);
//				} while (cursor.moveToNext());
//			} else {
//				// 테이블이 존재하지 않으므로 알림X
//			}
//		} finally {
//			cursor.close();
//		}
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get AllRouter : " + selectQuery + " --> " + arr_account.size());
//
//		return arr_account;
//	}



	/**
	 * All CRUD(Create, Read, Update, Delete) Operation - 장치목록 초기화
	 * @param 	contact : 장치 정보
	 * @throws 	None
	 * @return 	None
	 */
	public void deleteGroupDB() {
		SQLiteDatabase db = this.getWritableDatabase();
		int success = db.delete(TABLE_GROUP, KEY_GROUP_NO + " >= ?", new String[] { String.valueOf(0) });
		db.close();

		if(success == 0) {
			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "group - 그룹 목록 초기화 실패");
		} else {
			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "group - 그룹 목록 초기화 성공");
		}
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operation - 그룹 추가
	 * @param 	contact : 그룹 정보
	 * @throws 	None
	 * @return 	None
	 */
	public void addGroup(GetGroupList contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_GROUP_NAME, contact.getKeyGroupName());
		values.put(KEY_GROUP_DEVICES, contact.getKeyGroupDevices());
		values.put(KEY_GROUP_STATUS, contact.getKeyGroupStatus());
		values.put(KEY_GROUP_SLEEPTIME, contact.getKeyGroupSleepTime());
		values.put(KEY_GROUP_WAKEUPTIME, contact.getKeyGroupWakeupTime());
		values.put(KEY_GROUP_SLEEPTIME_DAY, contact.getKeyGroupSleepDay());
		values.put(KEY_GROUP_WAKEUPTIME_DAY, contact.getKeyGroupWakeupDay());
		values.put(KEY_GROUP_INSOCKET, contact.getKeyGroupInSocket());
		values.put(KEY_GROUP_OUTSOCKET, contact.getKeyGroupOutSocket());
		values.put(KEY_GROUP_USER_ID, contact.getKeyUserID());

		// 그룹 테이블에 추가
		int status = (int) db.insert(TABLE_GROUP, null, values);
		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "CustomListAdapter_Group - 그룹 추가: " + status);
		// 데이터베이스 커넥션 닫기
		db.close();
	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 그룹 편집
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public int updateGroup(GetGroupList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_GROUP_NAME, 		contact.getKeyGroupName());
//		values.put(KEY_GROUP_STATUS, 	contact.getKeyGroupStatus());
//		values.put(KEY_GROUP_DEVICES, 	contact.getKeyGroupDevices());
//		values.put(KEY_GROUP_SLEEPTIME, contact.getKeyGroupSleepTime());
//		values.put(KEY_GROUP_WAKEUPTIME, contact.getKeyGroupWakeupTime());
//		values.put(KEY_GROUP_SLEEPTIME_DAY, contact.getKeyGroupSleepDay());
//		values.put(KEY_GROUP_WAKEUPTIME_DAY, contact.getKeyGroupWakeupDay());
//		values.put(KEY_GROUP_INSOCKET, contact.getKeyGroupInSocket());
//		values.put(KEY_GROUP_OUTSOCKET, contact.getKeyGroupOutSocket());
//		values.put(KEY_GROUP_USER_ID, contact.getKeyUserID());
//		if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "CustomListAdapter_Group - 그룹 편집: " + contact.getKeyGroupStatus());
//
//		// 장치 테이블 편집
//		return db.update(TABLE_GROUP, values, KEY_GROUP_NO + " = ?", new String[] { String.valueOf(contact.getKeyNo()) });
//	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operation - 그룹 편집
	 * @param 	contact : 장치 정보
	 * @throws 	None
	 * @return 	None
	 */
	public int updateGroup2(GetGroupList contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_GROUP_NAME, 		contact.getKeyGroupName());
		values.put(KEY_GROUP_DEVICES, 	contact.getKeyGroupDevices());
		values.put(KEY_GROUP_STATUS, 	contact.getKeyGroupStatus());
		values.put(KEY_GROUP_SLEEPTIME, contact.getKeyGroupSleepTime());
		values.put(KEY_GROUP_WAKEUPTIME, contact.getKeyGroupWakeupTime());
		values.put(KEY_GROUP_SLEEPTIME_DAY, contact.getKeyGroupSleepDay());
		values.put(KEY_GROUP_WAKEUPTIME_DAY, contact.getKeyGroupWakeupDay());
		values.put(KEY_GROUP_INSOCKET, contact.getKeyGroupInSocket());
		values.put(KEY_GROUP_OUTSOCKET, contact.getKeyGroupOutSocket());
		values.put(KEY_GROUP_USER_ID, contact.getKeyUserID());
		if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "CustomListAdapter_Group - 그룹 편집: " + contact.getKeyGroupStatus());

		// 장치 테이블 편집
		return db.update(TABLE_GROUP, values, KEY_GROUP_NAME + " = ?", new String[] { String.valueOf(contact.getKeyGroupName()) });
	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 그룹 삭제
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void deleteGroup(GetGroupList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		int success = db.delete(TABLE_GROUP, KEY_GROUP_NO + " = ?", new String[] { String.valueOf(contact.getKeyNo()) });
//		// 데이터베이스 커넥션 닫기
//		db.close();
//
//		if(success == 0) if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "CustomListAdapter_Group - 그룹삭제 실패: " + contact.getKeyGroupName());
//		else if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "CustomListAdapter_Group - 그룹삭제 성공: " + contact.getKeyGroupName());
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 아이디 내 그룹 삭제
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void deleteAllGroup(String userID) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		int success = db.delete(TABLE_GROUP, KEY_GROUP_USER_ID + " = ?", new String[] { userID });
//		// 데이터베이스 커넥션 닫기
//		db.close();
//
//		if(success == 0) if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "CustomListAdapter_Group - 그룹삭제 실패: " + userID);
//		else if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "CustomListAdapter_Group - 그룹삭제 성공: " + userID);
//	}

	/**
	 * Getting multiple contact - 모든 그룹목록 불러오기
	 * @param 	None
	 * @return 	ArrayList<GetDeviceList> : 저장된 그룹목록들
	 */
	public ArrayList<GetGroupList> getAllGroup(String user_ID) {
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_GROUP +
				" WHERE " + KEY_GROUP_USER_ID + "=\"" + user_ID + "\"";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		ArrayList<GetGroupList> arr_account = new ArrayList<GetGroupList>();
		try {
			if (cursor.moveToFirst()) {
				do {
					GetGroupList contact = new GetGroupList();
					contact.setKeyNo(Integer.parseInt(cursor.getString(0)));
					contact.setKeyGroupName(cursor.getString(1));
					contact.setKeyGroupDevices(cursor.getString(2));
					contact.setKeyGroupStatus(cursor.getInt(3));
					contact.setKeyGroupSleepTime(cursor.getString(4));
					contact.setKeyGroupWakeupTime(cursor.getString(5));
					contact.setKeyGroupSleepDay(cursor.getString(6));
					contact.setKeyGroupWakeupDay(cursor.getString(7));
					contact.setKeyGroupInSocket(cursor.getInt(8));
					contact.setKeyGroupOutSocket(cursor.getInt(9));
					contact.setKeyUserID(cursor.getString(10));
					arr_account.add(contact);
					// how to dump local DB ksjung20170814
					Log.d(AppConst.TAG, "Get GroupStatus: "+ contact.getKeyGroupStatus());
				} while (cursor.moveToNext());
			} else {
				// 테이블이 존재하지 않으므로 알림X
			}
		} finally {
			cursor.close();
		}
		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get AllGroup : " + selectQuery + " --> " + arr_account.size());

		//20170513 yjjeon : NullPointException 예외처리
		if(arr_account == null)
			arr_account = new ArrayList<GetGroupList>();

		return arr_account;
	}

//	/**
//	 * 특정 MAC주소의 소켓목록 가져오기
//	 * @param 	None
//	 * @return 	ArrayList<GetDeviceList>: 소켓 목록
//	 */
//	public ArrayList<GetDeviceList> getGroupDevices(GetGroupList group) {
//		ArrayList<GetDeviceList> arr_group = new ArrayList<GetDeviceList>();
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		String[] devices = GroupActivity.convertStringToArray(group.getKeyGroupDevices());
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get GroupList: String[]-" + devices.length);
//
//		for (int i=0; i<devices.length; i++ ) {
//			// Select All Query
//			String selectQuery = "SELECT * FROM " + TABLE_CONTACTS +
//								 " WHERE " + KEY_DEVICE_MAC + "=\"" + devices[i] + "\"" +
//								 " AND " + KEY_SOCKET + "=0";
//			Cursor cursor = db.rawQuery(selectQuery, null);
//			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get GroupList: Device Mac-" + devices[i]);
//			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get GroupList: " + selectQuery);
//
//			// looping through all rows and adding to list
//			GetDeviceList contact = new GetDeviceList();
//			try {
//				if (cursor.moveToFirst()) {
//					do {
//						contact.setKeyNo(Integer.parseInt(cursor.getString(0)));
//						contact.setKeyDeviceMac(cursor.getString(1));
//						contact.setKeyDeviceName(cursor.getString(2));
//						contact.setKeyDeviceStatus(Integer.parseInt(cursor.getString(3)));
//						contact.setKeyStatus(Integer.parseInt(cursor.getString(4)));
//						contact.setKeySocket(Integer.parseInt(cursor.getString(5)));
//						contact.setKeySocketName(cursor.getString(6));
//						contact.setKeyGPIO(Integer.parseInt(cursor.getString(7)));
//						contact.setKeySocketImg(Integer.parseInt(cursor.getString(8)));
//						contact.setKeySocketActive(Integer.parseInt(cursor.getString(9)));
//						contact.setKeyWakeUp(cursor.getString(10));
//						contact.setKeySleep(cursor.getString(11));
//						contact.setKeyWakeUpDay(cursor.getString(12));
//						contact.setKeySleepDay(cursor.getString(13));
//						contact.setKeyDevice_WakeUp(cursor.getString(14));
//						contact.setKeyDevice_Sleep(cursor.getString(15));
//						contact.setKeyDevice_WakeUpDay(cursor.getString(16));
//						contact.setKeyDevice_SleepDay(cursor.getString(17));
//						contact.setKeyMasterPlug(Integer.parseInt(cursor.getString(18)));
//						contact.setKeyPlugControl(Integer.parseInt(cursor.getString(19)));
//						contact.setKeyPlugBlocker(Integer.parseInt(cursor.getString(20)));
//						contact.setKeyStanByPower(Integer.parseInt(cursor.getString(21)));
//						contact.setKeyStanByPowerThreshold(Integer.parseInt(cursor.getString(22)));
//						contact.setKeyVoiceAlert(Integer.parseInt(cursor.getString(23)));
//						contact.setKeyFireAlert(Integer.parseInt(cursor.getString(24)));
//						contact.setKeyFireAlertThreshold(Integer.parseInt(cursor.getString(25)));
//						contact.setKeyGasAlert(Integer.parseInt(cursor.getString(26)));
//						contact.setKeyGasAlertCO(Integer.parseInt(cursor.getString(27)));
//						contact.setKeyGasAlertLPG(Integer.parseInt(cursor.getString(28)));
//						contact.setKeyGasAlertSMOKE(Integer.parseInt(cursor.getString(29)));
//						contact.setKeyLatitude(Double.parseDouble(cursor.getString(30)));
//						contact.setKeyLongtitude(Double.parseDouble(cursor.getString(31)));
//						contact.setKeyVersion(Integer.parseInt(cursor.getString(32)));
//						contact.setKeyFWtime(cursor.getString(33));
//					} while (cursor.moveToNext());
//				} else {
//					// 테이블이 존재하지 않으므로 알림X
//				}
//
//				if(cursor.getCount()>0 && !contact.getKeyDeviceName().equals("")) {
//					arr_group.add(contact);
//				} else {
//					// 등록된 장치가 없으므로 장치 반환X
//				}
//			} finally {
//				cursor.close();
//			}
//		}
//		return arr_group;
//	}

//	/**
//	 * 특정 MAC주소의 소켓목록 가져오기
//	 * @param 	None
//	 * @return 	ArrayList<GetDeviceList>: 소켓 목록
//	 */
//	public ArrayList<GetDeviceList> getGroupSocket(GetGroupList group) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		String[] devices = GroupActivity.convertStringToArray(group.getKeyGroupDevices());
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SleepGroupActivity - Get GroupList: String[]-" + devices.length);
//
//		ArrayList<String> devices_checked = new ArrayList<String>(Arrays.asList(devices));
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SleepGroupActivity - 장치 수: " + devices_checked.size());
//
//		ArrayList<GetDeviceList> arr_group = new ArrayList<GetDeviceList>();
//		for(int i=0; i<devices_checked.size(); i++) {
//			// Select All Query
//			String[] device_check = devices_checked.get(i).split("/");
//			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SleepGroupActivity - 저장된 장치: " + devices_checked.get(i));
//
//			String selectQuery = "SELECT * FROM " + TABLE_CONTACTS +
//								 " WHERE " + KEY_DEVICE_MAC + "=\"" + device_check[0] + "\"" +
//								 " AND " + KEY_SOCKET + "=" + Integer.valueOf(device_check[1]);
//			Cursor cursor = db.rawQuery(selectQuery, null);
//
//			GetDeviceList contact = new GetDeviceList();
//			try {
//				if (cursor.moveToFirst()) {
//					do {
//						contact.setKeyNo(Integer.parseInt(cursor.getString(0)));
//						contact.setKeyDeviceMac(cursor.getString(1));
//						contact.setKeyDeviceName(cursor.getString(2));
//						contact.setKeyDeviceStatus(Integer.parseInt(cursor.getString(3)));
//						contact.setKeyStatus(Integer.parseInt(cursor.getString(4)));
//						contact.setKeySocket(Integer.parseInt(cursor.getString(5)));
//						contact.setKeySocketName(cursor.getString(6));
//						contact.setKeyGPIO(Integer.parseInt(cursor.getString(7)));
//						contact.setKeySocketImg(Integer.parseInt(cursor.getString(8)));
//						contact.setKeySocketActive(Integer.parseInt(cursor.getString(9)));
//						contact.setKeyWakeUp(cursor.getString(10));
//						contact.setKeySleep(cursor.getString(11));
//						contact.setKeyWakeUpDay(cursor.getString(12));
//						contact.setKeySleepDay(cursor.getString(13));
//						contact.setKeyDevice_WakeUp(cursor.getString(14));
//						contact.setKeyDevice_Sleep(cursor.getString(15));
//						contact.setKeyDevice_WakeUpDay(cursor.getString(16));
//						contact.setKeyDevice_SleepDay(cursor.getString(17));
//						contact.setKeyMasterPlug(Integer.parseInt(cursor.getString(18)));
//						contact.setKeyPlugControl(Integer.parseInt(cursor.getString(19)));
//						contact.setKeyPlugBlocker(Integer.parseInt(cursor.getString(20)));
//						contact.setKeyStanByPower(Integer.parseInt(cursor.getString(21)));
//						contact.setKeyStanByPowerThreshold(Integer.parseInt(cursor.getString(22)));
//						contact.setKeyVoiceAlert(Integer.parseInt(cursor.getString(23)));
//						contact.setKeyFireAlert(Integer.parseInt(cursor.getString(24)));
//						contact.setKeyFireAlertThreshold(Integer.parseInt(cursor.getString(25)));
//						contact.setKeyGasAlert(Integer.parseInt(cursor.getString(26)));
//						contact.setKeyGasAlertCO(Integer.parseInt(cursor.getString(27)));
//						contact.setKeyGasAlertLPG(Integer.parseInt(cursor.getString(28)));
//						contact.setKeyGasAlertSMOKE(Integer.parseInt(cursor.getString(29)));
//						contact.setKeyLatitude(Double.parseDouble(cursor.getString(30)));
//						contact.setKeyLongtitude(Double.parseDouble(cursor.getString(31)));
//						contact.setKeyVersion(Integer.parseInt(cursor.getString(32)));
//						contact.setKeyFWtime(cursor.getString(33));
//					} while (cursor.moveToNext());
//				} else {
//					// 테이블이 존재하지 않으므로 알림X
//				}
//
//				if(cursor.getCount()>0 && !contact.getKeyDeviceName().equals("")) {
//					arr_group.add(contact);
//				} else {
//					// 등록된 장치가 없으므로 장치 반환X
//				}
//			} finally {
//				cursor.close();
//			}
//		}
//		return arr_group;
//	}



//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 권한설정 추가
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void addShare(GetShareList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_SHARE_USER_ID, contact.getKeyUserID());
//		values.put(KEY_SHARE_DEVICE_MAC, contact.getKeyDeviceMac());
//		values.put(KEY_SHARE_ID, contact.getKeyShareID());
//		values.put(KEY_SHARE_PLUGCONTROL, contact.getKeyPlugControl());
//		values.put(KEY_SHARE_MASTERPLUG, contact.getKeyMasterPlug());
//		values.put(KEY_SHARE_STANBYPOWER, contact.getKeyStanByPower());
//		values.put(KEY_SHARE_GASALERT, contact.getKeyGasAlert());
//		values.put(KEY_SHARE_FIREALERT, contact.getKeyFireAlert());
//		values.put(KEY_SHARE_VOICEALERT, contact.getKeyVoiceAlert());
//
//		// 권한설정 테이블에 추가
//		db.insert(TABLE_SHARE, null, values);
//		// 데이터베이스 커넥션 닫기
//		db.close();
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 권한설정 편집
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public int updateShare(GetShareList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_SHARE_USER_ID, contact.getKeyUserID());
//		values.put(KEY_SHARE_DEVICE_MAC, contact.getKeyDeviceMac());
//		values.put(KEY_SHARE_ID, contact.getKeyShareID());
//		values.put(KEY_SHARE_PLUGCONTROL, contact.getKeyPlugControl());
//		values.put(KEY_SHARE_MASTERPLUG, contact.getKeyMasterPlug());
//		values.put(KEY_SHARE_STANBYPOWER, contact.getKeyStanByPower());
//		values.put(KEY_SHARE_GASALERT, contact.getKeyGasAlert());
//		values.put(KEY_SHARE_FIREALERT, contact.getKeyFireAlert());
//		values.put(KEY_SHARE_VOICEALERT, contact.getKeyVoiceAlert());
//		if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "권한설정 편집 성공: " + contact.getKeyDeviceMac());
//
//		// 장치 테이블 편집
//		return db.update(TABLE_SHARE, values, KEY_SHARE_NO + " = ?", new String[] { String.valueOf(contact.getKeyNo()) });
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 장치삭제
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void deleteShare() {
//		SQLiteDatabase db = this.getWritableDatabase();
//		int success = db.delete(TABLE_SHARE, KEY_SHARE_NO + " >= ?", new String[] { String.valueOf(0) });
//		db.close();
//
//		if(success == 0) {
//			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "권한설정 삭제 실패");
//		} else {
//			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "권한설정 삭제 성공");
//		}
//	}

//	/**
//	 * Getting multiple contact - 모든 장치목록 불러오기
//	 * @param 	None
//	 * @return 	ArrayList<GetDeviceList> : 저장된 장치목록들
//	 */
//	public ArrayList<GetShareList> getAllShare(String user_ID, String device_Mac) {
//		// Select All Query
//		String selectQuery = "SELECT * FROM " + TABLE_SHARE +
//				 " WHERE " + KEY_SHARE_USER_ID 	+ "=\"" + user_ID + "\"" +
//				 " AND " +  KEY_SHARE_DEVICE_MAC + "=\"" + device_Mac + "\"";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		// looping through all rows and adding to list
//		ArrayList<GetShareList> arr_account = new ArrayList<GetShareList>();
//		try {
//			if (cursor.moveToFirst()) {
//				do {
//					GetShareList contact = new GetShareList();
//					contact.setKeyNo(Integer.parseInt(cursor.getString(0)));
//					contact.setKeyUserID(cursor.getString(1));
//					contact.setKeyDeviceMac(cursor.getString(2));
//					contact.setKeyShareID(cursor.getString(3));
//					contact.setKeyPlugControl(cursor.getInt((4)));
//					contact.setKeyMasterPlug(cursor.getInt((5)));
//					contact.setKeyStanByPower(cursor.getInt((6)));
//					contact.setKeyGasAlert(cursor.getInt((7)));
//					contact.setKeyFireAlert(cursor.getInt((8)));
//					contact.setKeyVoiceAlert(cursor.getInt((9)));
//					arr_account.add(contact);
//				} while (cursor.moveToNext());
//			} else {
//				// 테이블이 존재하지 않으므로 알림X
//			}
//		} finally {
//			cursor.close();
//		}
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get AllShare : " + selectQuery + " --> " + arr_account.size());
//		return arr_account;
//	}



//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 공지사항 추가
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void addNotice(GetNoticeList contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_NOTICE_NO, contact.getKeyNo());
//		values.put(KEY_NOTICE_TITLE, contact.getKeyTitle());
//		values.put(KEY_NOTICE_DATE, contact.getKeyDate());
//		values.put(KEY_NOTICE_CONTENTS, contact.getKeyContents());
//		values.put(KEY_NOTICE_USERID, contact.getKeyUserID());
//
//		// 권한설정 테이블에 추가
//		db.insert(TABLE_NOTICE, null, values);
//		// 데이터베이스 커넥션 닫기
//		db.close();
//	}

//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 장치삭제
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void deleteNotice() {
//		SQLiteDatabase db = this.getWritableDatabase();
//		int success = db.delete(TABLE_NOTICE, KEY_NOTICE_NO + " >= ?", new String[] { String.valueOf(0) });
//		db.close();
//
//		if(success == 0) {
//			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "공지사항 목록 삭제 실패");
//		} else {
//			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "공지사항 목록 삭제 성공");
//		}
//	}

//	/**
//	 * Getting multiple contact - 모든 장치목록 불러오기
//	 * @param 	userID : 사용자 아이디
//	 * @return 	ArrayList<GetDeviceList> : 저장된 장치목록들
//	 */
//	public ArrayList<GetNoticeList> getAllNotice(String userID) {
//		// Select All Query
//		String selectQuery = "SELECT * FROM " + TABLE_NOTICE +
//				" WHERE " + KEY_NOTICE_USERID + "=\"" + userID + "\"";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		// looping through all rows and adding to list
//		ArrayList<GetNoticeList> arr_account = new ArrayList<GetNoticeList>();
//		try {
//			if (cursor.moveToFirst()) {
//				do {
//					GetNoticeList contact = new GetNoticeList();
//					contact.setKeyNo(Integer.parseInt(cursor.getString(0)));
//					contact.setKeyTitle(cursor.getString(1));
//					contact.setKeyDate(cursor.getString(2));
//					contact.setKeyContents(cursor.getString(3));
//					contact.setKeyUserID(cursor.getString(4));
//					arr_account.add(contact);
//				} while (cursor.moveToNext());
//			} else {
//				// 테이블이 존재하지 않으므로 알림X
//			}
//		} finally {
//			cursor.close();
//		}
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get AllNotice : " + selectQuery + " --> " + arr_account.size());
//		return arr_account;
//	}



	/**
	 * All CRUD(Create, Read, Update, Delete) Operation - 알림함 추가
	 * @param 	contact : 장치 정보
	 * @throws 	None
	 * @return 	None
	 */
	public void addMessage(GetMessageList contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MESSAGE_SHARINGID, contact.getKeySharingID());
		values.put(KEY_MESSAGE_SHAREDID, contact.getKeySharedID());
		values.put(KEY_MESSAGE_SHARINGMAC, contact.getKeySharingMAC());
		values.put(KEY_MESSAGE_SHARINGPOPUP, contact.getKeySharingPopup());
		values.put(KEY_MESSAGE_USERID, contact.getKeyUserID());

		// 메시지박스 테이블에 추가
		db.insert(TABLE_MESSAGE, null, values);
		// 데이터베이스 커넥션 닫기
		db.close();
	}


//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 장치삭제
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void deleteMessage(int message_No) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		int success = db.delete(TABLE_MESSAGE, KEY_MESSAGE_NO + " = ?", new String[] { String.valueOf(message_No) });
//		// 데이터베이스 커넥션 닫기
//		db.close();
//
//		if(success == 0) {
//			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "장치삭제 실패: " + message_No);
//		} else {
//			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "장치삭제 성공: " + message_No);
//		}
//	}


//	/**
//	 * All CRUD(Create, Read, Update, Delete) Operation - 모든 알림 삭제
//	 * @param 	contact : 장치 정보
//	 * @throws 	None
//	 * @return 	None
//	 */
//	public void deleteMessageDB() {
//		SQLiteDatabase db = this.getWritableDatabase();
//		int success = db.delete(TABLE_MESSAGE, KEY_MESSAGE_NO + " >= ?", new String[] { String.valueOf(0) });
//		db.close();
//
//		if(success == 0) {
//			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "메시지박스 목록 삭제 실패");
//		} else {
//			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "메시지박스 목록 삭제 성공");
//		}
//	}

//	/**
//	 * Getting multiple contact - 모든 알림 불러오기
//	 * @param 	None
//	 * @return 	ArrayList<GetDeviceList> : 저장된 장치목록들
//	 */
//	public ArrayList<GetMessageList> getAllMessage(String userID) {
//		// Select All Query
//		String selectQuery = "SELECT * FROM " + TABLE_MESSAGE +
//				" WHERE " + KEY_MESSAGE_USERID + "=\"" + userID + "\"" +
//				" ORDER BY " + KEY_MESSAGE_NO + " DESC";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		// looping through all rows and adding to list
//		ArrayList<GetMessageList> arr_account = new ArrayList<GetMessageList>();
//		try {
//			if (cursor.moveToFirst()) {
//				do {
//					GetMessageList contact = new GetMessageList();
//					contact.setKeyNo(Integer.parseInt(cursor.getString(0)));
//					contact.setKeySharingID(cursor.getString(1));
//					contact.setKeySharedID(cursor.getString(2));
//					contact.setKeySharingMAC(cursor.getString(3));
//					contact.setKeySharingPopup(cursor.getInt(4));
//					contact.setKeyUserID(cursor.getString(5));
//					arr_account.add(contact);
//				} while (cursor.moveToNext());
//			} else {
//				// 테이블이 존재하지 않으므로 알림X
//			}
//		} finally {
//			cursor.close();
//		}
//		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get AllMessage : " + selectQuery + " --> " + arr_account.size());
//		return arr_account;
//	}
}