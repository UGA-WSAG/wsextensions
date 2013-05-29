package edu.uga.RESTClient;

import java.io.InputStream;

public class RestInvokeResult {
	private Integer httpCode = null;
	private String result = null;
	private String xmlResult = null;
	//add inputstream..here
	private InputStream inputStream = null;
	
	public Integer getHttpCode() {
		return httpCode;
	}
	public void setHttpCode(Integer httpCode) {
		this.httpCode = httpCode;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getXmlResult() {
		return xmlResult;
	}
	public void setXmlResult(String xmlResult) {
		this.xmlResult = xmlResult;
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
}	
