package staplesApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class nbrTwo {
	
	ArrayList<request> requests;
	ArrayList<carrier> carriers;
	
	public nbrTwo(ArrayList<request> r, ArrayList<carrier> c){
		this.requests = r;
		this.carriers = c;
	}
	
	private int randomInt(int max,int min){
		Random rand = null;
		int randomNum = rand.nextInt((max - min) + 1) + min;
		
		return randomNum;
	}
	
	public void search(double p){

		// generate a list of all combinations
		combination comb = new combination(requests.size(),2);
		List<List<Integer>> combList = comb.combine();
		
		// shuffle this list
		Collections.shuffle(combList);
		
		// since it's pair, we allow n*n as maximum (ideally it is C(n,2))
		int maxIter = (int) p*combList.size();
		int count = 0;
		
		// select two requests and swap their assignments
		while (count<maxIter){
			// combination index starts from 1 so we subtract 1
			int pos1 = combList.get(count).get(0)-1;
			int pos2 = combList.get(count).get(1)-1;
			request r1 = requests.get(pos1);
			request r2 = requests.get(pos2);
			
			// identify whether swap or not
			int carrierId1 = r1.carrierId;
			int carrierId2 = r2.carrierId;
			Boolean swapFlag = false;
			// compute delta cost for this swap
			double deltaCost1 = - r1.costTable.get(carrierId1) + r1.costTable.get(carrierId2);
			double deltaCost2 = - r2.costTable.get(carrierId2) + r2.costTable.get(carrierId1);
			// compute loading situation
			double deltaLoad1 = -r1.weight + r2.weight;
			double deltaLoad2 = -r2.weight + r1.weight;
			carrier c1 = carriers.get(carrierId1-1);
			carrier c2 = carriers.get(carrierId2-1);
			if ((deltaCost1+deltaCost2)<0 && (c1.load+deltaLoad1)>=c1.minWeight && (c2.load+deltaLoad2)>=c2.minWeight){
				swapFlag = true;
			}
			// if true then swap
			if (swapFlag == true){
				// swap carrier
				r1.carrierId = carrierId2;
				r2.carrierId = carrierId1;
				// update carrier loading
				c1.load +=deltaLoad1;
				c2.load +=deltaLoad2;
				// change least carrier option
				if (carrierId2 == r1.costArray.get(0).carrierId){
					r1.leastCost = true;
				} else {
					r1.leastCost = false;
				}				
				if (carrierId1 == r2.costArray.get(0).carrierId){
					r2.leastCost = true;
				} else {
					r2.leastCost = false;
				}
				
			}
			
			count ++;

		}
		
	}

}
