package com.ptc.octo.twx.xsltransform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.w3c.dom.Document;

import com.thingworx.entities.utils.ThingUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.resources.Resource;
import com.thingworx.things.repository.FileRepositoryThing;
//import com.thingworx.security.io.SafeFile;
import com.thingworx.types.InfoTable;

public class XSLTrafoResource extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7963159820573691508L;
	/**
	 * 
	 */
	private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(XSLTrafoResource.class);

	public XSLTrafoResource() {	}

	@ThingworxServiceDefinition(name = "XSLTransformXML", 
			description = "Transforms XML with an XSL stylesheet that is either specified as input XML or from a FileRepository path. Read from a file repo xsl:included resopurces can be resolved as well with a path relative to the specified xsl file.", 
			category = "XSLTrafo", isAllowOverride = false, aspects = {"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "output as String - could be XML, HTML or anything else", baseType = "STRING", aspects = {})
	public String XSLTransformXML(
			@ThingworxServiceParameter(name = "XML", description = "xml input object", baseType = "XML", aspects = {
					"isRequired:true" }) Document XML,
			@ThingworxServiceParameter(name = "FileRepo", description = "if you use an XSL stylesheet from a FileRepo, specify the FileRepo name here", baseType = "THINGNAME", aspects = {
			"thingTemplate:FileRepository" }) String FileRepo,
			@ThingworxServiceParameter(name = "XMLPath", description = "if you use an XML file from a FileRepo, specify the path to it here", baseType = "STRING") String XMLPath,
			@ThingworxServiceParameter(name = "XSLPath", description = "if you use an XSL stylesheet from a FileRepo, specify the path to it here", baseType = "STRING") String XSLPath,
			@ThingworxServiceParameter(name = "XSL", description = "if you use plain xsl code, specify it here (if you want to specify it inline or got it from somewhere else before)", baseType = "XML")  Document XSL,
			@ThingworxServiceParameter(name = "ResultPath", description = "if you want to stream the result to a file in the FileRepository, specify it here. For very large xml files this may reduce memory usage and increase performance. The returned String will be the resulting File URL when this parameter specified and not the transformed XML", baseType = "STRING") String ResultPath) 
					throws Exception {
		_logger.trace("Entering Service: XSLTransformXML");
		String result = null;
		OutputStream os = null;
        try {
        	FileRepositoryThing repos = FileRepo!=null ? (FileRepositoryThing)ThingUtilities.findThing(FileRepo) : null;
			if(FileRepo!=null && repos==null) throw new Exception("FileRepo: "+FileRepo+" not found!");

			Source xmlsource = null;
			if(XML!=null) xmlsource = new DOMSource(XML);
			else {
				if(repos==null || XMLPath==null) throw new Exception("Either XML or FileRepo and XMLPath must be specified!");
				File xmlFile = new File(repos.getRootPath(), XMLPath);
				if(!xmlFile.exists())throw new Exception("File: "+XMLPath+" in FileRepo: "+FileRepo+" not found!");
				xmlsource = new StreamSource(xmlFile);
			}
			Source xslsource = null;
			if(XSL!=null) xslsource = new DOMSource(XSL);
			else {
				if(repos==null || XSLPath==null) throw new Exception("Either XSL or FileRepo and XSLPath must be specified!");
				//should use com.thingworx.security.io.SafeFile instead!?!
				File xslFile = new File(repos.getRootPath(), XSLPath);
				if(!xslFile.exists())throw new Exception("File: "+XSLPath+" in FileRepo: "+FileRepo+" not found!");
				xslsource = new StreamSource(xslFile);
				xslsource.setSystemId(xslFile.toURI().toURL().toExternalForm());
			}

			// Use a Transformer for output
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(xslsource);

			if(ResultPath==null) os = new ByteArrayOutputStream();
			else {
				if(repos==null || ResultPath==null) throw new Exception("If ResultPath is specified, FileRepo must be specified as well!");
				File resFile = new File(repos.getRootPath(), ResultPath);
				//if(!resFile.canWrite()) throw new Exception("Resulting File: "+ResultPath+" in FileRepo: "+FileRepo+" can not be written. Issues with Permissions?");
				os = new FileOutputStream(resFile);
			}
			StreamResult res = new StreamResult(os);
			transformer.transform(xmlsource, res);
			if(ResultPath==null) result = ((ByteArrayOutputStream)os).toString();
			else {
				InfoTable resultIT = repos.GetFileListingWithLinks(ResultPath.substring(0, ResultPath.lastIndexOf("/")), ResultPath.substring(ResultPath.lastIndexOf("/")+1));
				result = resultIT.getFirstRow().getStringValue("downloadLink");
			}		
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error(e.getLocalizedMessage()+"\n");
		} finally {
			if(os!=null) {
				os.flush();
				os.close();				
			}
		}
		_logger.trace("Exiting Service: XSLTransformXML");
		return result;
	}

}
