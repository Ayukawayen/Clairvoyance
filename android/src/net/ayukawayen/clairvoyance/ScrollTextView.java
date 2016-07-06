package net.ayukawayen.clairvoyance;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScrollTextView extends LinearLayout {
	private TextView textContent;
	
 	public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		this.init();
	}

	public ScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.init();
	}

	public ScrollTextView(Context context) {
		super(context);

		this.init();
	}

	private void init() {
		View.inflate(this.getContext(), R.layout.view_scroll_text, this);
		
		this.textContent = (TextView) this.findViewById(R.id.textView1);
	}
	
	public void setContent(int resId) {
		this.textContent.setText(resId);
	}
	public TextView getContentTextView() {
		return this.textContent;
	}
}
