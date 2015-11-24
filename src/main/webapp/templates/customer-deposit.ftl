<#-- @ftlvariable name="account" type="ru.ulfr.poc.modules.account.model.Account" -->
<#-- @ftlvariable name="currencies" type="java.util.List<ru.ulfr.poc.modules.bank.model.Currency>" -->
<#import "layout.ftl" as layout>
<@layout.myLayout>
<h1>Deposit</h1>
<div>
    <label for="deposit-amount">Amount</label>
    <input type="text" id="deposit-amount">
    <select id="deposit-currency">
        <#list currencies as currency>
            <option value="${currency.code?c}">${currency.uiCode}</option>
        </#list>
    </select>
    <button onclick="POC.deposit(${account.id?c})">deposit</button>
</div>
<div class="result" id="operation-success" style="display: none">
    <p>Operation status: SUCCESS</p>

    <p><a href="/customer/">Back to home</a></p>
</div>
<div class="result" id="operation-fail-unknown" style="display: none">
    <p>Operation status: FAILED, unknown reason</p>

    <p><a href="/customer/">Back to home</a></p>
</div>
</@layout.myLayout>
