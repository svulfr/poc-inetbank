<#-- @ftlvariable name="account" type="ru.ulfr.poc.modules.account.model.Account" -->
<#-- @ftlvariable name="transactions" type="java.util.List<ru.ulfr.poc.modules.processor.model.Transaction>" -->
<#-- @ftlvariable name="currencies" type="java.util.Map<java.lang.Integer,ru.ulfr.poc.modules.bank.model.Currency>" -->
<#import "layout.ftl" as layout>
<@layout.myLayout>
<h1>Account</h1>

<p>Account nr: ${account.id} / ${account.currency.uiCode} / ${account.amount}</p>

<h2>Transactions</h2>
<table>
    <tr>
        <th>Date</th>
        <th>Type</th>
        <th>Amount</th>
        <th>Status</th>
        <th>Revenue</th>
        <th>Expense</th>
        <th>Statement</th>
    </tr>
    <#list transactions as transaction>
    <#--hide failed income transactions (useless for customer)-->
        <#if !(transaction.state > 0 && transaction.recipientId == account.id)>
            <tr>
                <td>${transaction.createdOn?string.medium}</td>
                <td>
                    <#switch transaction.txType>
                        <#case 0>
                            <#if transaction.originId == account.id>payment<#else>income</#if>
                            <#break>
                        <#case 1>
                            withdrawal
                            <#break >
                        <#case 2>
                            deposit
                            <#break >
                    </#switch>
                </td>
                <td>${transaction.txAmount?string["0.00"]} ${currencies[transaction.txCurrency?string].uiCode}</td>
                <td><#if transaction.state == 0>success<#else>fail</#if></td>
                <td>
                    <#if transaction.recipientId??  && transaction.recipientId == account.id && transaction.state == 0>
                ${transaction.recipientAmount?string["0.00"]} ${currencies[transaction.recipientCurrency?string].uiCode}
                </#if>
                </td>
                <td>
                    <#if transaction.originId?? && transaction.originId == account.id && transaction.state == 0>
                ${transaction.originAmount?string["0.00"]} ${currencies[transaction.originCurrency?string].uiCode}
                </#if>
                </td>
                <td>
                    <#if transaction.state == 0>
                        <a href="/customer/statement?account=${account.id?c}&tx=${transaction.id?c}" target="_blank">View...</a>
                        <a href="/customer/statement-pdf?account=${account.id?c}&tx=${transaction.id?c}"
                           target="_blank">Download...</a>
                    </#if>
                </td>
            </tr>
        </#if>
    </#list>
</table>
</@layout.myLayout>
