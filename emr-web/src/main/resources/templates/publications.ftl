<#import "/spring.ftl" as spring>
<html>
<h2>My publications</h2>
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