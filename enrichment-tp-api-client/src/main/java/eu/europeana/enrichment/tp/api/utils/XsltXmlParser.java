package eu.europeana.enrichment.tp.api.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;

public class XsltXmlParser {

	public static String transfromXmlToString(String xsltFilePath, String xml) throws TransformerException, FileNotFoundException {
		if(StringUtils.isBlank(xsltFilePath) || StringUtils.isBlank(xml)) {
			return null;
		}

		File xslFile = new File(xsltFilePath);
	    InputStream xslIS = new FileInputStream(xslFile);
		InputStream xslBIS = new BufferedInputStream(xslIS);
		StreamSource xslSource = new StreamSource(xslBIS);

		InputStream inputStreamBytes = new ByteArrayInputStream(xml.getBytes());
		StreamSource inputStream = new StreamSource(inputStreamBytes);

		StringWriter outputStream = new StringWriter();
		StreamResult resultStream = new StreamResult(outputStream);
		
		TransformerFactory transFact = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
        Transformer trans = transFact.newTransformer(xslSource);
		trans.transform(inputStream, resultStream);

		return outputStream.getBuffer().toString(); 
	}
}
