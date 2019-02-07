package com.tony.builder.game2048.controller;

import java.util.HashMap;
import java.util.Map;

import com.tony.builder.game2048.R;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CardLayout extends FrameLayout {

	private static final String TAG = "CardLayout";
	private int number = 0;
	private TextView textView = null;
	private static Typeface tf = null;
	private static Map<Integer, Integer> colorMap = null;

	public CardLayout(Context context) {
		super(context);
		initColorMap();
		initTypeface();
		initCardLayout();
	}

	private void initColorMap() {
		if (colorMap == null) {
			colorMap = new HashMap<Integer, Integer>();
			colorMap.put(0, R.color.card_0);
			colorMap.put(2, R.color.card_2);
			colorMap.put(4, R.color.card_4);
			colorMap.put(8, R.color.card_8);
			colorMap.put(16, R.color.card_16);
			colorMap.put(32, R.color.card_32);
			colorMap.put(64, R.color.card_64);
			colorMap.put(128, R.color.card_128);
		}
	}

	public CardLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initCardLayout();
	}

	public CardLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initCardLayout();
	}

	private void initCardLayout() {
		textView = new TextView(this.getContext());
		textView.setText(String.valueOf(number));
		textView.setTextSize(32);
		textView.setGravity(Gravity.CENTER);
		int color = getResources().getColor(getColorByNumber(number));
		textView.setBackgroundColor(color);
		textView.setTypeface(getTypeface());

		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		lp.setMargins(5, 5, 5, 5);
		this.addView(textView, lp);
	}

	private void initTypeface() {
		if (tf == null) {
			tf = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
		}
	}

	private Typeface getTypeface() {
		initTypeface();
		return tf;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
		if (number > 0) {
			textView.setText(String.valueOf(number));
		} else {
			textView.setText("");
		}
		int color = getResources().getColor(getColorByNumber(number));
		textView.setBackgroundColor(color);
	}

	public boolean equals(CardLayout card) {
		return getNumber() == card.getNumber();
	}

	public int getColorByNumber(int number) {
		initColorMap();
		Integer colorIndex = colorMap.get(number);

		if (number <= 128) {
			return colorIndex.intValue();
		} else {
			return R.color.card_128;
		}
	}
}
