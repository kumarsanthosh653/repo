<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
    <head>
        <title><fmt:message key="login.title"/></title>

        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/${appConfig["csstheme"]}/theme.css'/>" />
        <link rel="stylesheet" type="text/css" media="print" href="<c:url value='/styles/${appConfig["csstheme"]}/print.css'/>" />
        <link rel="stylesheet" type="text/css" href="scripts/boxlib.css"/>
        <script type="text/javascript" src="<c:url value='/scripts/prototype.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/scriptaculous.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/global.js'/>"></script>
        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/${appConfig["csstheme"]}/layout-1col.css'/>" />
    </head>
    <body id="login">
        <div id="page">
            <div id="content" class="clearfix">
                    <div id="login">
                        <form method="post" id="loginForm" action="<c:url value='/j_security_check'/>"
    onsubmit="saveUsername(this);return validateForm(this)">

    <table width="100%">
        <tr><td>&nbsp;</td></tr>
        <tr><td>&nbsp;</td></tr>
        <tr>
            <td align="left">
                <table style="border:1px solid #d35036;" width="100%">
                    <tr><td>&nbsp;</td></tr>
                    <tr>
                        <td align="center">
                            <img src='<c:url value="/images/logo12.png"/>' alt='vconnect' />
                        </td>
                    </tr>
                    <tr><td>&nbsp;</td></tr>
                    <tr>
                        <td align="center">
                            <h3 style="font-weight:bold;color:#d35036;">Cloud Agent Admin Portal</h3>
                        </td>
                    </tr>
                    <tr><td>&nbsp;</td></tr>
                    <tr>
                        <td>
                            <fieldset style="border:1px solid #d35036;background-color:#c0c0c0">
                                <div style="height:2em;color:#fff;background-color:#d35036;font-weight:bold;text-align:left">
                                    <fmt:message key="login.heading"/>
                                </div>

                                <table style="margin-top:10px;margin-left:30px">
                                    <c:if test="${param.error != null}">
                                        <tr>
                                            <td colspan="2" class="error">
                                                <img src='<c:url value="/images/iconWarning.gif"/>' alt='<fmt:message key="icon.warning"/>' class="icon" />
                                                <fmt:message key="errors.password.mismatch"/>
                                                <%--<c:out value="${sessionScope.ACEGI_SECURITY_LAST_EXCEPTION.message}"/>--%>
                                            </td>
                                        </tr>
                                    </c:if>
                                    <tr>
                                        <td>
                                            <label for="j_username" class="desc">
                                                <fmt:message key="label.username"/> <span class="req">*</span>
                                            </label>
                                        </td>
                                        <td>
                                            <input type="text" class="text medium" name="j_username" id="j_username" tabindex="1" />
                                        </td>
                                    </tr>

                                    <tr>
                                        <td>
                                            <label for="j_password" class="desc">
                                                <fmt:message key="label.password"/> <span class="req">*</span>
                                            </label>
                                        </td>
                                        <td>
                                            <input type="password" class="text medium" name="j_password" id="j_password" tabindex="2" />
                                        </td>
                                    </tr>

                                    <c:if test="${appConfig['rememberMeEnabled']}">
                                        <%--<tr>
                                            <td colspan="2"><input type="checkbox" class="checkbox" name="rememberMe" id="rememberMe" tabindex="3"/>
                                            <label for="rememberMe" class="choice"><fmt:message key="login.rememberMe"/></label></td>
                                        </tr>
                                    --%></c:if>
                                    <tr><td></td></tr>
                                    <tr>
                                    <td></td>
                                        <td colspan="5"  align="center">
                                            <input type="submit" class="button" name="login" value="<fmt:message key="button.login"/>" tabindex="3" />
                                               </td>
                                    </tr>
                                    <tr>
                                        <td colspan="6">
                                            <%--<p>
                                                <fmt:message key="login.signup">
                                                    <fmt:param><c:url value="/signup.html"/></fmt:param>
                                                </fmt:message>
                                            </p>
                                        --%></td>


                                    </tr>
                                </table>
                            </fieldset>
                        </td>
                    </tr>
                    <tr><td>&nbsp;</td></tr>
                </table>
            </td>
        </tr>
    </table>
    </form>
                    </div>
            </div>
        </div>


<%--<fieldset style="padding-bottom: 0">
<ul>
<c:if test="${param.error != null}">
    <li class="error">
        <img src="${ctx}/images/iconWarning.gif" alt="<fmt:message key='icon.warning'/>" class="icon"/>
        <fmt:message key="errors.password.mismatch"/>
        ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION_KEY.message}
    </li>
</c:if>
    <li>
       <label for="j_username" class="required desc">
            <fmt:message key="label.username"/> <span class="req">*</span>
        </label>
        <input type="text" class="text medium" name="j_username" id="j_username" tabindex="1" />
    </li>

    <li>
        <label for="j_password" class="required desc">
            <fmt:message key="label.password"/> <span class="req">*</span>
        </label>
        <input type="password" class="text medium" name="j_password" id="j_password" tabindex="2" />
    </li>

<c:if test="${appConfig['rememberMeEnabled']}">
    <li>
        <input type="checkbox" class="checkbox" name="_spring_security_remember_me" id="rememberMe" tabindex="3"/>
        <label for="rememberMe" class="choice"><fmt:message key="login.rememberMe"/></label>
    </li>
</c:if>
    <li>
        <input type="submit" class="button" name="login" value="<fmt:message key='button.login'/>" tabindex="4" />
         <p>
            <fmt:message key="login.signup">
                <fmt:param><c:url value="/signup.html"/></fmt:param>
            </fmt:message>
        </p> 
    </li>
</ul>
</fieldset>--%>

<%@ include file="/scripts/login.js"%>
    </body>
</html>
<%-- 
<p><fmt:message key="login.passwordHint"/></p> --%>
