<#-- @ftlvariable name="accounts" type="java.util.List<ru.ulfr.poc.modules.account.model.Account>" -->
<#-- @ftlvariable name="customer" type="ru.ulfr.poc.modules.users.model.User" -->
<#import "layout.ftl" as layout>
<@layout.myLayout>
<h1>Customer Information</h1>
<p>Name: ${customer.name}</p>
<p>E-mail: ${customer.email}</p>
<h2>Accounts</h2>
<table>
    <tr>
        <th>Account</th>
        <th>Amount</th>
        <th>Actions</th>
    </tr>
    <#list accounts as account>
        <tr>
            <td>${account.id}</td>
            <td>${account.amount} ${account.currency.uiCode}</td>
            <td><a href="/admin/transactions/?id=${account.id?c}">browse transactions...</a></td>
        </tr>
    </#list>
</table>
</@layout.myLayout>
