<#-- @ftlvariable name="accounts" type="java.util.List<ru.ulfr.poc.modules.account.model.Account>" -->
<#import "layout.ftl" as layout>
<@layout.myLayout>
<h1>My Accounts</h1>
<table>
    <tr>
        <th>Account ID</th>
        <th>Amount</th>
        <th colspan="3">Operations</th>
    </tr>
    <#list accounts as account>
        <tr>
            <td>${account.id}</td>
            <td>${account.amount} ${account.currency.uiCode}</td>
            <td><a href="${account.id?c}/deposit?id=${account.id?c}">deposit</a></td>
            <td><a href="${account.id?c}/withdraw?id=${account.id?c}">withdraw</a></td>
            <td><a href="${account.id?c}/log">browse details...</a></td>
        </tr>
    </#list>
</table>
</@layout.myLayout>
