package com.andy.demo.analysis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.res.Resources.NotFoundException;

/**
 * 解析xml文件采用parser方法
 * 
 * @author ivankuo
 * 
 */
public class Analysis {

	/**
	 * 公开调用，传入DefaultHandler的实例及字符串
	 * 
	 * @param defaulthandler
	 * @param xml
	 * @throws Exception
	 * @throws ParserConfigurationException
	 */
	public static void parser(DefaultHandler defaulthandler, String xml)
			throws ParserConfigurationException, Exception {
		StringReader reader = new StringReader(xml);
		InputSource source = new InputSource(reader);

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		XMLReader xr = sp.getXMLReader();
		xr.setContentHandler(defaulthandler);
		xr.parse(source);
	}

	/**
	 * 公开调用，传入DefaultHandler的实例及字节数组流
	 * 
	 * @param defaulthandler
	 * @param is
	 */
	public static void parser(DefaultHandler defaulthandler, byte[] bytearray) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(defaulthandler);
			ByteArrayInputStream in = new ByteArrayInputStream(bytearray);
			xr.parse(new InputSource(in));
			in.close();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 公开调用，传入DefaultHandler的实例及字节数组流
	 * 
	 * @param defaulthandler
	 * @param is
	 */
	public static void parser(DefaultHandler defaulthandler, InputStream in) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(defaulthandler);
			xr.parse(new InputSource(in));
			in.close();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
