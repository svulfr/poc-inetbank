<#-- @ftlvariable name="account" type="ru.ulfr.poc.modules.account.model.Account" -->
<#import "layout.ftl" as layout>
<@layout.myLayout>
<h1>Transactions for account ${account.id?c}</h1>
<table id="transactions-list"></table>
<script type="application/javascript">
    $(document).ready(function () {
        POC.listTransactions(${account.id?c});
    });
</script>
</@layout.myLayout>
