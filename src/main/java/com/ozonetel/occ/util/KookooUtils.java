package com.ozonetel.occ.util;

import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.impl.Status;
import java.io.IOException;
import java.io.StringReader;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author pavanj
 */
public class KookooUtils {
    
    private static final Logger logger = Logger.getLogger(KookooUtils.class);
    
    public static StatusMessage parseKookooResponse(String response) {
        try {
            SAXBuilder sb = new SAXBuilder();
            StringReader sr = new StringReader(response);
            Element root = sb.build(sr).getRootElement();
            return new StatusMessage(Status.valueOf(root.getChild("status").getText().toUpperCase()), root.getChild("message").getText());
        } catch (IOException | JDOMException ex) {
            logger.error("Error parsing kookoo response '" + response + "' :", ex);
            return new StatusMessage(Status.ERROR, "Unknown response");
        }
    }
}
