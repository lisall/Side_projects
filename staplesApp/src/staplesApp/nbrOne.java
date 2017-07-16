package staplesApp;
import java.util.*;
import java.util.Random;

/**
 * @author jieyang
 * this class is for neighborhood search
 * it is reassign neighborhood
 */

public class nbrOne {
	
	ArrayList<request> requests;
	ArrayList<carrier> carriers;
	
	public nbrOne(ArrayList<request> r, ArrayList<carrier> c){
		this.requests = r;
		this.carriers = c;
	}
	
	private int randomInt(int max,int min){
		Random rand = null;
		int randomNum = rand.nextInt((max - min) + 1) + min;
		
		return randomNum;
	}
	
	public void search(double p){
		
		// select requests: request that not using least cost
		ArrayList<request> candidates = new ArrayList<request>();
		Iterator it = requests.iterator();
		while(it.hasNext()){
			request r = (request) it.next();
			if (r.leastCost == false){
				candidates.add(r);
			}
		}

		// randomly shuffle candidates list
		Collections.shuffle(candidates);
		
		// loop through a fraction of candidates
		int pos = 0;
		int maxIter = (int) p*candidates.size()-1;
		while (pos <= maxIter){

			request r = candidates.get(pos);
			// if the weight reduction is higher than (loading-minimum_weight)
			// then if we reassign, the constraint will be violated
			double delta = carriers.get(r.carrierId-1).load-carriers.get(r.carrierId-1).minWeight;
			if (delta>=r.weight){
				// we reassign
				int prev = r.carrierId;
				int current = r.costArray.get(0).carrierId;
				r.carrierId = current;
				// update carrier load
				carriers.get(current-1).load += r.weight;
				carriers.get(prev-1).load -= r.weight;
				// set leastCost boolean
				r.leastCost = true;
			} 
			
			pos+=1;
			
		}
		
	}

}
