<#-- @ftlvariable name="user" type="ru.ulfr.poc.modules.users.model.User" -->
<#-- @ftlvariable name="userAdmin" type="java.lang.Boolean" -->
<#-- @ftlvariable name="userCustomer" type="java.lang.Boolean" -->
<#macro myLayout page_title=""><!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>POC IBS <#if !(page_title == '')> - ${page_title}</#if></title>
    <link rel="stylesheet" href="/static/screen.css" type="text/css">
    <script type="application/javascript" src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
    <script type="application/javascript" src="/static/base.js"></script>
    <#if userAdmin>
        <script type="application/javascript" src="/static/admin.js"></script>
    </#if>
    <#if userCustomer>
        <script type="application/javascript" src="/static/customer.js"></script>
    </#if>
</head>
<body>
<header>
<p>
    <#if user??>
        <a href="/">home</a>
        <form method="post" action="/logout" style="float: right">
            <input type="submit" value="Log out">
        </form>

        Current user: ${user.name}
    <#else>
        Guest visitor - <a href="/login">Log in</a>
    </#if>
    </p>
</header>
<main><#nested></main>
<footer></footer>
</body>
</html>
</#macro>