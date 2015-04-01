/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team11.Parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.team11.webDB.SearchProvider;

/**
 *
 * @author Avinash
 */
public class ListFinderHTML {

	private String myHTML = "";
	private int currentpos = 0;
	private ArrayList<String> mylist; 
	private String header;
	private String description;
	private String title;
	boolean prevtable;
	int currenttablenumber;
	
	public void SetHTML(String myurl){
		myHTML ="";        
		currentpos = -1;
		prevtable= false;
		SearchProvider sp = new SearchProvider();
		myHTML = sp.getPageHtml(myurl).toLowerCase();

		currentpos = 0;
		SetTitle();        
	}



	public ArrayList<String> getNextList(){
		int indextable,indexol,indexul,indexdl,indexselect;
		mylist = null;

		if (prevtable){

			currenttablenumber++;            
			if (tableheaderlist.size()>currenttablenumber) header = tableheaderlist.get(currenttablenumber);
			if (currenttablenumber>=tablelistlist.size())
				prevtable = false;
			else return ProcessAndSend(tablelistlist.get(currenttablenumber));

		}

		if (currentpos ==-1) return null;

		indextable = myHTML.indexOf("<table", currentpos);
		indexol = myHTML.indexOf("<ol", currentpos);
		indexul = myHTML.indexOf("<ul", currentpos);
		indexdl = myHTML.indexOf("<dl", currentpos);
		indexselect = myHTML.indexOf("<select", currentpos);

		if (indextable ==-1 && indexol ==-1 && indexul ==-1 && indexdl ==-1 && indexselect ==-1) {currentpos = -1; return null;}

		if (indextable==-1) indextable = Integer.MAX_VALUE;
		if (indexol==-1) indexol = Integer.MAX_VALUE;
		if (indexul==-1) indexul = Integer.MAX_VALUE;
		if (indexdl==-1) indexdl = Integer.MAX_VALUE;
		if (indexselect==-1) indexselect = Integer.MAX_VALUE;



		if (indextable<=indexol && indextable<=indexul && indextable <=indexdl && indextable <=indexselect)
		{
			currentpos = myHTML.indexOf(">", indextable);
			//    currentpos = indextable + 5 +2;
			HandleTable();        
			currenttablenumber = 0;
			prevtable = true;
			if (tableheaderlist.size()>0) {header = tableheaderlist.get(0);
			return ProcessAndSend(tablelistlist.get(currenttablenumber));}

		}
		else if (indexol<=indextable && indexol<=indexul && indexol <=indexdl && indexol <=indexselect)
		{
			currentpos = myHTML.indexOf(">", indexol);
			//  currentpos = indexol+ 2 +2;
			HandleOl();
		}
		else if (indexul<indexol && indexul<indextable && indexul <indexdl && indexul <indexselect)
		{
			currentpos = myHTML.indexOf(">", indexul);
			//    currentpos = indexul+ 2 +2;
			HandleUl();
		}        
		else if (indexdl<indexol && indexdl<indexul && indexdl <indextable&& indexdl <indexselect)
		{
			currentpos = myHTML.indexOf(">", indexdl);
			//  currentpos = indexdl + 2 +2;
			HandleDl();
		}
		else if (indexselect<indexol && indexselect<indexul && indexselect <indextable&& indexselect <indexdl)
		{
			currentpos = myHTML.indexOf(">", indexselect);
			//  currentpos = indexdl + 2 +2;
			HandleSelect();
		}


		return ProcessAndSend(mylist);
	}                   

	public String getHeader()
	{
		return header;
	}

	public String getTitle()
	{
		return title;
	}
	public String getDescription()
	{
		return description;
	}

	ArrayList<String> tableheaderlist;
	ArrayList<ArrayList<String>> tablelistlist;


	private void HandleTable() {

		mylist = new ArrayList<String>();
		int endpos = myHTML.indexOf("</table>", currentpos);
		int temppos = currentpos;        
		int temp1,temp2;
		int nrows;
		String tempstr;

		SetHeader();

		temp1 = myHTML.indexOf("<tr", currentpos);
		temp1 = myHTML.indexOf(">", temp1)+1;
		temp2 = myHTML.indexOf("</tr>",currentpos);

		if (temp1==0 || temp2==-1 || temp2<temp1) return;

		tempstr = myHTML.substring(temp1, temp2);

		Pattern pattern = Pattern.compile("</th>|</td>");
		Matcher match = pattern.matcher(tempstr);

		nrows =0;

		while(match.find()) nrows++;

		tableheaderlist = new ArrayList<String>();
		tablelistlist = new ArrayList<ArrayList<String>>();        

		pattern = Pattern.compile("(<td>|<th>)([^<>]*)(</td>|</th>)");
		int i;

		for(i =0;i<nrows;i++)
		{
			tablelistlist.add(new ArrayList<String>());
		}

		while(true)
		{
			temp1 = myHTML.indexOf("<tr", temppos);
			if (temp1==-1 || temp1 >endpos) {return;}
			temp1 = myHTML.indexOf(">", temp1)+1;

			temp2 = myHTML.indexOf("</tr>", temp1);                

			if (temp1==0 || temp2 ==-1 || temp2<temp1) return;

			tempstr = myHTML.substring(temp1, temp2);

			match = pattern.matcher(tempstr);

			i=0;

			while(match.find())
			{
				if (i>=nrows)
				{
					nrows++;
					tablelistlist.add(new ArrayList<String>());
				}

				if (match.group(1).equals("<th>"))                
					tableheaderlist.add(match.group(2));
				else tablelistlist.get(i).add(match.group(2));

				i++;            
			}

			temppos = temp2 + 3;        
		}
	}

	private ArrayList<String> ProcessAndSend(ArrayList<String> mylist)
	{
		String str;
		String []strs;
		for(int i =0;i<mylist.size();i++)
		{
			str = RemoveNoise(mylist.get(i));
			//strs = str.split(",");
			//if (strs.length>1)
			//{
			//  mylist.remove(i);
			// for(int j=0;j<strs.length;j++)
			// mylist.add(RemoveNoise(strs[j]));
			// }
			//else 
			mylist.set(i, str);
		}
		return mylist;
	}
	private void SetHeader() {

		String tempheader;
		int headerstart;
		int headerend;

		header ="";
		if(currentpos>10000)
			tempheader = myHTML.substring(currentpos-10000,currentpos);
		else 
			tempheader = myHTML.substring(0, currentpos);


		headerstart = tempheader.lastIndexOf("<h");

		if (headerstart !=-1 && !tempheader.substring(headerstart,headerstart+3).equalsIgnoreCase("<ht")
				&& !tempheader.substring(headerstart,headerstart+3).equalsIgnoreCase("<he")
				)
		{
			headerstart = tempheader.indexOf(">",headerstart)+1;        
			headerend = tempheader.lastIndexOf("</h");        

			if (headerstart== 0 || headerend ==-1 || headerstart > headerend) return;
			//Make sure of the exact numbers here
			//System.out.println(tempheader.length() +" : "+ headerstart + " : "+ headerend);
			header = tempheader.substring(headerstart, headerend);
		}
		header = RemoveNoise(header);
		SetDescription();
	}

	Pattern pattern1 = Pattern.compile(">(\\s*\\w[^<>]*)<");

	private void SetDescription()
	{
		//Setting the description
		String tempheader;
		if(currentpos>250)
			tempheader = myHTML.substring(currentpos-250,currentpos);
		else 
			tempheader = myHTML.substring(0, currentpos);



		Matcher match ;

		match = pattern1.matcher(tempheader);

		//System.out.println(mylist.get(i));
		description = "";
		while(match.find())
		{
			description = match.group(1);
			//System.out.println("1: " + match.group(1));
			//else System.out.println("2: " + header);
		}
		description = RemoveNoise(description);
	}

	private String RemoveNoise(String str)
	{
		Pattern pattern = Pattern.compile("[><]");
		Matcher match ;


		match = pattern1.matcher(str);        

		if (match.find())
			str = match.group(1);



		match = pattern.matcher(str);

		//if (match.find()) return "";
		if (str.indexOf("<")!=-1 || str.indexOf(">")!=-1) return "";
		return str.replaceAll("  +", " ").replaceAll("\n","");
	}


	private void SetTitle()
	{
		int start,end;
		title="";

		start = myHTML.indexOf("<title");
		if (start==-1) return;
		start = myHTML.indexOf(">",start)+1;
		if (start==0) return;
		end = myHTML.indexOf("</title",start);
		if (end==-1) return;
		if (start>end)return;
		title = myHTML.substring(start, end);
	}


	private void HandleSelect()
	{
		mylist = new ArrayList<String>();
		int endpos = myHTML.indexOf("</select>", currentpos);
		int temppos = currentpos;        
		int temp1,temp2;

		SetHeader();

		while(true)
		{
			temp1 = myHTML.indexOf("<option", temppos);
			if (temp1==-1 || temp1 >endpos) return;

			temp1 = myHTML.indexOf(">", temp1)+1;
			temp2 = myHTML.indexOf("</option>", temp1);        
			if (temp2==-1) return;
			if (temp1>temp2) return;
			mylist.add(myHTML.substring(temp1, temp2));
			temppos = temp2 + 5;
		}    
	}


	private void HandleOl() {

		mylist = new ArrayList<String>();
		int endpos = myHTML.indexOf("</ol>", currentpos);
		int temppos = currentpos;        
		int temp1,temp2;

		SetHeader();

		while(true)
		{
			temp1 = myHTML.indexOf("<li", temppos);
			if (temp1==-1 || temp1 >endpos) return;

			temp1 = myHTML.indexOf(">", temp1)+1;        
			temp2 = myHTML.indexOf("</li>", temp1);        
			if (temp2==-1) return;
			if (temp1>temp2) return;
			mylist.add(myHTML.substring(temp1, temp2));
			temppos = temp2 + 3;
		}

	}

	private void HandleUl() {

		mylist = new ArrayList<String>();
		int endpos = myHTML.indexOf("</ul>", currentpos);
		int temppos = currentpos;        
		int temp1,temp2;

		SetHeader();

		while(true)
		{
			temp1 = myHTML.indexOf("<li", temppos);
			if (temp1==-1 || temp1 >endpos) return;
			temp1 = myHTML.indexOf(">", temp1)+1;        

			temp2 = myHTML.indexOf("</li>", temp1);        
			if (temp2==-1) return;
			if(temp1>temp2) return;
			mylist.add(myHTML.substring(temp1, temp2));
			temppos = temp2 + 5;
		}
	}

	private void HandleDl() {

		mylist = new ArrayList<String>();
		int endpos = myHTML.indexOf("</dl>", currentpos);
		int temppos = currentpos;        
		int temp1,temp2;

		SetHeader();

		while(true)
		{
			temp1 = myHTML.indexOf("<dt", temppos);
			if (temp1==-1 || temp1 >endpos) return;
			temp1 = myHTML.indexOf(">", temp1)+1;        

			temp2 = myHTML.indexOf("</dt>", temp1);        
			if (temp2==-1) return;
			if(temp1>temp2) return;

			mylist.add(myHTML.substring(temp1, temp2));
			temppos = temp2 + 3;
		}
	}
}