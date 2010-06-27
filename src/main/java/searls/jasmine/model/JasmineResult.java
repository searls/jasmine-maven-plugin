package searls.jasmine.model;

import java.util.ArrayList;
import java.util.List;

public class JasmineResult implements ResultItemParent {
	private String description;
	private List<TestResultItem> resultItems = new ArrayList<TestResultItem>();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean didPass() {
		if(description == null) {
			throw new IllegalStateException("Can only determine success after description is set.");
		}
		return description.contains("0 failures");
	}
	
	public void addChild(TestResultItem item) {
		resultItems.add(item);
	}
	
	public List<TestResultItem> getChildren() {
		return resultItems;
	}
	
	
}
