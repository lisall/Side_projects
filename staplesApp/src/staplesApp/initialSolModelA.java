package staplesApp;
import java.util.*;

/**
 * 
 * @author jieyang
 * this class will generate a feasible initial solution
 * for model-A: the basic model for exercise 1
 */

public class initialSolModelA {
	
	ArrayList<request> requests;
	ArrayList<carrier> carriers;
	
	public initialSolModelA(ArrayList<request> r, ArrayList<carrier> c){
		this.requests = r;
		this.carriers = c;
	}
	
	public void assign(){
		// iterate through requests and assign carrier
		Iterator it = requests.iterator();
		while(it.hasNext()) {
	         request r = (request) it.next();
	         // loop through its carriers list
	         // remember this list has already been sorted by cost, low -> high
	         int flag = 0;
	         for (int i=0; i<r.costArray.size();i++){
	        	 cost c = r.costArray.get(i);
	        	 int carrierId = c.carrierId;
	        	 // if selfCheck is true, load > minimum weight requirement
	        	 // we jump over this carrier
	        	 if (carriers.get(carrierId-1).selfCheck()){

	        	 } else {
	        		 // assign carrier id to request r
	        		 r.carrierId = carrierId;
	        		 // update carrier loading situation
	        		 carriers.get(carrierId-1).load += r.weight;
	        		 // reset flag
	        		 flag = 1;
	        		 // leastCost option
	        		 if (i==0){
	        			 r.leastCost = true;
	        		 }
	        	 }
	         }
	         // if all carriers have met the minimum requirement
	         // we choose the least cost option
	         if (flag == 0){
	        	 r.carrierId = r.costArray.get(0).carrierId;
	        	 carriers.get(r.costArray.get(0).carrierId-1).load += r.weight;
	        	 r.leastCost = true;
	         }
	    }
		
		
	}

}
