<%-- 
    Document   : wait
    Created on : 26 Feb, 2011, 5:29:27 PM
    Author     : chaitanya
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.ozonetel.occ.model.*"%>
<%
Response resp=new Response();
String agent=request.getParameter("agentId");
resp.addConference("conf_wait");
request.getSession().setAttribute("state","wait");
out.println(resp.getXML());
%>
