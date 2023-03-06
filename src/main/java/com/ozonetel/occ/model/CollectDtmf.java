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

public class CollectDtmf {
	DocumentBuilderFactory dbfac;
    DocumentBuilder docBuilder;
    Document doc;
    Element collectdtmf;
	public CollectDtmf() {
		super();
		  //We need a Document
		try{
        dbfac = DocumentBuilderFactory.newInstance();
        docBuilder = dbfac.newDocumentBuilder();
        doc = docBuilder.newDocument();
        //create the root element and add it to the document
        collectdtmf = doc.createElement("collectdtmf");
        doc.appendChild(collectdtmf);

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public CollectDtmf(int maxDigits,String termChar,int timeOut,String playText,String playType) {
		  //We need a Document
		try{
        dbfac = DocumentBuilderFactory.newInstance();
        docBuilder = dbfac.newDocumentBuilder();
        doc = docBuilder.newDocument();
        //create the root element and add it to the document
        collectdtmf = doc.createElement("collectdtmf");
        collectdtmf.setAttribute("l", ""+maxDigits);
        collectdtmf.setAttribute("t", termChar);
        collectdtmf.setAttribute("o", ""+timeOut);
        Element pt=null;
        if(playType.equals("text"))
        	pt=doc.createElement("playtext");
        else if(playType.equals("audio"))
        	pt=doc.createElement("playaudio");
        
		pt.setTextContent(playText);
		collectdtmf.appendChild(pt);
        doc.appendChild(collectdtmf);

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public CollectDtmf(int maxDigits,String termChar,int timeOut) {
		try{
        dbfac = DocumentBuilderFactory.newInstance();
        docBuilder = dbfac.newDocumentBuilder();
        doc = docBuilder.newDocument();
        //create the root element and add it to the document
        collectdtmf = doc.createElement("collectdtmf");
        collectdtmf.setAttribute("l", ""+maxDigits);
        collectdtmf.setAttribute("t", termChar);
        collectdtmf.setAttribute("o", ""+timeOut);
        doc.appendChild(collectdtmf);

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void setMaxDigits(int maxDigits)
	{
		collectdtmf.setAttribute("l", ""+maxDigits);
	}
	
	public void setTermChar(String termChar){
		collectdtmf.setAttribute("t", termChar);
		
	}
	public void setTimeOut(int timeOut){
		collectdtmf.setAttribute("o", ""+timeOut);
	}
	
	public void addPlayText(String playText){
		Element pt=doc.createElement("playtext");
		pt.setTextContent(playText);
		collectdtmf.appendChild(pt);
	}
	
	public void addPlayAudio(String playAudio){
		Element pt=doc.createElement("playaudio");
		pt.setTextContent(playAudio);
		collectdtmf.appendChild(pt);
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
		return collectdtmf;
	}
	
}
