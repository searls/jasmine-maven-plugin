package searls.jasmine.format;

import org.apache.maven.plugin.logging.Log;

import searls.jasmine.model.JasmineResult;
import searls.jasmine.model.ResultItemParent;
import searls.jasmine.model.Spec;
import searls.jasmine.model.TestResultItem;

public class JasmineResultLogger {

	public static final String HEADER="\n"+
		"-------------------------------------------------------\n"+
		" J A S M I N E   T E S T S\n"+
		"-------------------------------------------------------";
	public static final String FAIL_APPENDAGE = " <<< FAILURE!";
	public static final String INDENT = "  ";
	
	private Log log;

	public void setLog(Log log) {
		this.log = log;
	}

	public void log(JasmineResult result) {
		log.info(HEADER);
		
		logChildren(result,0);			
				
		log.info("\nResults:\n\n"+result.getDescription()+"\n");		
	}
	
	private void logChildren(ResultItemParent parent, int indentationLevel) {
		for(TestResultItem item : parent.getChildren()) {
			StringBuilder sb = new StringBuilder();
			appendIndent(sb, indentationLevel);
			
			sb.append(item instanceof Spec ? "it" : "describe")
			.append(' ')
			.append(item.getDescription());
			if(!item.didPass()) {
				sb.append(FAIL_APPENDAGE);
			}
			log.info(sb.toString());
			if(!item.didPass() && item instanceof Spec) {
				logMessages((Spec)item,indentationLevel+1);
			}
			if(item instanceof ResultItemParent) {
				logChildren((ResultItemParent)item,indentationLevel+1);
			}
		}
	}

	private void logMessages(Spec spec, int indentationLevel) {
		for (String message : spec.getMessages()) {
			StringBuilder sb = new StringBuilder();
			appendIndent(sb, indentationLevel);
			sb.append("* ").append(message);
			log.info(sb.toString());
		}
	}

	private void appendIndent(StringBuilder sb, int indentationLevel) {
		for (int i = 0; i < indentationLevel; i++) {
			sb.append(INDENT);
		}
	}

}
