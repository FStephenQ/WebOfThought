package com.quaratiello.webofthought;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class WebWalker {
	
	
	public static SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> prune(SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> in, String vertex, int hops, int level){
		if(level > hops) return null;
		SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> ret = new SimpleDirectedWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		ret.addVertex(vertex);
		DefaultWeightedEdge[] edges = new DefaultWeightedEdge[hops];
		for(DefaultWeightedEdge e:in.outgoingEdgesOf(vertex)){
			int i = hops-1;
			while(i>=0){
				if((!in.outgoingEdgesOf(in.getEdgeTarget(e)).isEmpty() || level ==hops) &&(edges[i] == null || in.getEdgeWeight(edges[i]) <in.getEdgeWeight(e))){
					if(i+1 == hops) 
						edges[i] = e;
					else{
						edges[i+1] = edges[i];
						edges[i] = e;
					}
				}
				i--;
			}
		}
		for(DefaultWeightedEdge e:edges){
			if(e == null);
			else{
				if(!ret.containsVertex(in.getEdgeTarget(e)))
				ret.addVertex(in.getEdgeTarget(e));
			ret.addEdge(vertex,in.getEdgeTarget(e));
			ret.setEdgeWeight(e, in.getEdgeWeight(e));
			ret = mergeGraph(ret,prune(in,in.getEdgeTarget(e),hops,level+1));
			}
		}
		return ret;
	}	
	
	
	
	public static SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> mergeGraph(SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> ret, SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> tmp){
		if(tmp == null) return ret;
		for(String v:tmp.vertexSet()){
			if(!ret.vertexSet().contains(v))
				ret.addVertex(v);
		}
		for(DefaultWeightedEdge e:tmp.edgeSet()){
			if(!ret.edgeSet().contains(e)){
				ret.addEdge(tmp.getEdgeSource(e), tmp.getEdgeTarget(e));
				ret.setEdgeWeight(e, tmp.getEdgeWeight(e));
			}
		}
		return ret;
	}
}
