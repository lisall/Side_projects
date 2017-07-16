package staplesApp;
import java.io.*;
import java.util.*;

public class readFile {
	public static readRes readAll () throws IOException {
		
		ArrayList<request> requests = new ArrayList<request>();
		
		/**
		 * read order.csv
		 */
		
		BufferedReader reader1=new BufferedReader (new FileReader ("order.csv"));
		String line1=null;
		String status1="find_data";
		int requestCount = 0;
		
		while ((!(line1=reader1.readLine()).equals("end"))) {
				
			if (status1.equals("find_data")) {
				String[] element0=line1.split(",");
				if (element0[0].equals("start")){
					status1="start_to_read";
			    }
			} else if (status1.equals("start_to_read")) {

				String[] element=line1.split(",");
				request r= new request();
				r.requestId = requestCount;
				r.orderId=element[0];
				r.shipNum=element[1];
				r.address=element[2];
				r.zone=Integer.parseInt(element[3]);
				r.weight=Double.parseDouble(element[4]);	
				requests.add(r);
			
			}
			
			requestCount++;
		
		}
		
		try {
			
			PrintStream out_txt = new PrintStream (new FileOutputStream("ordersCheck"));			
			out_txt.println("OrderId,ShipNum,Address,Zone,Weight");			
			for (int i=0; i<requests.size(); i++){				
				request r=requests.get(i);				
				out_txt.println(r.orderId+","+r.shipNum+","+r.address+","+r.zone+","+r.weight);
			}
		
		} catch(FileNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
		/**
		 * read cost.csv
		 */
		
		BufferedReader reader2=new BufferedReader (new FileReader ("cost.csv"));
		String line2=null;
		String status2="find_data";
		
		// read top line as column (zone #)
		String cols = reader2.readLine();
		String[] colsArray = cols.split(",");
		int carrierId =0;
		int weightCount = 0;
		// create a hash table to store costs
		// first key for carrier id, starting from 1
		// second key for weight id, starting from 1, step size 1
		Hashtable <Integer,Hashtable<Integer,double[]>> costTable = 
				new  Hashtable <Integer,Hashtable<Integer,double[]>>();
		
		while ((!(line2=reader2.readLine()).equals("end"))) {
			
			weightCount += 1;
			String[] element=line2.replace("$","").split(",");
			if (element[0].equals("start")){
				carrierId += 1;
				weightCount = 0;	
			} else {

				double[] values = new double[element.length];
				for (int i=0; i<element.length; i++){
					values[i]=Double.parseDouble(element[i]);
				}
				if (costTable.containsKey(carrierId)){
					costTable.get(carrierId).put(weightCount,values);
				} else {
					Hashtable<Integer,double[]> c = new Hashtable<Integer,double[]>();
					c.put(weightCount,values);
					costTable.put(carrierId,c);
				}
			
			}
		
		}
		
		/**
		 * read carrier's minimum requirement
		 */
		
		BufferedReader reader3=new BufferedReader (new FileReader ("carrier.csv"));
		String line3=null;
		ArrayList<carrier> carriers = new ArrayList<carrier>();
		
		while ((!(line3=reader3.readLine()).equals("end"))) {
			String[] element=line3.split(",");
			carrier c = new carrier(Integer.parseInt(element[0]),Integer.parseInt(element[1]));
			carriers.add(c);
		}
		
		/**
		 * read configuration file
		 */
		BufferedReader reader4=new BufferedReader (new FileReader ("config.txt"));
		String line4=null;
		params paramsList = new params();
		
		while ((!(line4=reader4.readLine()).equals("End"))) {
			String[] element=line4.split("=");
			if (element[0].equals("Neighborhood1")){
				paramsList.neighborhood1 = Double.parseDouble(element[1]);
			} else if (element[0].equals("Neighborhood2")){
				paramsList.neighborhood2 = Double.parseDouble(element[1]);
			} else if (element[0].equals("Neighborhood3")){
				paramsList.neighborhood3 = Double.parseDouble(element[1]);
			}

		}
		
		reader1.close();
		reader2.close();
		reader3.close();
		reader4.close();
		readRes res=new readRes();
		res.requests = requests;
		res.costTable = costTable;
		res.carriers = carriers;
		res.paramList = paramsList;
		
		return res;
		
	}

}


	
