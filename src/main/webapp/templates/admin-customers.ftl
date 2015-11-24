<#import "layout.ftl" as layout>
<@layout.myLayout>
<h1>Search Customers</h1>
<label for="criteria">Criteria (user name, email)</label>
<input type="text" id="criteria" name="criteria">
<button onclick="POC.searchUser($('#criteria').val())">Search!</button>
<div id="search-results">

</div>
</@layout.myLayout>
