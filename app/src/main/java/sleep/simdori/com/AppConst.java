package sleep.simdori.com;

/**
 * 각 클래스에서 사용되는 변수명, 주소, 설정값을 저장한다.
 * simdori_Host / simdori_MQTT_Host 만 변경하면 다른 주소도 변경된다.
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class AppConst {
    //20170321 DY 설정초기화 처리
    // 디버깅용 초기화 버튼 기능 삭제, 2017-11-24/shlee
    public static final boolean DEBUG_RESET_ALL_SETTINGS = false;
    //MQTT / JSON DEBUG
    public static final boolean DEBUG_ALL = true;
    public static final boolean DEBUG_KSJUNG = true;
    public static final boolean DEBUG_MQTT = false;
    public static final boolean DEBUG_JSON = false;

    // 공통
    public static final String VERSION = "2.0.9";
    public static final String TAG = "LOG4";
    public static final String TAGmac = "LOGmc";
    public static final String Super_ID = "simdori";
    public static final String Supwr_PW = "simdori1!";
    public static final int HONEYCOMB = 11;

    // 스플래쉬 딜레이
    public static final int SPLASH_DISPLAY_LENGTH = 1000;
    public static final int Request_Delay = 5000;
    public static final int Response_Delay = 5000;
    public static final int GPIO_Response_Delay = 5000;
    public static final int Device_List_First = 800;
    public static final int Device_List_Delay = 800;
    public static final int Device_List_RESPONSE_TIME = 8;
    public static final int Wifi_Scan_First = 10000;
    public static final int Wifi_Scan_Delay = 10000;


    // URL - simdori
    public static final String simdori_Host = "simdori.com";
    public static final String simdori_API_Host = "api.simdori.com";
    public static final String simdori_MQTT_Host = "mqtt.simdori.com";
    //	public static final String 	simdori_Azure_Host		= "vitar.com";
//	public static final String 	simdori_API_Host 		= simdori_Azure_Host;
//	public static final String 	simdori_MQTT_Host		= simdori_Azure_Host;
    public static final int MQTT_port = 8883;

    // MQTT - 인증서
    public static final String MQTT_FilePath = "/storage/emulated/0/simdori/";
    public static final String MQTT_CA_FilePath = "/storage/emulated/0/simdori/all-ca.crt";
    public static final String MQTT_ClientCrt_FilePath = "/storage/emulated/0/simdori/client.crt";
    public static final String MQTT_ClientKey_FilePath = "/storage/emulated/0/simdori/client.key";

    // MQTT - 토픽 및 메시지
    public static final String Topic = "simdori/";
    public static final String MQTT_SensorData_Topic = "sensordata/";
    public static final int MQTT_TimeToWait = 1000;
    public static final int MQTT_KeepAlive = 120;
    public static final int MQTT_RESPONSE_TIME = 15; //20170316 DY ON/OFF 스위치 반복처리 기본: 150
    public static final int MQTT_STATUS_MINTIME = 10;
    public static final int MQTT_STATUS_RESPONSE_TIME = 150;
    public static final int MQTT_TopicType_User = 0;
    public static final int MQTT_TopicType_Device = 1;
    public static final int MQTT_TopicType_Sensor = 2;
    public static final String MQTT_WilMessage = "RESPONSE/5/OFFLINE/ALL/";
    public static final String Msg_Request = "REQUEST";
    public static final String Msg_Response = "RESPONSE";
    public static final String Msg_Device = "REQUEST/5/9/";
    public static final String Msg_Fwupdate = "REQUEST/10/1/";
    public static final String Msg_GPIO_Group_ON = "REQUEST/1/24/";
    public static final String Msg_GPIO_Group_OFF = "REQUEST/1/9/";
    public static final String Msg_GPIO = "REQUEST/1/";
    public static final String Msg_GPIO2 = "RESPONSE/1/";
    public static final int Msg_GPIO_OFFSET = 9;
    public static final int Msg_GPIO_MAX = 24;
    public static final String Msg_ONLINE = "ONLINE";
    public static final String Msg_OFFLINE = "OFFLINE";
    public static final int OFF = 0;
    public static final int ON = 1;
    public static final int DEVICE_TYPE_Default = 0;
    public static final int DEVICE_TYPE_Socket1 = 1;
    public static final int DEVICE_TYPE_Socket2 = 2;
    public static final int DEVICE_TYPE_Socket3 = 3;
    public static final int DEVICE_TYPE_Socket4 = 4;
    public static final int DEVICE_ONLINE = 1;
    public static final int DEVICE_OFFLINE = 0;
    public static final int DEVICE_DISCONNECTED = 2;
    public static final String HEX_CHECK = "^[a-fA-F0-9]+";

    public static final String mmWaveStart = "response/sensor_start";
    public static final String mmWaveStop = "response/sensor_stop";
    public static final String mmWaveBackUp = "response/sensor_backup";

    // URL - DB
    public static final int DB_Port = 3000;
    public static final String InsertBPM_host = "http://" + simdori_API_Host + ":" + DB_Port + "/insertBPM";
    public static final String InsertBPMDetail_host = "http://" + simdori_API_Host + ":" + DB_Port + "/insertBPMDetail";
    public static final String Select_BPM_host = "http://" + simdori_API_Host + ":" + DB_Port + "/selectBPM";
    public static final String Select_BPMDetail_host = "http://" + simdori_API_Host + ":" + DB_Port + "/selectBPMDetail";
    public static final String registerDevice_host = "http://" + simdori_API_Host + ":" + DB_Port + "/registerDevice";
    public static final String deleteDevice_host = "http://" + simdori_API_Host + ":" + DB_Port + "/deleteDevice";
    public static final String getDeviceMac_host = "http://" + simdori_API_Host + ":" + DB_Port + "/getDeviceMac";
    public static final String getSleepDaily_host = "http://" + simdori_API_Host + ":" + DB_Port + "/getSleepDaily";
    public static final String getSleepDays_host = "http://" + simdori_API_Host + ":" + DB_Port + "/getSleepDays";


    // URL - 로그인
    public static final int Login_Port = 3000;
    public static final String Login_host = "http://" + simdori_API_Host + ":" + Login_Port + "/login";
    public static final String Logout_host = "http://" + simdori_API_Host + ":" + Login_Port + "/logout";
    public static final String Find_ID_host = "http://" + simdori_API_Host + ":" + Login_Port + "/findId";
    public static final String Find_pw_host = "http://" + simdori_API_Host + ":" + Login_Port + "/findPassword";

    // URL - 회원가입
    public static final int Signup_Port = 3000;
    public static final String Signup_host = "http://" + simdori_API_Host + ":" + Signup_Port + "/signup";
    public static final String ID_check_host = "http://" + simdori_API_Host + ":" + Signup_Port + "/isDupID";
    public static final String Email_Chekck_host = "http://" + simdori_API_Host + ":" + Signup_Port + "/isDupEmail";
    public static final String Update_host = "http://" + simdori_API_Host + ":" + Signup_Port + "/update";
    public static final String Delete_host = "http://" + simdori_API_Host + ":" + Signup_Port + "/delete";

    // URL - 장치
    public static final int Device_Port = 3000;
    public static final String Device_List_host = "http://" + simdori_API_Host + ":" + Device_Port + "/listUseIOT";
    public static final String Device_Register_host = "http://" + simdori_API_Host + ":" + Device_Port + "/registerDevice";
    public static final String Device_Edit_host = "http://" + simdori_API_Host + ":" + Device_Port + "/updateUseIOT";
    public static final String Device_SocketEdit_host = "http://" + simdori_API_Host + ":" + Device_Port + "/updateSocketIOT";
    public static final String Device_Delete_host = "http://" + simdori_API_Host + ":" + Device_Port + "/deleteUseIOT";

    // URL - 장치공유
    public static final String Device_Request_host = "http://" + simdori_API_Host + ":" + Device_Port + "/requestUseIOT";
    public static final String Device_Release_host = "http://" + simdori_API_Host + ":" + Device_Port + "/releaseUseIOT";
    public static final String Device_Accept_host = "http://" + simdori_API_Host + ":" + Device_Port + "/acceptUseIOT";
    public static final String Device_Reject_host = "http://" + simdori_API_Host + ":" + Device_Port + "/rejectUseIOT";
    public static final String Device_Shared_Request_host = "http://" + simdori_API_Host + ":" + Device_Port + "/requestDeviceShare";
    public static final String Device_Shared_Accept_host = "http://" + simdori_API_Host + ":" + Device_Port + "/acceptDeviceShare";
    public static final String Device_Shared_Reject_host = "http://" + simdori_API_Host + ":" + Device_Port + "/rejectDeviceShare";

    // URL - 팝업 제거
    public static final String Device_SharingPopup_host = "http://" + simdori_API_Host + ":" + Device_Port + "/deleteShareInfo";
    public static final String Device_GetGroup_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getGroup";
    public static final String Device_GetGroupActivity_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getGroupActivity";//20170329 DY 그룹별 사용 빈도 알림박스
    public static final String Device_SetGroup_host = "http://" + simdori_API_Host + ":" + Device_Port + "/setGroup";

    // URL - 센서
    public static final String Device_SetMasterPlug_host = "http://" + simdori_API_Host + ":" + Device_Port + "/setMasterPlug";
    public static final String Device_GetMasterPlug_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getMasterPlug";
    public static final String Device_SetPlugBlocker_host = "http://" + simdori_API_Host + ":" + Device_Port + "/setBlocking";
    public static final String Device_GetPlugBlocker_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getBlocking";
    public static final String Device_GetDeviceShareList_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getDeviceShareList";
    public static final String Device_SetDeviceAuth_host = "http://" + simdori_API_Host + ":" + Device_Port + "/setDeviceAuth";
    public static final String Device_GetDeviceAuth_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getDeviceAuth";
    public static final String Device_SetThreshold_host = "http://" + simdori_API_Host + ":" + Device_Port + "/setThreshold";
    public static final String Device_GetThreshold_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getThreshold";
    public static final String Device_GetWhData_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getWhData";
    public static final String Device_GetStanByPower_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getStandByPower";

    public static final String Device_GetWhList_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getWhList";
    public static final String Device_GetDailyWh_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getIntervalHourlyWh";
    public static final String Device_GetWeeklyWh_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getIntervalDailyWh";
    public static final String Device_GetMonthlyWh_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getIntervalWeeklyWh";

    // URL - AI
    public static final String Device_Get_AI_host = "http://" + simdori_API_Host + ":" + Device_Port + "/getAI";
    public static final String Device_Set_AI_host = "http://" + simdori_API_Host + ":" + Device_Port + "/setAI";

    // URL - Google 주소 가져오기
    public static final String GOOGLE_GEOCODER = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";

    // URL - 예약설정
    public static final String Device_Wakeup_host = "http://" + simdori_API_Host + ":" + Device_Port + "/setWakeupTime";
    public static final String Device_Wakeup_Cancel_host = "http://" + simdori_API_Host + ":" + Device_Port + "/cancelWakeupTime";
    public static final String Device_Sleep_host = "http://" + simdori_API_Host + ":" + Device_Port + "/setSleepTime";
    public static final String Device_Sleep_Cancel_host = "http://" + simdori_API_Host + ":" + Device_Port + "/cancelSleepTime";
    public static final String Device_WakeupGroup_host = "http://" + simdori_API_Host + ":" + Device_Port + "/setGroupWakupTime";
    public static final String Device_WakeupGroup_Cancel_host = "http://" + simdori_API_Host + ":" + Device_Port + "/cancelGroupWakeupTime";
    public static final String Device_SleepGroup_host = "http://" + simdori_API_Host + ":" + Device_Port + "/setGroupSleepTime";
    public static final String Device_SleepGroup_Cancel_host = "http://" + simdori_API_Host + ":" + Device_Port + "/cancelGroupSleepTime";

    // URL - 고객센터
    //public static final String 	Notice_host 			= "http://" + simdori_Host + "/bbs/board.php?bo_table=notice";
    public static final String Notice_host = "http://" + simdori_API_Host + ":" + Device_Port + "/langdata";
    public static final String Guide_host = "file:///android_asset/appuse.php";
    public static final String Guide_en_host = "file:///android_asset/appuse_en.php";
    public static final String Guide_zh_host = "file:///android_asset/appuse_cn.php";
    public static final String Agreements_host = "file:///android_asset/agreement.php";
    public static final String QnA_host = "file:///android_asset/QnA.php";
    public static final String inquiry_host = "file:///android_asset/inquiry.php";
    public static final String help_host = "file:///android_asset/help.php";
    public static final String Agreements_en_host = "file:///android_asset/agreement_en.php";
    public static final String Agreements_zh_host = "file:///android_asset/agreement_cn.php";
    public static final String Personal_host = "file:///android_asset/personal.php";
    public static final String Personal_en_host = "file:///android_asset/personal_en.php";
    public static final String Personal_zh_host = "file:///android_asset/personal_cn.php";
    public static final String Location_host = "file:///android_asset/location.php";
    public static final String Location_en_host = "file:///android_asset/location_en.php";
    public static final String Location_zh_host = "file:///android_asset/location_cn.php";
    public static final String Opensource_host = "file:///android_asset/opensource.php";


    // Facebook
    public static final String FACEBOOK_ID = "212670339133503";
    public static final int LoginType_Default = 0;        // 일반 회원가입
    public static final int LoginType_Facebook = 1;        // 페이스북 회원가입
    public static final int LoginType_Kakao = 2;        // 카카오톡 회원가입
    public static final int LOGIN_PW_NO_SAVE = 0;        // 비빌번호 저장없음
    public static final int LOGIN_PW_SAVE = 1;        // 비밀번호 저장
    public static final int LOGIN_NO_AUTOLOGIN = 0;        // 자동로그인 없음
    public static final int LOGIN_AUTOLOGIN = 1;        // 자동로그인
    public static final int LoginStatus_Logout = 0;        // 로그아웃
    public static final int LoginStatus_Default = 1;        // 일반 로그인
    public static final int LoginStatus_Auto = 2;        // 자동 로그인
    public static final int LoginStatus_Facebook = 3;        // 페이스북 로그인

    // Intent
    public static final int INTENT_REGISTER = 0;        // 회원가입 인텐트
    public static final int Login_Password_Save = 1;        // 비밀번호 저장 여부
    public static final int Queue_GPIO_BaseLine = 5;        // GPIO 응답 제한
    public static final int Queue_GPIO_Last = 1;        // GPIO 응답
    public static final int QUEUE_ZERO = 0;        // 큐 기본값
    public static final int QUEUE_MAX = 9;        // 큐 최댓값

    // 장치정보
    public static final int Device_Request_Type1 = 1;        // 회원가입 인텐트
    public static final int Device_Request_Type2 = 2;        // 비밀번호 저장 여부
    public static final String Device_FWupdate_Status = "2016-06-16 15:44:30";            // 펌웨어 날짜
    public static final String Device_FWupdate_Status2 = "2016 Aug 11 20:10:51";        // 펌웨어 날짜

    public static final int Device_Status_Default = 0;            // 기본 공유상태
    public static final int Device_Status_Sharing = 2;            // 공유한 장치
    public static final int Device_Status_Shared = 3;            // 공유받은 장치
    public static final String Device_List_NoData = "noData";        // 장치가 없는 경우

    // 그룹정보
    public static final int Group_list_BaseLine = 200;        // 그룹 추가 제한수 - 3개
    public static final int Group_AI_NoSetting = 16;        // 그룹 AI 설정 없음
    public static final int Group_Save_Store = 0;        // 그룹정보 저장
    public static final int Group_Save_Restore = 1;        // 그룹정보 복원
    public static final String Group_NoDevice = "";        // 그룹 내 장치가 없는 경우
    public static final String Group_NewDevice = "test/0";    // 그룹 내 새장치 등록하는 경우
    public static final int Group_MAX = 3;        // 그룹 최댓값

    // MQTT Status
    public static final int MQTT_Publish_Baseline = 3;    // MQTT 연결 성공
    public static final int MQTT_Success = 0;    // MQTT 연결 성공
    public static final int MQTT_Failed_Connection = 1;    // 연결 실패 - 커넥션 오류
    public static final int MQTT_Failed_Topic_Null = 2;    // 연결 실패 - 토픽 처리 오류
    public static final int MQTT_Failed_Bundle_Null = 3;    // 연결 실패 - 번들 오류
    public static final int MQTT_Failed_Client_Null = 4;    // 연결 실패 - 클라이언트 연결 오류
    public static final int MQTT_Failed_Data = 5;    // 연결 실패 - 잘못된 값
    public static final int MQTT_Already_Connection = 6;    // 연결 실패 - 잘못된 값

    // MQTT Message - 사용자 토픽
    public static final int MQTT_Receiver_NoMQTT = -1;    // 로그아웃
    public static final int MQTT_Receiver_Logout = 0;    // 로그아웃
    public static final int MQTT_Receiver_Device_ON = 1;    // 전원ON
    public static final int MQTT_Receiver_Device_OFF = 2;    // 전원OFF
    public static final int MQTT_Receiver_GPIO = 3;    // GPIO
    public static final int MQTT_Receiver_Sensor = 4;    // 센서

    // MQTT Message - 사용자 토픽
    public static final int MQTT_Logout = 1;    // 로그아웃
    public static final int MQTT_Sharing_Requested = 2;    // 장치공유
    public static final int MQTT_Sharing_Accepted = 3;    // 장치공유 수락
    public static final int MQTT_Sharing_Rejected = 4;    // 장치공유 거절
    public static final int MQTT_Sharing_Released = 5;    // 장치공유 해제
    public static final int MQTT_Shared_Requested = 6;    // 장치공유 요청
    public static final int MQTT_Shared_Accepted = 7;    // 장치공유 요청 수락
    public static final int MQTT_Shared_Rejected = 8;    // 장치공유 요청 거절
    public static final int MQTT_Notice = 9;    // 단체 공지사항
    public static final int MQTT_FwUpdate = 10;    // 일괄 장치 업데이트
    public static final int MQTT_Auto_FwUpdate = 11;    // 장치 업데이트

    // MQTT Message - 장치 토픽
    public static final int MQTT_Device_GPIO = 1;    // 전원 제어
    public static final int MQTT_Device_Alert = 4;    // 장치 상태
    public static final int MQTT_Device_Status = 5;    // 장치 상태
    public static final int MQTT_Device_Info = 8;    // 장치 정보

    public static final int MQTT_GPIO_Device_Control = 0;    // 장치 정보
    public static final int MQTT_GPIO_Device_Status = 1;    // 장치 정보

    // Sensor Type
    public static final int SensorType_Version1 = 1;        // simdori 1.0
    public static final int SensorType_Version2 = 2;        // simdori 2.0
    public static final int SensorType_StabBy1 = 1;        // 1번 플러그 대기전력 차단 기준치
    public static final int SensorType_StabBy2 = 2;        // 2번 플러그 대기전력 차단 기준치
    public static final int SensorType_StabBy3 = 3;        // 3번 플러그 대기전력 차단 기준치
    public static final int SensorType_StabBy4 = 4;        // 4번 플러그 대기전력 차단 기준치
    public static final int SensorType_Temperature = 11;        // 온도 기준치
    public static final int SensorType_Humidity = 12;        // 습도 기준치
    public static final int SensorType_CO = 21;        // 가스 알람 기준치 - CO
    public static final int SensorType_LPG = 22;        // 가스 알람 기준치 - LPG
    public static final int SensorType_SMOKE = 23;        // 가스 알람 기준치 - SMOKE
    public static final int SensorType_VoiceAlert = 30;        // 음성 알람
    public static final int SensorType_GasAlert = 31;        // 가스 알람
    public static final int SensorType_FireAlert = 32;        // 화재 알람
    public static final int Sensor_GasAlert_Warning_Baseline = 1;        //  화재주의보 기준치
    public static final int Sensor_GasAlert_Alarm_Baseline = 2;        //  화재경보 기준치

    // 센서 이름
    public static final String SensorName_StabBy = "current";        // 전력량
    public static final String SensorName_Gas = "gas";            // 가스량
    public static final String SensorName_Temperature = "temperature";    // 온도
    public static final String SensorName_Temperature_Character = "°C";    // 온도
    public static final String SensorName_Humidity = "humidity";        // 습도
    public static final String SensorName_VoiceAlert = "voice_alarm";    // 음성 알람
    public static final String SensorName_GasAlert = "gas_alarm";        // 가스 알람
    public static final String SensorName_FireAlert = "fire_alarm";        // 화재 알람

    // 전력 사용량
    public static final String Sensor_StandByPower_Daily = "daily";        // 하루 사용량
    public static final String Sensor_StandByPower_Weekly = "weekly";        // 주간 사용량
    public static final String Sensor_StandByPower_Monthly = "monthly";    // 월간 사용량

    public static final int Sensor_StandByPower_Total = 0;        // 전체 사용량
    public static final int Sensor_StandByPower_Plug1 = 1;        // 소켓1 사용량
    public static final int Sensor_StandByPower_Plug2 = 2;        // 소켓2 사용량
    public static final int Sensor_StandByPower_Plug3 = 3;        // 소켓3 사용량
    public static final int Sensor_StandByPower_Plug4 = 4;        // 소켓4 사용량


    // 마스터 플러그 설정
    public static final int MasterPlug_Baseline = 4;    // 마스트 플러그 기준치
    public static final int MasterPlug_Master = 2;    // 마스트 플러그
    public static final int MasterPlug_Slave = 1;    // 슬레이브 플러그
    public static final int MasterPlug_None = 0;    // 설정 없음

    // 플러그 차단 설정
    public static final int PlugBlocker_NoAuth = 0;    // 설정 없음
    public static final int PlugBlocker_Auth = 1;    // 플러그 차단 설정
    public static final int PlugBlocker_Socket1 = 8;    // 플러그1 차단값
    public static final int PlugBlocker_Socket2 = 4;    // 플러그2 차단값
    public static final int PlugBlocker_Socket3 = 2;    // 플러그3 차단값
    public static final int PlugBlocker_Socket4 = 1;    // 플러그4 차단값
    public static final int PlugBlocker_defalut = 0;    // 플러그1 차단값

    // 권한설정 여부
    public static final int Permission_Deny = 0;    // 권한 거절
    public static final int Permission_Allow = 1;    // 권한 승락

    // 예약설정
    public static final int Sleep_Type_Device = 0;        // 기본화면 내 그룹
    public static final int Sleep_Type_Group = 1;        // 예약설정 화면 내 그룹
    public static final int Setting_SleepTime = 1;
    public static final int Setting_WakeupTime = 0;
    public static final String Setting_NoTime = "0000-00-00 00:00:00";
    public static final String Setting_AllDay = "1111111";
    public static final String Setting_NoDay = "0000000";
    public static final String Setting_AllSocket = "1111";
    public static final String Setting_NoSocket = "0000";
    public static final int Setting_Auth = 1;
    public static final int Setting_NoAuth = 0;


    // 장치설정
    public static final int Setting_Socket_Edit = 0;
    public static final int Setting_Socket_Sleep = 1;
    public static final int Setting_Device_Edit = 0;
    public static final int Setting_Device_Delete = 1;
    public static final int Setting_Sleep_Setting = 2;
    public static final int Setting_AIsetting = 3;
    public static final int Setting_AIsetting_IN = 0;
    public static final int Setting_AIsetting_OUT = 1;
    public static final int AIsetting_AItype_NONE = 0;
    public static final int AIsetting_AItype_IN = 1;
    public static final int AIsetting_AItype_OUT = 2;
    public static final int AIsetting_AItype_ALL = 3;
    public static final int AIsetting_Status_Socket1 = 0;        // 로그아웃
    public static final int AIsetting_Status_Socket2 = 1;        // 일반 로그인
    public static final int AIsetting_Status_Socket3 = 2;        // 자동 로그인
    public static final int AIsetting_Status_Socket4 = 3;        // 페이스북 로그인

    // Wi-Fi
    public static final int WIFI_CONNECTION_SUCCESS = 1;        // WI-FI 연결 성공
    public static final int WIFI_CONNECTION_FAIL = 0;        // WI-FI 연결 실패
    public static final int WIFI_Setting_Login = 1;        // Wi-FI 설정 로그인
    public static final int WIFI_Setting_Connect = 2;        // WI-FI 연결

    // LuCI - RPC Method
    public static final String Wifi_Admin_ID = "root";
    public static final String Wifi_method = "login";
    public static final String Wifi_GPIO_method_on = "on";
    public static final String Wifi_GPIO_method_off = "off";
    public static final String Wifi_SysLog_method = "getDhcpLease";
    public static final String Wifi_Admin_method = "user.setpasswd";
    public static final String Wifi_Edit_method = "changeWiFiConfig";
    public static final String Wifi_APinfo_method = "getDeviceInfo";
    public static final String Wifi_Repeater_method = "changeDeviceMode";
    public static final String Wifi_RPinfo_method = "getApInfo";
    public static final String Wifi_FW_method = "updateFirmware";
    public static final String WIfi_NR_method = "nr";
    public static final String Wifi_Reboot_method = "reboot";

    // LuCI - APmode
    public static final int APInfo_DHCP = 1;
    public static final int APInfo_REPEATER = 2;
    public static final int APInfo_REPEATER_ifname = 1;
    public static final int APInfo_REPEATER_ApCliChannel = 2;
    public static final int APInfo_REPEATER_ApCliSsid = 3;
    public static final int APInfo_REPEATER_ApCliAuthMode = 4;
    public static final int APInfo_REPEATER_ApCliEncrypType = 5;
    public static final int APInfo_REPEATER_ApCliPassWord = 6;
    public static final int APInfo_STATIC = 3;
    public static final int APInfo_STATIC_ipAddr = 1;
    public static final int APInfo_STATIC_SubnetMask = 2;
    public static final int APInfo_STATIC_Gateway = 3;
    public static final int APInfo_STATIC_DNS = 4;
    public static final int APInfo_LAN = 4;
    public static final String APMode_DHCP = "1";
    public static final String APMode_REPEATER = "2";
    public static final String APMode_STATIC = "3";
    public static final String APMode_LAN = "4";
    public static final String APMode_REPEATER_STATIC = "5";
    public static final String Mode_AP = "ap";
    public static final String Mode_STA = "sta";
    public static final String Mode_AdHoc = "adhoc";
    public static final String EncryptionKey_AUTO = "auto";
    public static final String EncryptionKey_CCMP = "ccmp";
    public static final String EncryptionKey_TKIP = "tkip";
    public static final String EncryptionKey_TKIP_CCMP = "tkip+ccmp";
    public static final String Encryption_No = "none";
    public static final String Encryption_WEP_Open = "wep-open";
    public static final String Encryption_WEP_Shared = "wep-shared";
    public static final String Encryption_PSK = "psk";
    public static final String Encryption_PSK2 = "psk2";
    public static final String Encryption_Mixed = "psk-mixed";

    // LuCI - Repeater Mode
    public static final String ifname_AP = "eth0.2";
    public static final String ifname_Repeater = "apcli0";
    public static final String ifname_Extender = "eth0.1";
    public static final String ApCliAuthMode_WPA = "WPAPSK";
    public static final String ApCliAuthMode_WPA2 = "WPA2PSK";
    public static final String ApCliAuthMode_WEP = "WEP";
    public static final String ApCliAuthMode_NONE = "NONE";
    public static final String ApCliEncrypType_AES = "AES";
    public static final String ApCliEncrypType_TKIP = "TKIP";
    public static final String ApCliEncrypType_WEP = "WEP";
    public static final String ApCliEncrypType_NONE = "NONE";
    public static final String ApCliChannel_AUTO = "auto";
    public static final String ApCliChannel_1 = "1";
    public static final String ApCliChannel_2 = "2";
    public static final String ApCliChannel_3 = "3";
    public static final String ApCliChannel_4 = "4";
    public static final String ApCliChannel_5 = "5";
    public static final String ApCliChannel_6 = "6";
    public static final String ApCliChannel_7 = "7";
    public static final String ApCliChannel_8 = "8";
    public static final String ApCliChannel_9 = "9";
    public static final String ApCliChannel_10 = "10";
    public static final String ApCliChannel_11 = "11";
    public static final String ApCliChannel_12 = "12";
    public static final String ApCliChannel_13 = "13";
    public static final int ApCliChannel_2412 = 2412;
    public static final int ApCliChannel_2417 = 2417;
    public static final int ApCliChannel_2422 = 2422;
    public static final int ApCliChannel_2427 = 2427;
    public static final int ApCliChannel_2432 = 2432;
    public static final int ApCliChannel_2437 = 2437;
    public static final int ApCliChannel_2442 = 2442;
    public static final int ApCliChannel_2447 = 2447;
    public static final int ApCliChannel_2452 = 2452;
    public static final int ApCliChannel_2457 = 2457;
    public static final int ApCliChannel_2462 = 2462;
    public static final int ApCliChannel_2467 = 2467;
    public static final int ApCliChannel_2472 = 2472;

    // Network ResultMSG
    public static final String Network_ResultMSG_Hold = "3";    // 연결 성공
    public static final String Network_ResultMSG_Duplicate = "2";    // 연결 성공
    public static final String Network_ResultMSG_Facebook = "2";    // 연결 성공
    public static final String Network_ResultMSG_NoAuth = "2";    // 권한이 없는 경우
    public static final String Network_ResultMSG_Success = "1";    // 연결 성공
    public static final String Network_ResultMSG_Fail = "0";    // 연결 실패
    public static final String Network_ResultMSG2_Success = "0";    // 연결 성공
    public static final String Network_ResultMSG2_NOT_CORRECT_DATA = "101";    // 잘못된 데이터
    public static final String Network_ResultMSG2_NOT_LOGIN = "102";    // 로그인되지 않는 사용자
    public static final String Network_ResultMSG2_DB_CONNECTION_ERROR = "103";    // DB 커넥션 오류
    public static final String Network_ResultMSG2_DB_TRANSACTION_ERROR = "104";    // DB 트랜젝션 오류
    public static final String Network_ResultMSG2_UNREGISTERED_DEVICE = "105";    // 등록되지 않은 장치
    public static final String Network_ResultMSG2_NO_DATA_BLOCKING_PLUG = "106";    // 잘못된 블럭킹 값
    public static final String Network_ResultMSG2_NO_DATA_GROUP = "107";    // 그룹 없음
    public static final String Network_ResultMSG2_NO_DEVICE_LIST = "108";    // 장치 없음
    public static final String Network_ResultMSG2_NOT_WAKEUP_TIME = "109";    // 켜지는 시간 없음
    public static final String Network_ResultMSG2_ALREADY_CANCEL_SCHEDULE = "110";    // 이미 취소된 스케쥴
    public static final String Network_ResultMSG2_DEVICE_INTERNET_OFF = "111";    // 장치가 꺼져 있음
    public static final String Network_ResultMSG2_NO_DATA_AI_SETTING = "112";    // 잘못된 AI설정 값
    public static final String Network_ResultMSG2_NO_MASTERPLUG = "113";    // 잘못된 마스터 플러그 설정 값
    public static final String Network_ResultMSG2_NOT_BLCOKING = "114";    // 플러그 차단 설정 못함
    public static final String Network_ResultMSG2_NOT_MASTERPLUG = "115";    // 마스터 플러그 설정 못함
    public static final String Network_ResultMSG2_NOT_AI = "116";    // AI설정 못함
    public static final String Network_ResultMSG2_NOT_SLEEP_TIME = "117";    // 꺼지는 시간 없음
    public static final String Network_ResultMSG2_NOT_GROUP_TIME = "118";    // 그룹 시간 없음
    public static final String Network_ResultMSG2_NOT_SOCKET_TIME = "119";    // 소켓 시간 없음


    // Network ResultCode
    public static final int Network_Success = 0;    // 연결 성공
    public static final int Network_Failed_Connection = 1;    // 연결 실패 - 커넥션 오류
    public static final int Network_Failed_Response = 2;    // 연결 실패 - 응답 오류
    public static final int Network_Failed_Data = 3;    // 연결 실패 - 변환 오류
    public static final int Network_Failed_RetryOver = 4;    // 연결 실패 - 재시도 초과
    public static final int Network_Failed_Setting = 5;    // 연결 실패 - 변환 오류
    public static final int Network_Facebooke_login = 11;    // 연결 실패 - 재시도 초과
    public static final int Network_Failed_NoAuth = 12;    // 연결 실패 - 재시도 초과
    public static final int Network_Retry_Baseline = 2;    // 재시도 횟수

    // Wi-FI Asynctask Status
    public static final int Wifi_FWUpdate_Failed = 10;    // 연결 실패 - 재시도 초과
    public static final int Wifi_FWUpdate_UptoDate = 6;    // 연결 실패 - 재시도 초과
    public static final int Wifi_FWUpdate_NotSupported = 5;    // 연결 실패 - 재시도 초과
    public static final int Wifi_Failed_RetryOver = 1;    // 연결 실패 - 재시도 초과
    public static final int Wifi_Success = 0;    // 연결 실패 - 재시도 초과
    public static final int Wifi_Edit_Fail_KEY = 8;    // 연결 실패 - 재시도 초과
    public static final int Wifi_Edit_Fail_Encryption = 7;    // 연결 실패 - 재시도 초과
    public static final int Wifi_Edit_Fail_Mode = 6;    // 연결 실패 - 재시도 초과
    public static final int Wifi_Edit_Fail_SSID = 5;    // 연결 실패 - 재시도 초과

    // AP모드
    public static final int Wifi_RePeater_Success_LAN_Cable = 15;    // 랜선이 연결되어 있지 않을 때
    public static final int Wifi_RePeater_Fail_PrimaryDNS = 14;    // 기본 DNS가 존재하지 않을 때
    public static final int Wifi_RePeater_Fail_Gateway = 13;    // 게이트 웨이가 존재하지 않을 때
    public static final int Wifi_RePeater_Fail_SubnetMask = 12;    // 넷마스크 주소가 존재하지 않을 때
    public static final int Wifi_RePeater_Fail_IPAddr = 11;    // 고정 아이피 주소가 존재하지 않을 때
    public static final int Wifi_RePeater_Fail_ApCliChannel = 10;    // 연결 실패 - 재시도 초과
    public static final int Wifi_RePeater_Fail_ApCliPassWord = 9;    // 연결 실패 - 재시도 초과
    public static final int Wifi_RePeater_Fail_ApCliEncrypType = 8;    // 연결 실패 - 재시도 초과
    public static final int Wifi_RePeater_Fail_ApCliAuthMode = 7;    // 연결 실패 - 재시도 초과
    public static final int Wifi_RePeater_Fail_ApCliSsid = 6;    // 연결 실패 - 재시도 초과
    public static final int Wifi_RePeater_Fail_ifnamed = 5;    // 연결 실패 - 재시도 초과
    public static final int Wifi_APMODE_ReaperMode = 3;    // 연결 실패 - 재시도 초과
    public static final int Wifi_APMODE_LAN = 2;    // 연결 실패 - 재시도 초과
    public static final int Wifi_APMODE_STATIC = 1;    // 연결 실패 - 재시도 초과
    public static final int Wifi_APMODE_DHCP = 0;    // 연결 실패 - 재시도 초과

    // 공유기 설정
    public static final int Wifi_EncryptionKey_AUTO = 0;
    public static final int Wifi_EncryptionKey_CCMP = 1;
    public static final int Wifi_EncryptionKey_TKIP = 2;
    public static final int Wifi_EncryptionKey_TKIP_CCMP = 3;
    public static final int Wifi_Encryption_No = 0;
    //public static final int Wifi_Encryption_WEP_Open	= 1;
    public static final int Wifi_Encryption_WEP_Shared = 2;
    public static final int Wifi_Encryption_PSK = 3;
    public static final int Wifi_Encryption_PSK2 = 1;
    public static final int Wifi_Encryption_Mixed = 5;
    public static final int Wifi_ifname_AP = 0;
    public static final int Wifi_ifname_Repeater = 1;
    public static final int Wifi_ifname_Extender = 2;
    public static final int Wifi_ApCliAuthMode_NONE = 0;
    public static final int Wifi_ApCliAuthMode_WEP = 1;
    public static final int Wifi_ApCliAuthMode_WPA = 2;
    public static final int Wifi_ApCliAuthMode_WPA2 = 3;
    public static final int Wifi_ApCliEncrypType_NONE = 0;
    public static final int Wifi_ApCliEncrypType_WEP = 1;
    public static final int Wifi_ApCliEncrypType_TKIP = 2;
    public static final int Wifi_ApCliEncrypType_AES = 3;
    public static final int Wifi_ApCliChannel_AUTO = 0;
    public static final int Wifi_ApCliChannel_1 = 1;
    public static final int Wifi_ApCliChannel_2 = 2;
    public static final int Wifi_ApCliChannel_3 = 3;
    public static final int Wifi_ApCliChannel_4 = 4;
    public static final int Wifi_ApCliChannel_5 = 5;
    public static final int Wifi_ApCliChannel_6 = 6;
    public static final int Wifi_ApCliChannel_7 = 7;
    public static final int Wifi_ApCliChannel_8 = 8;
    public static final int Wifi_ApCliChannel_9 = 9;
    public static final int Wifi_ApCliChannel_10 = 10;
    public static final int Wifi_ApCliChannel_11 = 11;
    public static final int Wifi_ApCliChannel_12 = 12;
    public static final int Wifi_ApCliChannel_13 = 13;

    // 소켓 이미지
    public static final int icon_01settopbox = 758;
    public static final int icon_02router = 600;
    public static final int icon_03tv = 433;
    public static final int icon_04audio = 912;
    public static final int icon_05cooker = 347;
    public static final int icon_06computer = 326;
    public static final int icon_07laptop = 260;
    public static final int icon_08moniter = 253;
    public static final int icon_09printer = 307;
    public static final int icon_10refrigerator = 490;
    public static final int icon_11phone = 215;
    public static final int icon_12microwave = 277;
    public static final int icon_13lamp = 060;
    public static final int icon_14light = 050;
    public static final int device_icon = 400;

    // 전력 소비량
    public static final int icon_active_01settopbox = 1900;
    public static final int icon_active_02router = 1000;
    public static final int icon_active_03tv = 15000;
    public static final int icon_active_04audio = 4000;
    public static final int icon_active_05cooker = 9500;
    public static final int icon_active_06computer = 12000;
    public static final int icon_active_07laptop = 2200;
    public static final int icon_active_08moniter = 4000;
    public static final int icon_active_09printer = 2500;
    public static final int icon_active_10refrigerator = 490;
    public static final int icon_active_11phone = 700;
    public static final int icon_active_12microwave = 277;
    public static final int icon_active_13lamp = 6000;
    public static final int icon_active_14light = 500;
    public static final int device_acitve_icon = 5000;

    public static final String regular_expression_filter = "^[ _~!@#$%^*(),./?:=+a-zA-Z0-9ㄱ-ㅣ가-힣-]*$";
    public static final String cmd_ping = "ping -c 1 -W 10 ";
    public static final String Msg_GPIO_Status = "REQUEST/5/";
}
