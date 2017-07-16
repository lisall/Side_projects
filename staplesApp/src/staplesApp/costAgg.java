package staplesApp;
import java.util.*;

/**
 * @author jieyang
 * this class is to aggregate cost
 * and output total cost
 */

public class costAgg {
	
	ArrayList<request> requests;
	
	public costAgg(ArrayList<request> r){
		this.requests = r;
	}
	
	public double compute(){
		Iterator it = requests.iterator();
		double totalCost = 0;
		while (it.hasNext()){
			request r = (request) it.next();
			// create a hash table and add (carrierId, cost)
			totalCost += r.costTable.get(r.carrierId);
			
		}
		
		return totalCost;
	}
	
	public double computeLB(){
		Iterator it = requests.iterator();
		double totalCost = 0;
		while (it.hasNext()){
			request r = (request) it.next();
			// create a hash table and add (carrierId, cost)
			totalCost += r.costArray.get(0).price;
			
		}
		
		return totalCost;
	}

}
