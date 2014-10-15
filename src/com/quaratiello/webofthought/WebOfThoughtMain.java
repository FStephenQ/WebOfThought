package com.quaratiello.webofthought;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerEdgeNameProvider;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class WebOfThoughtMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WebMaker m =new WebMaker("http://en.wikipedia.org/wiki/Affluenza",null);
		try {
			class MyIntegerEdgeNameProvider extends IntegerEdgeNameProvider<DefaultWeightedEdge>{
				
				SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> graph;
				
				public MyIntegerEdgeNameProvider(SimpleDirectedWeightedGraph<String,DefaultWeightedEdge> g){
					this.graph = g;
				}
				
				@Override
				public String getEdgeName(DefaultWeightedEdge edge) {
					// TODO Auto-generated method stub
					return this.graph.getEdgeWeight(edge)+"";
				}
				
			}
			IntegerEdgeNameProvider<DefaultWeightedEdge> i = new MyIntegerEdgeNameProvider(m.existingNodes);
			new DOTExporter<String, DefaultWeightedEdge>(new IntegerNameProvider<String>(), new StringNameProvider<String>(),i ).export(new FileWriter(new File("D:/image.dot")), m.existingNodes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
}
