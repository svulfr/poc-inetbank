<#-- @ftlvariable name="account" type="ru.ulfr.poc.modules.account.model.Account" -->
<#-- @ftlvariable name="party" type="ru.ulfr.poc.modules.users.model.User" -->
<#-- @ftlvariable name="transaction" type="ru.ulfr.poc.modules.processor.model.Transaction" -->
<#-- @ftlvariable name="currencies" type="java.util.Map<java.lang.Integer,ru.ulfr.poc.modules.bank.model.Currency>" -->
<#import "layout.ftl" as layout>
<@layout.myLayout>
    <#switch transaction.txType>
        <#case 0>
            <#if transaction.originId == account.id>
                <#assign opType>payment</#assign>
                <#assign opSign="-">
            <#else>
                <#assign opType>income</#assign>
                <#assign opSign="+">
            </#if>
            <#break>
        <#case 1>
            <#assign opType>withdrawal</#assign>
            <#assign opSign="-">
            <#break >
        <#case 2>
            <#assign opType>deposit</#assign>
            <#assign opSign="+">
            <#break >
    </#switch>

    <#if transaction.originId?? && transaction.originId == account.id>
        <#assign lcAmount>${transaction.originAmount} ${currencies[transaction.originCurrency?string].uiCode}</#assign>
    <#else>
        <#assign lcAmount>${transaction.recipientAmount} ${currencies[transaction.recipientCurrency?string].uiCode}</#assign>
    </#if>
<h1>Statement</h1>
<table>
    <tr>
        <td>Date:</td>
        <td>${transaction.createdOn?string.medium}</td>
    </tr>
    <tr>
        <td>Type:</td>
        <td>${opType}</td>
    </tr>
    <tr>
        <td>Amount:</td>
        <td>${transaction.txAmount} ${currencies[transaction.txCurrency?string].uiCode}</td>
    </tr>
    <tr>
        <td>Account currency:</td>
        <td>${account.currency.name}</td>
    </tr>
    <tr>
        <td>Account balance change:</td>
        <td>${opSign}${lcAmount}</td>
    </tr>
    <#if transaction.party??>
        <tr>
            <td><#if transaction.originId == account.id>Paid to:<#else>Received from:</#if></td>
            <td>${transaction.party.name} (${transaction.party.email})</td>
        </tr>
    </#if>
</table>
</@layout.myLayout>
