package staplesApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class nbrThree {
	
	ArrayList<request> requests;
	ArrayList<carrier> carriers;
	
	public nbrThree(ArrayList<request> r, ArrayList<carrier> c){
		this.requests = r;
		this.carriers = c;
	}
	
	public void search(double p){

		// generate a list of all triple combinations
		combination comb = new combination(requests.size(),3);
		List<List<Integer>> combList = comb.combine();
		
		// shuffle this list
		Collections.shuffle(combList);
		
		// since it's pair, we allow n*n as maximum (ideally it is C(n,2))
		int maxIter = (int) p*combList.size();
		int count = 0;
		
		while (count<maxIter){
			// select a triple
			int pos1 = combList.get(count).get(0)-1;
			int pos2 = combList.get(count).get(1)-1;
			int pos3 = combList.get(count).get(2)-1;
			request r1 = requests.get(pos1);
			request r2 = requests.get(pos2);
			request r3 = requests.get(pos3);
			
			// assignment 1: 3, 1, 2
			double deltaCost1 = costCheck(r1,r2,r3,3,1,2);
			Boolean flag1 = loadCheck(r1,r2,r3,3,1,2);
			// assignment 2: 2, 3, 1
			double deltaCost2 = costCheck(r1,r2,r3,2,3,1);
			Boolean flag2 = loadCheck(r1,r2,r3,2,3,1);
			
			int method = 0;
			if (flag1==true && deltaCost1<0){
				if (flag2==true && deltaCost2<0){
					if (deltaCost1<deltaCost2){
						method = 1;
					} else {
						method = 2;
					}
				} else {
					method = 1;
				}
			} else {
				if (flag2==true && deltaCost2<0){
					method = 2;
				} 
			}
			
			if (method==1){
				change(r1,r2,r3,3,1,2);
			} else if (method==2){
				change(r1,r2,r3,2,3,1);
			}
			
			count ++;
		}
		
	}
	
	/**
	 * 
	 * @param r1
	 * @param r2
	 * @param r3
	 * @param i: new carrier assignment i.e. carrierId for r1
	 * @param j: same for r2
	 * @param k: same for r3
	 * @return cost change: negative means good
	 */
	
	private double costCheck(request r1, request r2, request r3, int i, int j, int k){
		
		// get current carrierId
		int carrierId1 = r1.carrierId;
		int carrierId2 = r2.carrierId;
		int carrierId3 = r3.carrierId;
		
		ArrayList<Integer> carriersTriple = new ArrayList<Integer>();
		carriersTriple.add(r1.carrierId);
		carriersTriple.add(r2.carrierId);
		carriersTriple.add(r3.carrierId);

		// compute delta cost for this swap
		double deltaCost1 = - r1.costTable.get(carrierId1) + r1.costTable.get(carriersTriple.get(i-1));
		double deltaCost2 = - r2.costTable.get(carrierId2) + r2.costTable.get(carriersTriple.get(j-1));
		double deltaCost3 = - r3.costTable.get(carrierId3) + r3.costTable.get(carriersTriple.get(k-1));
		
		return deltaCost1+deltaCost2+deltaCost3;
		
	}
	
	/**
	 * Similar to costCheck(), this function ensures loading constraints met
	 * @param r1
	 * @param r2
	 * @param r3
	 * @param i
	 * @param j
	 * @param k
	 * @return
	 */
	
	private Boolean loadCheck(request r1, request r2, request r3, int i, int j, int k){
		
		ArrayList<request> requestsTriple = new ArrayList<request>();
		requestsTriple.add(r1);
		requestsTriple.add(r2);
		requestsTriple.add(r3);
		
		// get current carrierId
		int carrierId1 = r1.carrierId;
		int carrierId2 = r2.carrierId;
		int carrierId3 = r3.carrierId;
		
		double deltaLoad1 = -r1.weight + requestsTriple.get(i-1).weight;
		double deltaLoad2 = -r2.weight + requestsTriple.get(j-1).weight;
		double deltaLoad3 = -r3.weight + requestsTriple.get(k-1).weight;
		
		carrier c1 = carriers.get(carrierId1-1);
		carrier c2 = carriers.get(carrierId2-1);
		carrier c3 = carriers.get(carrierId3-1);
		
		if ((c1.load+deltaLoad1)>=c1.minWeight && (c2.load+deltaLoad2)>=c2.minWeight && 
				(c3.load+deltaLoad2)>=c3.minWeight){
			return true;
		}
		
		return false;
		
	}
	
	private void loadChange(request r1, request r2, request r3, int i, int j, int k){
		
		ArrayList<request> requestsTriple = new ArrayList<request>();
		requestsTriple.add(r1);
		requestsTriple.add(r2);
		requestsTriple.add(r3);
		
		// get current carrierId
		int carrierId1 = r1.carrierId;
		int carrierId2 = r2.carrierId;
		int carrierId3 = r3.carrierId;
		
		double deltaLoad1 = -r1.weight + requestsTriple.get(i-1).weight;
		double deltaLoad2 = -r2.weight + requestsTriple.get(j-1).weight;
		double deltaLoad3 = -r3.weight + requestsTriple.get(k-1).weight;
		
		carrier c1 = carriers.get(carrierId1-1);
		carrier c2 = carriers.get(carrierId2-1);
		carrier c3 = carriers.get(carrierId3-1);
		
		// update carrier loading
		c1.load +=deltaLoad1;
		c2.load +=deltaLoad2;
		c3.load +=deltaLoad3;
		
	}
	
	private void change(request r1, request r2, request r3, int i, int j, int k){		
		ArrayList<request> requestsTriple = new ArrayList<request>();
		requestsTriple.add(r1);
		requestsTriple.add(r2);
		requestsTriple.add(r3);
		
		ArrayList<Integer> carriersTriple = new ArrayList<Integer>();
		carriersTriple.add(r1.carrierId);
		carriersTriple.add(r2.carrierId);
		carriersTriple.add(r3.carrierId);

		// swap carrier
		r1.carrierId = carriersTriple.get(i-1);
		r2.carrierId = carriersTriple.get(j-1);
		r3.carrierId = carriersTriple.get(k-1);
		
		// update loading
		loadChange(r1,r2,r3,i,j,k);
		
		// change least cost option
		if (r1.carrierId == r1.costArray.get(0).carrierId){
			r1.leastCost = true;
		} else {
			r1.leastCost = false;
		}				
		if (r2.carrierId == r2.costArray.get(0).carrierId){
			r2.leastCost = true;
		} else {
			r2.leastCost = false;
		}
		if (r3.carrierId == r3.costArray.get(0).carrierId){
			r3.leastCost = true;
		} else {
			r3.leastCost = false;
		}
		
	}

}
