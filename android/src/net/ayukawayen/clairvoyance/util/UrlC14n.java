package net.ayukawayen.clairvoyance.util;

import java.util.regex.Pattern;

import android.net.Uri;

public abstract class UrlC14n {
	public static UrlC14n[] instances = {
		new _104m(),
		new _104(),
		new _1111(),
		new _518(),
		new _yes123(),
	};
	
	abstract protected Pattern[] getPatterns();
	abstract protected String c14n(String urlString);
	
	protected boolean isMatch(String urlString) {
		Pattern[] patterns = this.getPatterns();
		for(Pattern pattern : patterns) {
			if(pattern.matcher(urlString).matches()) {
				return true;
			}
		}
		return false;
	}
	public String getC14nUrl(String urlString) {
		if(!this.isMatch(urlString)) {
			return null;
		}
		return this.c14n(urlString);
	}
	
	
	public static class _104 extends UrlC14n {
		private static Pattern[] patterns = {
			Pattern.compile("http(s)?://(www.)?104.com.tw/job/\\?jobno=(.*)"),
		};

		@Override
		protected Pattern[] getPatterns() {
			return patterns;
		}
		@Override
		protected String c14n(String urlString) {
			Uri uri = Uri.parse(urlString);
			String param = uri.getQueryParameter("jobno");
			
			return "www.104.com.tw/job/?jobno="+param;
		}
	}
	public static class _104m extends UrlC14n {
		private static Pattern[] patterns = {
			Pattern.compile("http(s)?://m.104.com.tw/job/(.*)"),
		};

		@Override
		protected Pattern[] getPatterns() {
			return patterns;
		}
		@Override
		protected String c14n(String urlString) {
			Uri uri = Uri.parse(urlString);
			String param = uri.getLastPathSegment();
			
			return "www.104.com.tw/job/?jobno="+param;
		}
	}
	public static class _1111 extends UrlC14n {
		private static Pattern[] patterns = {
			Pattern.compile("http(s?)://(www.)?1111.com.tw(/+)job-bank/job-description.asp\\?eNo=(.*)"),
			Pattern.compile("http(s?)://(www.)?1111.com.tw(/+)mobileWeb/job-description.asp\\?eNo=(.*)"),
		};

		@Override
		protected Pattern[] getPatterns() {
			return patterns;
		}
		@Override
		protected String c14n(String urlString) {
			Uri uri = Uri.parse(urlString);
			String param = uri.getQueryParameter("eNo");
			return "www.1111.com.tw/job-bank/job-description.asp?eNo="+param;
		}
	}
	public static class _518 extends UrlC14n {
		private static Pattern[] patterns = {
			Pattern.compile("http(s?)://(www.)?518.com.tw/(.+)-job-(.+).html(.*)"),
			Pattern.compile("http(s?)://m.518.com.tw/(.+)-job-(.+).html(.*)"),
		};
		
		@Override
		protected Pattern[] getPatterns() {
			return patterns;
		}
		@Override
		protected String c14n(String urlString) {
			Uri uri = Uri.parse(urlString);
			String param = uri.getPath();
			
			return "www.518.com.tw/"+param;
		}
	}
	public static class _yes123 extends UrlC14n {
		private static Pattern[] patterns = {
			Pattern.compile("http(s?)://(www.)?yes123.com.tw/admin/job_refer_comp_job_detail2.asp(.*)"),
		};
		
		@Override
		protected Pattern[] getPatterns() {
			return patterns;
		}
		@Override
		protected String c14n(String urlString) {
			Uri uri = Uri.parse(urlString);
			String param = uri.getPath() + "/?" + uri.getEncodedQuery();
			
			return "www.yes123.com.tw/"+param;
		}
	}
}
