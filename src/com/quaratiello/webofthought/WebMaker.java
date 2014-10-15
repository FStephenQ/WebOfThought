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

public class WebMaker {
	private SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> existingNodes;
	private List<String> commonWords = Arrays.asList("A", "OF","THE"," ","^","TO","THAT","FROM","AND", "HIM");
	
	public WebMaker(String url, SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> existing){
		if(existing == null)
			existing = new SimpleDirectedWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.existingNodes = existing;
		processPage(url);
	}
	
	public void processPage(String URL){
		try {
			Document doc = Jsoup.connect(URL).get();
			String title = doc.getElementById("firstHeading").text();
			System.out.println(title+"\n");
			if(!this.existingNodes.containsVertex(title)){
				this.existingNodes.addVertex(title);
			}
			HashMap<String,Integer> phrases = extractPhrases(doc.getElementById("bodyContent").text());
			for(String s:phrases.keySet()){
				if(!this.existingNodes.containsVertex(s))
					this.existingNodes.addVertex(s);
				//check if the edge already exists; figure out how to re-enforce
				DefaultWeightedEdge e = this.existingNodes.addEdge(title, s);
				this.existingNodes.setEdgeWeight(e, phrases.get(s));
			}
			System.out.println(this.existingNodes);
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
