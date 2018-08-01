<#import "/spring.ftl" as spring>
<html>
<h1>My publications</h1>
<ul>
<#list publications as publication>
    <li>${publication}</li>
</#list>
</ul>
<br>
<a href="http://localhost:8080/auth/realms/demo/protocol/openid-connect/logout?redirect_uri=http://localhost:8083">Logout</a>
</html>