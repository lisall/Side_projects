package staplesApp;
import java.util.*;

/**
 * @author jieyang
 * to generate a list of all combinations c(n,k) from 1,2,3...to n
 */

public class combination {
	
	int n;
	int k;
	
	public combination(int n, int k){
		
		this.n = n;
		this.k = k;
	}
	
    public List<List<Integer>> combine() {
        List<List<Integer>> res = new ArrayList<>();
        if (n < 0 || k < 0 || n < k) return res;
        getCombinations(res, new ArrayList<Integer>(), n, k, 1);
        return res;
    }
    
    private void getCombinations(List<List<Integer>> res, List<Integer> list, int n, int k, int pos) {
        if (list.size() == k) {
            res.add(new ArrayList<>(list));
            return;
        }
        for (int i = pos; i <= n; i++) {
            list.add(i);
            getCombinations(res, list, n, k, i + 1);
            list.remove(list.size() - 1);
        }
    }

}
