package com.d2g.mead.weather;

import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class Utils {
	public static Logger logger = Logger.getLogger(Utils.class);

	public static Document downloadXml(String address) throws Exception {
		System.out.println("downloading from: " + address);
		URLConnection conn = null;
		int retry = 4;
		while ( retry>0 ) {
			retry--;
			try {
	            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            factory.setExpandEntityReferences(true);
	            factory.setValidating(false);
	            DocumentBuilder builder = factory.newDocumentBuilder();
				URL url = new URL(address);
				conn = url.openConnection();
	            Document downloadDocument = builder.parse( new InputSource(conn.getInputStream()));
				return downloadDocument;
			} catch (SAXParseException e ) {
				logger.error("SAX Parse Exception occurred getting XML document from " + address + ". Retry:" + retry, e);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {}
				
			}
		}
		logger.error("Repeated SAX Parse Exception occurred getting XML document " + address);
		throw new Exception("Repeated SAX Parse Exception occurred getting XML document " + address);
	}


}
