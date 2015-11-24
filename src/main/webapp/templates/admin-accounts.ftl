<#-- @ftlvariable name="currencies" type="java.util.List<ru.ulfr.poc.modules.bank.model.Currency>" -->
<#import "layout.ftl" as layout>
<@layout.myLayout>
<h1>Search Accounts</h1>
<p>please fill in at least one of:</p>
<div class="x-form">
    <div>
        <label for="criteria-user">User (user name, email)</label>
        <input type="text" id="criteria-user">
    </div>
    <div>
        <label for="criteria-account">Account (account id)</label>
        <input type="text" id="criteria-account">
    </div>
    <div>
        <label for="criteria-currency">Currency</label>
        <select id="criteria-currency" name="criteria-currency">
            <option value="">any</option>
            <#list currencies as currency>
                <option value="${currency.code?c}">${currency.uiCode}</option>
            </#list>
        </select>
    </div>
    <button onclick="POC.searchAccounts()">Search!</button>
</div>
<p>Results:</p>
<div id="search-results">

</div>
</@layout.myLayout>
