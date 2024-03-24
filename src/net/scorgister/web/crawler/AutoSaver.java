package net.scorgister.web.crawler;

public class AutoSaver extends Thread {
	
	private WebCrawler crawler;
	
	private boolean running;
	
	private float min = 1;
	
	public AutoSaver(WebCrawler crawler) {
		this.crawler = crawler;
	}
	
	@Override
	public void run() {
		running = true;
		try {
			while(running) {
				Thread.sleep((long) (60000 * min));
				crawler.saveSiteMapToJson();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void setInterval(float min) {
		this.min = min;
	}
	
	public void stopAutoSaver() {
		running = false;
	}

}
