package staplesApp;
import java.io.*;
import java.util.*;

public class fileOutput {
	ArrayList<request> requests;
	ArrayList<carrier> carriers;
	
	double lowerBound;
	double initialCost;
	double finalCost;
	
	public fileOutput(ArrayList<request> r, ArrayList<carrier> c, double l, double i, double f){
		this.requests = r;
		this.carriers = c;
		this.lowerBound = l;
		this.initialCost = i;
		this.finalCost = f;
	}
	
	public void write(){
		
		try {
			
			PrintStream out_txt = new PrintStream (new FileOutputStream("solution"));
			// output summary
			out_txt.println("total request #: "+requests.size());
			out_txt.println("total carrier #: "+carriers.size());
			for (int i=0; i<carriers.size();i++){
				carrier c = carriers.get(i);
				out_txt.println("carrier"+c.id+" minimum weight requirement (lb): "+c.minWeight);
			}
			out_txt.println("lower bound : $"+lowerBound);
			out_txt.println("initial cost: $"+initialCost);
			out_txt.println("final cost: $"+finalCost);
			out_txt.printf("%-16s %-16s %-16s %-16s\n",
					"Order Id", "Ship Num", "Carrier","Cost");		
			for (int i=0; i<requests.size(); i++){				
				request r=requests.get(i);				
				out_txt.printf("%-16s %-16s %-16s %-16s\n",r.orderId,r.shipNum,r.carrierId,r.costTable.get(r.carrierId));
			}
		
		} catch(FileNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
	}

}
