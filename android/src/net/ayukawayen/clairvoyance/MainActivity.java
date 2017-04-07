package net.ayukawayen.clairvoyance;

import android.app.TabActivity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	private static final String PREF_KEY_TABTAG = "TAB_TAG";
	
	private TabHost tabHost;
	
	private ScrollTextView readmeView;
	private ScrollTextView licenseView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.tabHost = (TabHost) this.findViewById(android.R.id.tabhost);
		this.tabHost.addTab(this.tabHost
				.newTabSpec("readme")
				.setIndicator(this.getResources().getString(R.string.readme_title))
				.setContent(R.id.scrollTextView1)
		);
		this.tabHost.addTab(this.tabHost
				.newTabSpec("top")
				.setIndicator(this.getResources().getString(R.string.top_title))
				.setContent(R.id.qollieWebView1)
		);
		this.tabHost.addTab(this.tabHost
				.newTabSpec("license")
				.setIndicator(this.getResources().getString(R.string.license_title))
				.setContent(R.id.scrollTextView2)
		);

		this.readmeView = (ScrollTextView) this.findViewById(R.id.scrollTextView1);
		this.licenseView = (ScrollTextView) this.findViewById(R.id.scrollTextView2);
		
		this.readmeView.setContent(R.string.readme_content);
		this.licenseView.setContent(R.string.license_content);
		this.licenseView.getContentTextView().setTextAppearance(this, android.R.style.TextAppearance);
		
		this.tabHost.setCurrentTabByTag(this.getSharedPreferences(MainActivity.class.getCanonicalName(), Context.MODE_PRIVATE)
				.getString(PREF_KEY_TABTAG, "readme")
		);
	}
	
	@Override
	protected void onPause() {
		this.getSharedPreferences(MainActivity.class.getCanonicalName(), Context.MODE_PRIVATE)
				.edit()
				.putString(PREF_KEY_TABTAG, this.tabHost.getCurrentTabTag())
				.apply()
		;
		super.onPause();
	}
	
	@Override
	public void onBackPressed() {
		this.moveTaskToBack(false);
	}
}
