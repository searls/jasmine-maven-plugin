package searls.jasmine.runner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import searls.jasmine.model.JasmineResult;
import searls.jasmine.model.ResultItemParent;
import searls.jasmine.model.Spec;
import searls.jasmine.model.Suite;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class SpecRunnerExecutor {

	private static final String SHOW_PASSED_CHECKBOX = "__jasmine_TrivialReporter_showPassed__";
	
	public JasmineResult execute(String runnerFile) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3);
	    HtmlPage initialPage = webClient.getPage("file://"+runnerFile);

	    HtmlCheckBoxInput checkbox = (HtmlCheckBoxInput) initialPage.getElementById(SHOW_PASSED_CHECKBOX);
	    HtmlPage page = checkbox.click();
	    
	    JasmineResult result = new JasmineResult();
	    result.setDescription(((DomText) page.getFirstByXPath("//a[@class='description']/text()")).asText());
	    
	    HtmlDivision rootDiv = page.getFirstByXPath("html/body/div");
	    populateChildren(result, rootDiv);
	    
	    return result;
	}

	@SuppressWarnings("unchecked")
	private void populateChildren(ResultItemParent parent, HtmlDivision resultItem) {
		List<HtmlDivision> childResultDivs = (List<HtmlDivision>) resultItem.getByXPath("div[contains(@class,'suite') or contains(@class,'spec')]");
	    for (HtmlDivision div : childResultDivs) {			
	    	String[] classes = div.getAttribute("class").split(" ");
	    	String type = classes[0];
	    	String success = classes[1];
	    	boolean passed = "passed".equals(success);
	    	if("spec".equals(type)) {
	    		Spec spec = new Spec();
	    		spec.setDescription(((HtmlAnchor)div.getFirstByXPath("a[@class='description']")).getAttribute("title"));
	    		spec.setPassed(passed);
	    		List<DomText> messageTexts = (List<DomText>) div.getByXPath("div[@class='messages']/div/text()");
	    		for (DomText text : messageTexts) {
					spec.addMessage(text.getTextContent());
				}
	    		parent.addChild(spec);
	    	} else {
	    		Suite suite = new Suite();
	    		suite.setDescription(((HtmlAnchor)div.getFirstByXPath("a[@class='description']")).getTextContent());
	    		suite.setPassed(passed);
	    		parent.addChild(suite);
	    		populateChildren(suite,div);
	    	}	    	
		}
	}


}
