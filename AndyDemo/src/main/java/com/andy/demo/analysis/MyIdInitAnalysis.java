package com.andy.demo.analysis;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.andy.demo.analysis.bean.MyIdInitResult;

/**
 * 放在DownloadService中测试
 * @author guoch
 *
 */
public class MyIdInitAnalysis extends ErrorAnalysis{
	public MyIdInitResult myIdInitResult = new MyIdInitResult();
	
	
	/**
	 * 也可在这里新建参数对象
	 */
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}
	
	/**
	 * 先执行startElement，再执行parseElement
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		super.startElement(namespaceURI, localName, qName, atts);
	}
	
	@Override
	public void parseElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		super.parseElement(namespaceURI, localName, qName);
		if (localName.equalsIgnoreCase("appId")) {
			this.myIdInitResult.appId = buf.toString().trim();
		}else if (localName.equalsIgnoreCase("seqId")) {
			this.myIdInitResult.seqId = buf.toString().trim();
		}else if (localName.equalsIgnoreCase("random")) {
			this.myIdInitResult.random = buf.toString().trim();
		} else if (localName.equalsIgnoreCase("qrCodeUrl")) {
			this.myIdInitResult.qrCodeUrl = buf.toString().trim();
		}
	}
}
