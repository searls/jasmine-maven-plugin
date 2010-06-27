package searls.jasmine.model;

import java.util.ArrayList;
import java.util.List;


public class Suite extends TestResultItem implements ResultItemParent {

	private List<TestResultItem> children = new ArrayList<TestResultItem>();
	
	public void addChild(TestResultItem item) {
		children.add(item);
	}
	
	public List<TestResultItem> getChildren() {
		return children;
	}

}
