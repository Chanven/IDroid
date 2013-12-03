package com.andy.demo.analysis;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.andy.demo.analysis.bean.ErrorMessage;

/**
 * 错误信息解析
 * @author ivankuo
 *
 */
public class ErrorAnalysis extends DefaultHandler {

	protected StringBuffer buf = new StringBuffer();
	public ErrorMessage _error = new ErrorMessage();
	
	public final boolean succeeded() {
		return (_error._code == null || _error._code.equals(""));
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		parseElement(namespaceURI, localName, qName);
		buf.setLength(0);
	}

	@Override
	public void characters(char ch[], int start, int length) {
		buf.append(ch, start, length);
	}
	
	public void parseElement(String namespaceURI, String localName, String qName)
	throws SAXException {
		if (localName.equalsIgnoreCase("Code")) {
			this._error._code= buf.toString().trim();
		} else if (localName.equalsIgnoreCase("Message")) {
			this._error._message = buf.toString().trim() ;
		}
	}
}
