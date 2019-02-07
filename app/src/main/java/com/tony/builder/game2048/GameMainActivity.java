package com.tony.builder.game2048;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.TextView;

public class GameMainActivity extends Activity {

	private static GameMainActivity mGameActivity = null;
	private TextView tvScore = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game_main);
		mGameActivity = this;
		tvScore = findViewById(R.id.tvScore);
	}

	public static GameMainActivity getInstance() {
		return mGameActivity;
	}

	public static final int EVENT_FINISH_ACTIVITY = 0;
	public static final int EVENT_RESET_GAME = 1;
	public static final int EVENT_ADD_SCORE = 2;

	public Handler h = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_FINISH_ACTIVITY:
				mGameActivity.finish();
				break;
			case EVENT_RESET_GAME:
				tvScore.setText("0");
				break;
			case EVENT_ADD_SCORE:
				int current = Integer.valueOf((String) tvScore.getText());
				tvScore.setText("" + (msg.arg1 + current));
				break;
			default:
				break;
			}
		};
	};
}
