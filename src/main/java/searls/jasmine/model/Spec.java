package searls.jasmine.model;

import java.util.ArrayList;
import java.util.List;

public class Spec extends TestResultItem {

	private List<String> messages = new ArrayList<String>();
	
	public void addMessage(String message) {
		messages.add(message);
	}

	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
}
