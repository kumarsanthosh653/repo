package com.ozonetel.occ.model;

/**
 *This encapsulates 3 details of a http request.
 * <ul>
 * <li>Status Code </li>
 * <li>Reason Phrase/Short textual description of the Status-Code </li>
 * <li>Response Body</li>
 * </ul>
 * @author pavanj
 */
public class HttpResponseDetails {

    private int statusCode;
    private String reasonPhrase;
    private String responseBody;

    public HttpResponseDetails() {
    }

    public HttpResponseDetails(int code, String responseMessage, String response) {
        this.statusCode = code;
        this.reasonPhrase = responseMessage;
        this.responseBody = response;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return "HttpResponse{" + "code=" + statusCode + ", responseMessage=" + reasonPhrase + '}';
    }

    public String toLongString() {
        return "HttpResponseDetails{" + "SatusCode=" + statusCode + ", ReasonPhrase=" + reasonPhrase + ", ResponseBody=" + responseBody + '}';
    }

}
