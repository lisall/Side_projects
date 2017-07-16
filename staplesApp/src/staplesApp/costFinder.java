package staplesApp;
import java.util.*;

/**
 * 
 * @author jieyang
 * This class is to find each carrier's cost for each request
 * find() also computes average cost across all carriers for each request
 *
 */

public class costFinder {
	
	ArrayList<request> requests = new ArrayList<request>();
	Hashtable <Integer,Hashtable<Integer,double[]>> costTable = 
			new  Hashtable <Integer,Hashtable<Integer,double[]>>();
	
	public costFinder(ArrayList<request> r, Hashtable <Integer,Hashtable<Integer,double[]>> c){
		this.requests = r;
		this.costTable = c;
	}
	
	public void find(){

		for (int i=0; i<requests.size(); i++){
			request r = requests.get(i);
			// get request's zone
			int z = r.zone;
			// get request's weight
			int w = (int)r.weight;
			// get # of carriers
			int num = costTable.size();
			// assign costs
			double costSum = 0;
			for (int j=1;j<=num;j++){
				// zone starts from 2 not 0
				double cost = costTable.get(j).get(w)[z-2];
				costSum = costSum + cost;
				cost cij = new cost(r.requestId,j,cost);
				r.costArray.add(cij);
			}
			r.costAvg = costSum/num;
			// sort the cost array based on cost, low -> high
			Collections.sort(r.costArray);
			
			// create a hash table and add (carrierId, cost)
			for (int k=0; k<r.costArray.size(); k++){
				cost c = r.costArray.get(k);
				r.costTable.put(c.carrierId, c.price);
			}
			
		}
		
		
	}

}
