package net.scorgister.web.crawler;

public class PageData {
	
	private String url;
	private String title;
	private String mimeType;

	public PageData(String url, String title, String mimeType) {
		this.title = title;
		this.mimeType = mimeType;
		this.url = url;
	}
	
	
	public String getURL() {
		return url;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	
	@Override
	public String toString() {
		return "[url=" + url + ", title=" + title + ", mimeType=" + mimeType + "]";
	}

}
