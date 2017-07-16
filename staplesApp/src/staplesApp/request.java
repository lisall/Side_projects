package staplesApp;
import java.util.Hashtable;
import java.util.ArrayList;

/**
 * 
 * @author jieyang
 * store all information about a request
 *
 */

public class request implements Comparable<request>{
	
	int requestId;
	String orderId;
	String shipNum;
	String address;
	int zone;
	double weight;
	
	/**
	 * variable: average cost
	 * costArray : it is easier to sort
	 * costTable : a hash table of cost, key is carrier # and value is cost
	 * carrier: assigned carrier id, starting from 1
	 * leastCost: true already least cost
	 */
	
	double costAvg;
	ArrayList<cost> costArray = new ArrayList<cost>();
	Hashtable<Integer,Double> costTable = new Hashtable<Integer,Double>();
	int carrierId;
	Boolean leastCost=false;
	
	public request(int r,String o, String s, String a, int z, double w){
		
		this.requestId=r;
		this.orderId=o;
		this.shipNum=s;
		this.address=a;
		this.zone=z;
		this.weight=w;
	}
	
	public request(){
		
	}
	
	public int compareTo (request compareItem){
		
		double compare_cost=((request) compareItem).costAvg;
		
		if (this.costAvg < compare_cost){
			return 1;
		}			
		else if (this.costAvg == compare_cost) {
			return 0;
		}
		else  {
			return -1;
		}
			
    }
	

}
