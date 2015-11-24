<#import "layout.ftl" as layout>
<@layout.myLayout>
<h1>IBS POC</h1>
<p>Internet Banking System POC (development sample)</p>
<p>Development time: 24 hours (limited)</p>
<h3>Definition</h3>
<p>Implement Internet Banking Accounting system, with UI and data presentation</p>
<p>System should support:</p>
<ul>
    <li>Customer and Admin interfaces</li>
    <li>Multiple accounts per customer</li>
    <li>Multiple currencies</li>
    <li>Multi-currency Deposit/Withdraw operations for customer</li>
    <li>Multi-currency account-to-account transfer withing the system</li>
    <li>Ability to list account transactions</li>
    <li>Ability to show/download statement for specific transaction</li>
    <li>Search accounts for administrator</li>
</ul>
<h3>Implementation</h3>
<p>Java-based implementation for web service, with use of REST for admin interface</p>
<ul>
    <li>Oracle Java 8</li>
    <li>Spring 4.2.3 (MVC, Security, JPA) annotation-driven</li>
    <li>Hibernate 5 ORM</li>
    <li>MySQL 5.6 InnoDB engine</li>
    <li>Apache PDFBox for generating PDF</li>
    <li>FreeMarker template engine</li>
    <li>JavaScript/jQuery for AJAX access to REST API</li>
    <li>JUnit test for low-level DAO tests</li>
    <li>JUnit/Mockito/Spring for REST API tests</li>
    <li>Maven</li>
</ul>

<h2>Usage</h2>
<p>Administrative pair: admin / admin</p>
<p>User pair: user0001 / pass0001</p>
<h2>Contacts</h2>
<p>Alexander Terekhov</p>
<p>Skype: ssterekhov<br/>
    E-mail: svulfr@gmail.com</p>
</@layout.myLayout>
