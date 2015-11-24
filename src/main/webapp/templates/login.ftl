<#-- @ftlvariable name="accounts" type="java.util.List<ru.ulfr.poc.modules.account.model.Account>" -->
<#import "layout.ftl" as layout>
<@layout.myLayout>
<form method="post" action="/login" class="x-form">
    <p><label for="i-username">Login</label><input id="i-username" name="username" type="text"></p>

    <p><label for="i-password">Password</label><input id="i-password" name="password" type="password"></p>

    <p><input type="submit" value="Login"></p>
</form>
</@layout.myLayout>