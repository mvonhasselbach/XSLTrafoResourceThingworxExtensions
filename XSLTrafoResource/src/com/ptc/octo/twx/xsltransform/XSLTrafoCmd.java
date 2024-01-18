package com.ptc.octo.twx.xsltransform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XSLTrafoCmd {

	public XSLTrafoCmd() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("usage: java com.ptc.octo.twx.xsltransform.XSLTrafoCmd <XMLFilePath> <XSLFilePath> <ResultFilePath>");
			System.out.println("	   or");
			System.out.println("	   java -jar XSLTrafoCmd.jar <XMLFilePath> <XSLFilePath> <ResultFilePath>");
			System.out.println("");
			System.out.println("<ResultFilePath> is optional. If ommited the result is returned on the command line");
			return;
		}
		try {
			XSLTransformXML(args[0], args[1], args.length==2?null:args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void XSLTransformXML(String XMLPath, String XSLPath, String ResultPath) throws Exception {
		//System.out.println("Entering Service: XSLTransformCmd");
		String result = null;
		OutputStream os = null;
        try {
			Source xmlsource = null;
			if(XMLPath==null) throw new Exception("XMLPath must be specified!");
			File xmlFile = new File(XMLPath);
			if(!xmlFile.exists())throw new Exception("File: "+XMLPath+" not found!");
			xmlsource = new StreamSource(xmlFile);
			
			Source xslsource = null;
			if(XSLPath==null) throw new Exception("XSLPath must be specified!");
			//should use com.thingworx.security.io.SafeFile instead!?!
			File xslFile = new File(XSLPath);
			if(!xslFile.exists())throw new Exception("File: "+XSLPath+" not found!");
			xslsource = new StreamSource(xslFile);
			xslsource.setSystemId(xslFile.toURI().toURL().toExternalForm());

			// Use a Transformer for output
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(xslsource);

			if(ResultPath==null) os = new ByteArrayOutputStream();
			else {
				File resFile = new File(ResultPath);
				//if(!resFile.canWrite()) throw new Exception("Resulting File: "+ResultPath+" in FileRepo: "+FileRepo+" can not be written. Issues with Permissions?");
				os = new FileOutputStream(resFile);
			}
			StreamResult res = new StreamResult(os);
			transformer.transform(xmlsource, res);
			if(ResultPath==null) result = ((ByteArrayOutputStream)os).toString();
			else {
				result = ResultPath;
			}		
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage()+"\n");
			e.printStackTrace();
		} finally {
			if(os!=null) {
				os.flush();
				os.close();				
			}
		}
		//System.out.println("Exiting Service: XSLTransformXML");
		//System.out.println("Result: ");
		System.out.println(result);
	}
}
