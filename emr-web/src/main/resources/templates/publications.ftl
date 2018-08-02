<#import "/spring.ftl" as spring>
<html>
<h1>My publications</h1>
<ul>
<#list publications as publication>
    <li>${publication}</li>
</#list>
</ul>
<br>
<#if logoutUrl??>
<a href="${logoutUrl}">Logout</a>
</#if>
</html>