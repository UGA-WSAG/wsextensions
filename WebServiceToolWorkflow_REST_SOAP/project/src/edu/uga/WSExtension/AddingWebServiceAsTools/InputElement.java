package edu.uga.WSExtension.AddingWebServiceAsTools;

import com.predic8.schema.Element;

public class InputElement {
	String xpath;
	String name;
	Element element;
	
	public InputElement() {
		// TODO Auto-generated constructor stub
	}
	
	public InputElement(String xpath, String name, Element element){
		this.xpath = xpath;
		this.name = name;
		this.element = element;
	}
	
	public InputElement(String xpath, String name){
		this.xpath = xpath;
		this.name = name;
	}
	
	public String getXpath() {
		return xpath;
	}
	
	public String getName() {
		return name;
	}
	
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}
}
