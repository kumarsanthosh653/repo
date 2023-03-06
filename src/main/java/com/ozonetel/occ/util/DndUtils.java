package com.ozonetel.occ.util;

import com.ozonetel.occ.model.HttpResponseDetails;
import com.ozonetel.occ.model.StatusMessage;
import com.ozonetel.occ.service.impl.Status;
import java.io.IOException;
import java.net.URLEncoder;
import org.apache.log4j.Logger;

/**
 *
 * @author pavanj
 */
public class DndUtils {
    
    public StatusMessage checkDnd(String custNumber) {
//------------------------------------        
        StatusMessage statusMessage = new StatusMessage(Status.ERROR, "DND check failed");
        try {
            HttpResponseDetails httpResponseDetails = HttpUtils.doGet(dndCheckUrl + URLEncoder.encode(custNumber,"UTF-8"));
            statusMessage = KookooUtils.parseKookooResponse(httpResponseDetails.getResponseBody());
            if (statusMessage.getStatus() == Status.ERROR) { //If any error  re try once more.
                statusMessage = KookooUtils.parseKookooResponse(HttpUtils.doGet(dndCheckUrl + custNumber).getResponseBody());
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        
        return statusMessage;
    }
    
    public void setDndCheckUrl(String dndCheckUrl) {
        this.dndCheckUrl = dndCheckUrl;
    }
    
    private String dndCheckUrl;
    private static Logger logger = Logger.getLogger(DndUtils.class);
}
