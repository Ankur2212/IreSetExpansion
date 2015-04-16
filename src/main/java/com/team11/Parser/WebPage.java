/*
	Organizes a webpage 
*/
package com.team11.Parser;

import java.util.ArrayList;

public class WebPage {
	String title;
	String url;
	String snippest;
	ArrayList<WebList> allList;
	
	public WebPage() {
		// TODO Auto-generated constructor stub
	}
	
	public WebPage(String title, String url) {
		this.title = title;
		this.url = url;
		allList = new ArrayList<WebList>();
	}
	
	public WebPage(String title, String url, String snippest) {
		this.title = title;
		this.url = url;
		this.snippest=snippest;
		allList = new ArrayList<WebList>();
	}

	public WebPage(String title, String url, String snippest, ArrayList<WebList> allList) {
		this.title = title;
		this.url = url;					// set the title, url, lists, content
		this.snippest=snippest;
		this.allList = allList;
	}
	
	public String getSnippest() {
		return snippest;
	}

	public void setSnippest(String snippest) {
		this.snippest = snippest;
	}

	public WebPage(String title, String url, ArrayList<WebList> allList) {
		this.title = title;
		this.url = url;
		this.allList = allList;
	}
	
	public void addList(WebList list){
		getAllList().add(list);
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ArrayList<WebList> getAllList() {
		return allList;
	}
	public void setAllList(ArrayList<WebList> allList) {
		this.allList = allList;
	}
	
	@Override
	public String toString() {
		return  "Titile: " + title + "\n"+
				"Url: " + url + "\n"+
				"Snippest:" + snippest +"\n" +
				"allList:" + allList ;
	}
}
