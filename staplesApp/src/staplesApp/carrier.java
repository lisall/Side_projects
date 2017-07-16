package staplesApp;
import java.util.*;

public class carrier {
	
	int id;
	double minWeight;
	
	/**
	 * @param load: loading situation
	 */
	double load;
	
	public carrier(int i, double w){
		this.id = i;
		this.minWeight = w;
	}
	
	public carrier(){
		
	}
	
	public Boolean selfCheck(){
		
		if (load>minWeight){
			
			return true;
		}
		
		return false;
	}

}
