package net.ayukawayen.clairvoyance;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import net.ayukawayen.clairvoyance.util.UrlC14n;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class OnSharedActivity extends Activity {
	private ProgressBar progressBar;
	private ImageView imageView;
	private TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onshared);
		
		this.progressBar = (ProgressBar) this.findViewById(R.id.progressBar1);
		this.imageView = (ImageView) this.findViewById(R.id.imageView1);
		this.textView = (TextView) this.findViewById(R.id.textView1);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		this.textView.setText(R.string.loading);
		this.imageView.setVisibility(View.INVISIBLE);
		this.progressBar.setVisibility(View.VISIBLE);
		
		Intent intent = this.getIntent();
		if(intent == null) {
			this.finish();
			return;
		}
		if(!intent.getAction().equals(Intent.ACTION_SEND)) {
			this.finish();
			return;
		}
		
		String urlString = intent.getStringExtra(Intent.EXTRA_TEXT);
		String c14nUrlString = getC14nUrl(urlString);
		if(c14nUrlString == null) {
			this.onError(R.string.err_url_not_supported, 400);
			return;
		}
		
		try {
			new LoadDisqusThread(c14nUrlString).start();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			this.onError(R.string.err_internal, 600);
			return;
		}
	}
	
	
	private static String getC14nUrl(String urlString) {
		for(UrlC14n c14n : UrlC14n.instances) {
			String c14ned = c14n.getC14nUrl(urlString);
			if(c14ned != null) {
				return c14ned;
			}
		}
		return null;
	}
	
	private void onDisqusLoaded(String disqusUrlString) {
		this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(disqusUrlString)));
		
		this.finish();
	}
	private void onError(int resId, Integer code) {
		this.onError(this.getResources().getString(resId), code);
	}
	private void onError(String message, Integer code) {
		String text = message + (code==null ? "" : "\n"+this.getResources().getString(R.string.err_code_hint)+code);
		
		this.textView.setText(text);
		this.progressBar.setVisibility(View.INVISIBLE);
		this.imageView.setVisibility(View.VISIBLE);
	}
	
	private class LoadDisqusThread extends Thread {
		private URL embedUrl;
		
		@SuppressWarnings("deprecation")
		public LoadDisqusThread(String urlString) throws MalformedURLException {
			String path;
			try {
				path = "embed/comments/?"
						+ "base=default"
						+ "&version=abfa4959dc34e1f6ae72ea1155d08c72"
						+ "&f=clv-bakc-end"
						+ "&t_u=" + URLEncoder.encode(urlString, "UTF-8")
						+ "&s_o=default";
			} catch (UnsupportedEncodingException e) {
				path = "embed/comments/?"
						+ "base=default"
						+ "&version=abfa4959dc34e1f6ae72ea1155d08c72"
						+ "&f=clv-bakc-end"
						+ "&t_u=" + URLEncoder.encode(urlString)
						+ "&s_o=default";
			}
			
			this.embedUrl = new URL("http", "disqus.com", path);
		}
		
		@Override
		public void run() {
			Document doc;
			
			try {
				doc = Jsoup.parse(this.embedUrl, 30000);
				
			} catch (IOException e) {
				e.printStackTrace();
				OnSharedActivity.this.onError(R.string.err_loading, 500);
				return;
			}
			
			String strDisqusData = doc.select("#disqus-threadData").first().data();
			
			try {
				JSONObject jsonDisqusData = new JSONObject(strDisqusData);
				String slug = jsonDisqusData.getJSONObject("response").getJSONObject("thread").getString("slug");
				String disqusUrlString = "https://disqus.com/home/discussion/clv-bakc-end/"+slug+"/";

				OnSharedActivity.this.onDisqusLoaded(disqusUrlString);
				
			} catch (JSONException e) {
				e.printStackTrace();
				OnSharedActivity.this.onError(R.string.err_loading, 500);
				return;
			}
		}
	}
}
