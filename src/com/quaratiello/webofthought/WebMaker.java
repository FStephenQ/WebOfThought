package com.quaratiello.webofthought;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;




import org.jgraph.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebMaker {
	public SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> existingNodes;
	private List<String> commonWords = Arrays.asList("A", "OF","THE"," ","^","TO","THAT","FROM","AND", "HIM");
	private List<String> previouslyAdded;

	public WebMaker(String url, SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> existing){
		if(existing == null)
			existing = new SimpleDirectedWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.previouslyAdded = new ArrayList<String>();
		this.existingNodes = existing;
		processPage(url,2,null);
		//System.out.println(this.existingNodes);
	}

	public void processPage(String URL, int hops, String parent){
		if(hops <= 0 || this.previouslyAdded.contains(parent)) return;
		try {
			Document doc = Jsoup.connect(URL).get();
			String title = doc.getElementById("firstHeading").text();
			System.out.println(title+"\n");
			if(!this.existingNodes.containsVertex(title)){
				this.existingNodes.addVertex(title);
			}
			if(parent != null && title.toLowerCase().equals(parent.toLowerCase())) return;
			List<String> urls = new ArrayList<String>();
			String body = doc.getElementById("bodyContent").text();
			HashMap<String,Integer> phrases = new HashMap<String,Integer>()/*extractPhrases(doc.getElementById("bodyContent").text())*/;
			Elements links = doc.getElementsByTag("a");
			for(int i = 0;i<links.size();i++){
				Element e = links.get(i);
				String text = e.text();
				String url = e.attr("href");
				if(text == null || url == null || 
						(text.isEmpty() || url.isEmpty()) || !url.contains("wiki") ||
						(text.startsWith("ISBN") ||text.equals("edit")));
				else{
					
						urls.add(url);
					String tempStr = body;
					int reps = 0;
					while(e.text() != "" && tempStr.indexOf(e.text()) >= 0){
						tempStr = tempStr.substring(tempStr.indexOf(e.text())+e.text().length());
						reps++;
					}
					if(reps > 1 && !e.text().toLowerCase().equals( title.toLowerCase())) {
						phrases.put(e.text().replace("\"", ""),reps);
					}
				}
			}
			//System.out.println(phrases);
			for(String s:phrases.keySet()){
				if(!this.existingNodes.containsVertex(s))
					this.existingNodes.addVertex(s);
				if(title.equals(s)) break;
				//check if the edge already exists; figure out how to re-enforce
				DefaultWeightedEdge e = this.existingNodes.addEdge(title, s);
				if(e != null)this.existingNodes.setEdgeWeight(e, phrases.get(s));
			}
			for(String s:urls){
				if(!s.contains("http") && !s.contains(".m."))
					this.processPage("http://en.wikipedia.org"+s,hops-1,title);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public HashMap<String,Integer> extractPhrases(String s){
		HashMap<String,Integer> ret = new HashMap<String,Integer>();
		s = s.replace('\n', ' ');
		SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> tmp = new SimpleDirectedWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		String[] strings = s.split("[ \t]"); 
		for(int i = 1; i<strings.length-2;i+=1){
			Double d = Math.random();
			String past = strings[i-1].toUpperCase();
			if(!tmp.containsVertex(past)){
				tmp.addVertex(past);
			}
			String current = strings[i].toUpperCase();
			if(!tmp.containsVertex(current)){
				tmp.addVertex(current);
			}
			String future = strings[i+1].toUpperCase();
			if(!tmp.containsVertex(future)){
				tmp.addVertex(future);
			}
			DefaultWeightedEdge e = tmp.getEdge(past, current);
			if(e==null){
				//e = tmp.getEdge(current, past);
				//if(e == null)
				tmp.setEdgeWeight(tmp.addEdge(past, current),d);
				//else{
				//tmp.setEdgeWeight(tmp.addEdge(past, current),tmp.getEdgeWeight(e));
				//}
			}
			else{
				tmp.setEdgeWeight(e, tmp.getEdgeWeight(e)+1.0);
			}
			e = tmp.getEdge(current, future);
			if(e==null){
				//e = tmp.getEdge(future, current);
				//if(e == null)
				tmp.setEdgeWeight(tmp.addEdge( current,future),d);
				//else{
				//tmp.setEdgeWeight(tmp.addEdge(current,future),tmp.getEdgeWeight(e));
				//}
			}
			else{
				tmp.setEdgeWeight(e, tmp.getEdgeWeight(e)+1.0);
			}
		}
		System.out.println(tmp);
		this.in = tmp;
		ret = traverse();
		System.out.println(ret);
		return ret;
	}
	private SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> in;

	public HashMap<String,Integer> traverse(){
		List<DefaultWeightedEdge> edges = new ArrayList<DefaultWeightedEdge>();
		for(DefaultWeightedEdge e:in.edgeSet()){
			//if(in.getEdgeWeight(e) > 1.0){
			edges.add(e);
			//}
		}
		HashMap<String,Integer> tmp = new HashMap<String,Integer>();
		for(DefaultWeightedEdge e:edges){
			double reps = in.getEdgeWeight(e);
			String key = followPath(in,in.getEdgeSource(e),reps);
			key = in.getEdgeSource(e);
			if(tmp.containsKey(key) || this.commonWords.contains(key));
			else{
				boolean b = true;
				List<String> strings = new ArrayList<String>();
				for(String s:tmp.keySet()){
					if(s.contains(key)){
						b=false;
					}
					else if(key.contains(s)){
						strings.add(s);
					}
				}
				for(String s:strings){
					tmp.remove(s);
				}
				if(b)tmp.put(key, (int) reps);
			}
		}
		return tmp;
	}

	public String followPath(SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> in, String v, double weight){
		if(v=="" || v==null || v==" ") return "";
		String before ="";
		String after = "";
		for(DefaultWeightedEdge e : in.incomingEdgesOf(v)){
			if(in.getEdgeWeight(e)%1.0<=weight%1.0){
				before=in.getEdgeSource(e);
				break;
			}
		}
		for(DefaultWeightedEdge e : in.outgoingEdgesOf(v)){
			if(in.getEdgeWeight(e)%1.0<=weight%1.0){
				after=in.getEdgeSource(e);
				break;
			}
		}
		if(before == v || after == v) return "";
		else {
			before = followPath(in,before,weight);
			after = followPath(in,after,weight);
			return before+" "+v+" "+after;
		}
	}
}
