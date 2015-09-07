package def;

import java.io.*;
import java.util.*;

import com.sun.jersey.api.client.WebResource;

public class OwnMethods {
	
	//Generate Random node_count vertices in the range(0, graph_size) which is attribute id
	public static HashSet<String> GenerateRandomInteger(long graph_size, int node_count)
	{
		HashSet<String> ids = new HashSet();
		
		Random random = new Random();
		while(ids.size()<node_count)
		{
			Integer id = (int) (random.nextDouble()*graph_size);
			ids.add(id.toString());
		}
		
		return ids;
	}
	
	//Generate absolute id in database depends on attribute_id and node label
	public static ArrayList<String> GenerateStartNode(WebResource resource, HashSet<String> attribute_ids, String label)
	{
		String query = "match (a:" + label + ") where a.id in " + attribute_ids.toString() + " return id(a)";
		String result = Neo4j_Graph_Store.Execute(resource, query);
		ArrayList<String> graph_ids = Neo4j_Graph_Store.GetExecuteResultData(result);
		return graph_ids;
	}
	
	//Generate absolute id in database depends on attribute_id and node label
	public static ArrayList<String> GenerateStartNode(HashSet<String> attribute_ids, String label)
	{
		Neo4j_Graph_Store p_neo4j_graph_store = new Neo4j_Graph_Store();
		String query = "match (a:" + label + ") where a.id in " + attribute_ids.toString() + " return id(a)";
		String result = p_neo4j_graph_store.Execute(query);
		ArrayList<String> graph_ids = Neo4j_Graph_Store.GetExecuteResultData(result);
		return graph_ids;
	}
	
	public ArrayList<String> ReadFile(String filename)
	{
		ArrayList<String> lines = new ArrayList<String>();
		
		File file = new File(filename);
		BufferedReader reader = null;
		
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while((tempString = reader.readLine())!=null)
			{
				lines.add(tempString);
			}
			reader.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(reader!=null)
			{
				try
				{
					reader.close();
				}
				catch(IOException e)
				{					
				}
			}
		}
		return lines;
	}
	
	public static void WriteFile(String filename, boolean app, ArrayList<String> lines)
	{
		try 
		{
			FileWriter fw = new FileWriter(filename,app);
			for(int i = 0;i<lines.size();i++)
			{
				fw.write(lines.get(i)+"\n");
			}
			fw.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void WriteFile(String filename, boolean app, String str)
	{
		try 
		{
			FileWriter fw = new FileWriter(filename,app);
			fw.write(str);
			fw.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public static long getDirSize(File file) {     
        if (file.exists()) {     
            if (file.isDirectory()) {     
                File[] children = file.listFiles();     
                long size = 0;     
                for (File f : children)     
                    size += getDirSize(f);     
                return size;     
            } else {
            	long size = file.length(); 
                return size;     
            }     
        } else {     
            System.out.println("File not exists!");     
            return 0;     
        }     
    }
	
	public static int GetNodeCount(String datasource)
	{
		int node_count = 0;
		File file = null;
		BufferedReader reader = null;
		try
		{
			file = new File("/home/yuhansun/Documents/Real_data/"+datasource+"/graph.txt");
			reader = new BufferedReader(new FileReader(file));
			String str = reader.readLine();
			String[] l = str.split(" ");
			node_count = Integer.parseInt(l[0]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return node_count;		
	}
	
	public static String ClearCache()
	{
		//String[] command = {"/bin/bash","-c","echo data| sudo -S ls"};
		String []cmd = {"/bin/bash","-c","echo data | sudo -S sh -c \"sync; echo 3 > /proc/sys/vm/drop_caches\""};
		String result = null;
		try 
		{
			Process process = Runtime.getRuntime().exec(cmd);
			process.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));  
	        StringBuffer sb = new StringBuffer();  
	        String line;  
	        while ((line = br.readLine()) != null) 
	        {  
	            sb.append(line).append("\n");  
	        }  
	        result = sb.toString();
	        result+="\n";
	        
        }   
		catch (Exception e) 
		{  
			e.printStackTrace();
        }
		return result;
	}
	
	public static String RestartNeo4jClearCache(String datasource)
	{
		String result = "";
		result += Neo4j_Graph_Store.StopMyServer(datasource);
		result += ClearCache();
		result += Neo4j_Graph_Store.StartMyServer(datasource);
		return result;
	}
}
