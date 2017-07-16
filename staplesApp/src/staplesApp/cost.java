package staplesApp;

/**
 * 
 * @author jieyang
 * this class acts like c_ij
 */

public class cost implements Comparable<cost>{
	
	int requestId;
	int carrierId;
	double price;
	
	public cost(int r, int c, double p){
		
		this.requestId = r;
		this.carrierId = c;
		this.price = p;
	}
	
	public int compareTo (cost compareItem){
		
		double compare_price=((cost) compareItem).price;
		
		if (this.price > compare_price){
			return 1;
		}			
		else if (this.price == compare_price) {
			return 0;
		}
		else  {
			return -1;
		}
			
    }

}
