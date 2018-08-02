<#import "/spring.ftl" as spring>
<html>
<h1>My publications</h1>
<ul>
<#list publications as publication>
    <li>${publication}</li>
</#list>
</ul>
<br>
<a href="${logout_url}">Logout</a>
</html>