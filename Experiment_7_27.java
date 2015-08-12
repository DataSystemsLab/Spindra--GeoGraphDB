package def;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

public class Experiment_7_27 {	
	
	private static long graph_size = 693947;
	private static int experiment_node_count = 100;
	private static double spatial_total_range = 1000;
	private static String datasource = "citeseer";
			
	public static void main(String[] args) throws SQLException {
		
		String result_file_path = "/home/yuhansun/Documents/Real_data/"+datasource+"/query_time.txt";
		boolean break_flag = false;
		
		for(int ratio = 20;ratio<100;ratio+=20)
		{
			OwnMethods.WriteFile(result_file_path, true, "ratio=" + ratio + "\n");
			OwnMethods.WriteFile(result_file_path, true, "spatial_range\t"+"traversal_time\t"+"SpatialReachIndex_time\t"+"GeoReach_time\n");
			
			String graph_label = "Graph_Random_" + ratio;
			String filepath = "/home/yuhansun/Documents/Real_data/"+datasource+"/Random_spatial_distributed/" + ratio;
			HashSet<String> hs = OwnMethods.GenerateRandomInteger(graph_size, experiment_node_count);
			ArrayList<String> al = OwnMethods.GenerateStartNode(hs, graph_label);
			OwnMethods.WriteFile(filepath + "/experiment_node.txt", false, al);
					
			for(int j = 1;j<60;j+=10)
			{
				Traversal traversal = new Traversal();
				Spatial_Reach_Index spareach = new Spatial_Reach_Index(datasource + "_Random_" + ratio);
				GeoReach georeach = new GeoReach();
				
				double rect_size = spatial_total_range * Math.sqrt(j/100.0);
				MyRectangle query_rect = new MyRectangle(0, 0, rect_size, rect_size);
				
				long time_traversal = 0,time_reachindex = 0,time_georeach = 0;
				for(int i = 0;i<al.size();i++)
				{					
					System.out.println(i);
					int id = Integer.parseInt(al.get(i));
					System.out.println(id);
					
					traversal.VisitedVertices.clear();
					long start = System.currentTimeMillis();
					boolean result1 = traversal.ReachabilityQuery(id, query_rect);
					time_traversal+=System.currentTimeMillis() - start;
					System.out.println(result1);
					
					start = System.currentTimeMillis();
					boolean result2 = spareach.ReachabilityQuery(id, query_rect);
					time_reachindex+= (System.currentTimeMillis() - start);
					System.out.println(result2);
					
					georeach.VisitedVertices.clear();
					start = System.currentTimeMillis();
					boolean result3 = georeach.ReachabilityQuery(id, query_rect);
					time_georeach+=System.currentTimeMillis() - start;
					System.out.println(result3);
					
					if(result1!=result2 || result1!=result3)
					{
						System.out.println(id);
						System.out.println(rect_size);
						break_flag=true;
						break;
					}
				}
				spareach.Disconnect();
				if(break_flag)
					break;
				OwnMethods.WriteFile(result_file_path, true, j/100.0+"\t"+time_traversal/experiment_node_count+"\t"+time_reachindex/experiment_node_count+"\t"+time_georeach/experiment_node_count+"\n");
			}
			if(break_flag)
				break;
			
			OwnMethods.WriteFile(result_file_path, true, "\n");
		}
	}
}
