package net.ayukawayen.clairvoyance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

@SuppressLint("SetJavaScriptEnabled")
public class QollieWebView extends FrameLayout {
	private static String URL = "https://www.qollie.com/";
	
	private WebView webview;
	private ProgressBar progressBar;
	private ImageButton imageButton;
	
	public QollieWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		this.init();
	}

	public QollieWebView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.init();
	}

	public QollieWebView(Context context) {
		super(context);

		this.init();
	}
	
	private void init() {
		View.inflate(this.getContext(), R.layout.view_qollie_web, this);
		
		this.webview = (WebView) this.findViewById(R.id.webView1);
		this.progressBar = (ProgressBar) this.findViewById(R.id.progressBar1);
		this.imageButton = (ImageButton) this.findViewById(R.id.imageButton1);
		
		this.webview.getSettings().setJavaScriptEnabled(true);
		
		this.webview.setWebViewClient(new WebViewClient(){
			@Override
			public void onLoadResource(WebView view, String url) {
				if(!view.getUrl().equals(QollieWebView.URL)) {
					QollieWebView.this.openUrl(view.getUrl());
					view.loadUrl(QollieWebView.URL);
					return;
				}
				super.onLoadResource(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				view.setAlpha(0.5f);
				QollieWebView.this.progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				QollieWebView.this.progressBar.setVisibility(View.GONE);
				view.setAlpha(1.0f);
				super.onPageFinished(view, url);
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				QollieWebView.this.openUrl(url);
				
				return true;
			}
        });
		
		this.webview.setWebChromeClient(new WebChromeClient(){
		});
		
		this.imageButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				QollieWebView.this.webview.reload();
			}
		});
		
		this.webview.loadUrl(QollieWebView.URL);
	}

	private void openUrl(String urlString) {
		Intent intent = new Intent();
		intent.setData(Uri.parse(urlString));
		this.getContext().startActivity(intent);
	}
}
