package com.quaratiello.webofthought;

import java.io.IOException;
import java.util.HashMap;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebMaker {
	private SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> existingNodes;
	
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
		for(int i = 1; i<strings.length-2;i++){
			String past = strings[i-1];
			if(!tmp.containsVertex(past)){
				tmp.addVertex(past);
			}
			String current = strings[i];
			if(!tmp.containsVertex(current)){
				tmp.addVertex(current);
			}
			String future = strings[i+1];
			if(!tmp.containsVertex(future)){
				tmp.addVertex(future);
			}
			DefaultWeightedEdge e = tmp.getEdge(past, current);
			if(e==null){
				tmp.setEdgeWeight(tmp.addEdge(past, current),1);
			}
			else{
				tmp.setEdgeWeight(e, tmp.getEdgeWeight(e));
			}
			e = tmp.getEdge(current, future);
			if(e==null){
				tmp.setEdgeWeight(tmp.addEdge(current, future),1);
			}
			else{
				tmp.setEdgeWeight(e, tmp.getEdgeWeight(e)+1);
			}
		}
		System.out.println(tmp);
		return ret;
	}
}
