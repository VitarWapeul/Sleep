package sleep.simdori.com.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import sleep.simdori.com.R;

/**
 * 홈화면에서
 * ID 찾기 알림창을 먼저 보여준다. 
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.adapter
 * @since       Android 5.1
 */
public class CustomDialog_State extends Dialog {

	// Views
	private LinearLayout customDialog_select_state, state_Linear;
	private TextView customDialog_state_walk_tv;
	private TextView customDialog_state_relax_tv;
	private TextView customDialog_state_exercise_tv;
	private TextView customDialog_state_chill_tv;
	private TextView customDialog_state_tv;
	private Button customDialog_state_btn_Cancel, customDialog_state_btn_Confirm;

	/**
	 * 커스텀 다이얼로그를 초기화하는 함수
	 * <View>
	 * customDialog_select_state 	: 걷기/휴식/아완/운동 선택창
	 * customDialog_state_walk_tv 			: 걷기
	 * customDialog_state_relax_tv			: 휴식
	 * customDialog_state_exercise_tv 	: 운동
	 * customDialog_state_chill_tv	: 이완
	 * customDialog_state_tv		: 현재 고른 상태 표시
	 * customDialog_state_btn_Cancel 	: 취소 버튼
	 * customDialog_state_btn_Confirm 	: 확인 버튼
	 * @param context
	 */

	public CustomDialog_State(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams();
		//팝업 외부 뿌연 효과
		layoutParams.flags= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		//뿌연 효과 정도
		layoutParams.dimAmount= 1.0f;
		//적용
		getWindow().setAttributes(layoutParams);
		View view = View.inflate(context, R.layout.customdialog_state, null);
		setContentView(view);
		setCancelable(true);

		// 현재상태 선택창
		customDialog_select_state = (LinearLayout) findViewById(R.id.customDialog_select_state);

		state_Linear = (LinearLayout) findViewById(R.id.state_Linear);
		customDialog_state_walk_tv = (TextView) findViewById(R.id.customDialog_state_walk_tv);
		customDialog_state_relax_tv = (TextView) findViewById(R.id.customDialog_state_relax_tv);
		customDialog_state_exercise_tv = (TextView) findViewById(R.id.customDialog_state_exercise_tv);
		customDialog_state_chill_tv = (TextView) findViewById(R.id.customDialog_state_chill_tv);
		customDialog_state_tv = (TextView) findViewById(R.id.customDialog_state_tv);
		customDialog_state_btn_Cancel = (Button)findViewById(R.id.customDialog_state_btn_Cancel);
		customDialog_state_btn_Confirm = (Button)findViewById(R.id.customDialog_state_btn_Confirm);

	}
	
	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		super.setOnDismissListener(listener);
	}

	public LinearLayout getCustomDialog_select_state() {
		return customDialog_select_state;
	}

	public void setCustomDialog_select_state(LinearLayout customDialog_select_state) {
		this.customDialog_select_state = customDialog_select_state;
	}

	public LinearLayout getState_Linear() {
		return state_Linear;
	}

	public void setState_Linear(LinearLayout state_Linear) {
		this.state_Linear = state_Linear;
	}

	public TextView getCustomDialog_state_walk_tv() {
		return customDialog_state_walk_tv;
	}

	public void setCustomDialog_state_walk_tv(TextView customDialog_state_walk_tv) {
		this.customDialog_state_walk_tv = customDialog_state_walk_tv;
	}

	public TextView getCustomDialog_state_relax_tv() {
		return customDialog_state_relax_tv;
	}

	public void setCustomDialog_state_relax_tv(TextView customDialog_state_relax_tv) {
		this.customDialog_state_relax_tv = customDialog_state_relax_tv;
	}

	public TextView getCustomDialog_state_exercise_tv() {
		return customDialog_state_exercise_tv;
	}

	public void setCustomDialog_state_exercise_tv(TextView customDialog_state_exercise_tv) {
		this.customDialog_state_exercise_tv = customDialog_state_exercise_tv;
	}

	public TextView getCustomDialog_state_chill_tv() {
		return customDialog_state_chill_tv;
	}

	public void setCustomDialog_state_chill_tv(TextView customDialog_state_chill_tv) {
		this.customDialog_state_chill_tv = customDialog_state_chill_tv;
	}

	public TextView getCustomDialog_state_tv() {
		return customDialog_state_tv;
	}

	public void setCustomDialog_state_tv(TextView customDialog_state_tv) {
		this.customDialog_state_tv = customDialog_state_tv;
	}

	public Button getCustomDialog_state_btn_Cancel() {
		return customDialog_state_btn_Cancel;
	}

	public void setCustomDialog_state_btn_Cancel(Button customDialog_state_btn_Cancel) {
		this.customDialog_state_btn_Cancel = customDialog_state_btn_Cancel;
	}

	public Button getCustomDialog_state_btn_Confirm() {
		return customDialog_state_btn_Confirm;
	}

	public void setCustomDialog_state_btn_Confirm(Button customDialog_state_btn_Confirm) {
		this.customDialog_state_btn_Confirm = customDialog_state_btn_Confirm;
	}
} 