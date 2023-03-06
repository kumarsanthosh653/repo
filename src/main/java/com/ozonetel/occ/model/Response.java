package com.ozonetel.occ.model;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Response {

	DocumentBuilderFactory dbfac;
    DocumentBuilder docBuilder;
    Document doc;
    Element response;
	public Response() {
		super();
		try{
        dbfac = DocumentBuilderFactory.newInstance();
        docBuilder = dbfac.newDocumentBuilder();
        doc = docBuilder.newDocument();
        //create the root element and add it to the document
        response = doc.createElement("response");
        doc.appendChild(response);

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void setSid(String sid)
	{
		response.setAttribute("sid", sid);
	}
	
	public void addPlayText(String playText){
		Element pt=doc.createElement("playtext");
		pt.setTextContent(playText);
		response.appendChild(pt);
	}

        public void addConference(String conf){
		Element pt=doc.createElement("conference");
		pt.setTextContent(conf);
		response.appendChild(pt);
	}
	
	public void addHangup(){
		Element hu=doc.createElement("hangup");
		response.appendChild(hu);
	}
	
	
	public void addContext(String context){
		Element ctx=doc.createElement("context");
		ctx.setTextContent(context);
		response.appendChild(ctx);
	}
	
	public void addPlayAudio(String playAudio){
		Element pt=doc.createElement("playaudio");
		pt.setTextContent(playAudio);
		response.appendChild(pt);
	}
	
	public void addCollectDtmf(CollectDtmf cd){
		Node c = doc.importNode(cd.getRoot(),true); //true if you want a deep copy
		response.appendChild(c);
	}
	public String getXML(){
		//set up a transformer
		String xmlString="";
        try{
		TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);
        xmlString = sw.toString();
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        return xmlString;
	}
	
	public Element getRoot(){
		return response;
	}
}
