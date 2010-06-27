package searls.jasmine.model;

public abstract class TestResultItem {
	
	protected boolean passed;
	protected String description;
	
	public boolean didPass() {
		return passed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

}
