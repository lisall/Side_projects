package staplesApp;

import java.io.IOException;
import java.util.*;

/**
 * 
 * @author jieyang
 * Main function to optimize order assignment
 *
 */

public class assign {

	public static void main(String[] args) throws IOException{
		
		// read inputs
		readRes res = readFile.readAll();
		ArrayList<request> requests = res.requests;
		Hashtable <Integer,Hashtable<Integer,double[]>> costTable = res.costTable;
		ArrayList<carrier> carriers = res.carriers;
		params paramList = res.paramList;

		// assign cost array to each request
		// based on each request's region #
		costFinder findCost = new costFinder(requests, costTable);
		findCost.find();
		
		// sort requests by costAvg, high -> low
		Collections.sort(requests);
		
		// generate initial solution
		initialSolModelA initialSol = new initialSolModelA(requests,carriers);
		initialSol.assign();
		
		// compute total cost
		costAgg totalCost = new costAgg(requests);
		double initialCost = totalCost.compute();
		double lowerBound = totalCost.computeLB();
		
		// variable neighborhood search
		Random random_num = new  Random (10);
		
		double costPrev = totalCost.compute();
		double costNow = 999999999;
		int count = 0;
		int maxIter = 2000;
		while ((costNow-costPrev)/costPrev > 0.01 || count < maxIter){

			costPrev = costNow;
			nbrOne nbone = new nbrOne(requests,carriers);
			nbone.search(paramList.neighborhood1);
			nbrTwo nbTwo = new nbrTwo(requests,carriers);
			nbTwo.search(paramList.neighborhood2);
			nbrThree nbThree = new nbrThree(requests,carriers);
			nbThree.search(paramList.neighborhood3);
			costAgg newCost = new costAgg(requests);
			costNow = newCost.compute();
			count ++;
			
		}
		
		// output results to a file called solution.txt
		fileOutput output = new fileOutput(requests,carriers,lowerBound,initialCost,costNow);
		output.write();
		

	}

}
