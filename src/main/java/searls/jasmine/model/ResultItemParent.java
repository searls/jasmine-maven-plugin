package searls.jasmine.model;

import java.util.List;

public interface ResultItemParent {

	public void addChild(TestResultItem item);
	
	public List<TestResultItem> getChildren();
}
