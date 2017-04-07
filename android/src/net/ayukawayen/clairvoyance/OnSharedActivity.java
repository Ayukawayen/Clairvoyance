package net.ayukawayen.clairvoyance;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

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
	
	private String jobUrlString = "";
	
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
		
		this.imageView.setVisibility(View.INVISIBLE);
		this.progressBar.setVisibility(View.VISIBLE);
		this.textView.setText(R.string.loading);
		
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
			new LoadApiThread(c14nUrlString).start();
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
	
	private void openUrl() {
		Intent intent = new Intent();
		intent.setData(Uri.parse(this.jobUrlString));
		this.startActivity(intent);
		
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
	
	private class LoadApiThread extends Thread {
		private String query; 
		
		public LoadApiThread(String urlString) throws MalformedURLException {
			this.query = "{\"query\":\"\\nquery getJob(\\n\\t$id: ID\\n\\t$jobLink: String\\n){\\n\\tgetJob(query: {\\n\\t\\tid: $id\\n\\t\\tjobLink: $jobLink\\n\\t}) "
					+ "{\\n\\t\\t_id\\n\\t\\tcomments}\\n}\\n\","
					+ "\"variables\":{\"jobLink\":\""
					+ urlString
					+ "\"}}";
		}
		
		@Override
		public void run() {
			try {
				this._run();
			} catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				OnSharedActivity.this.onError(R.string.err_loading, 500);
			}
		}
		
		private void _run() throws IOException, JSONException {
			HttpsURLConnection conn = (HttpsURLConnection) new URL("https", "www.qollie.com", "/graphql").openConnection();
			
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			OutputStream output = conn.getOutputStream();
			
			output.write(this.query.getBytes());
			
			conn.connect();
			
			InputStream input = conn.getInputStream();
			
			String result = "";
			byte[] buffer = new byte[1024];
			while(true) {
				int len = input.read(buffer);
				if(len<=0) {
					break;
				}
				
				result += new String(buffer, 0, len);
			}
			
			JSONObject jsonResult = new JSONObject(result);
			JSONObject jsonData = jsonResult.getJSONObject("data");
			
			int cntPost = 0;
			if(!jsonData.isNull("getJob")) {
				jsonData = jsonData.getJSONObject("getJob");
				cntPost = jsonData.getJSONArray("comments").length();
				String jobId = jsonData.getString("_id");
				OnSharedActivity.this.jobUrlString = "https://www.qollie.com/jobs/"+jobId+"/";
			}

			OnSharedActivity.this.runOnUiThread(new OnCountLoadedThread(cntPost));
				
		}
	}
	
	private class OnCountLoadedThread extends Thread {
		private int cntPost;
		
		public OnCountLoadedThread(int cntPost) {
			this.cntPost = cntPost;
		}
		
		@Override
		public void run() {
			if(this.cntPost > 0) {
				OnSharedActivity.this.openUrl();
				return;
			}
			
			OnSharedActivity.this.imageView.setVisibility(View.GONE);
			OnSharedActivity.this.progressBar.setVisibility(View.GONE);
			OnSharedActivity.this.textView.setText(R.string.zeroPost);
		}
	}
}
