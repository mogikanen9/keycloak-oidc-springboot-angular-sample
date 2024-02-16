# Sample apps protected with Keycloak with OIDC

## Projects
* `webapp-keycloak-protected` - Spring MVC (Thymeleaf) app secured using **spring-boot-starter-oauth2-client**
* `angular-spa-keycloak-protected` - Angular SPA secured using **angular-oauth2-oidc**
* `heroes-rest-api-keycloak-protected` - REST API secured as resource server using **spring-boot-starter-oauth2-resource-server**

## OAuth2 Flows
* Authorization Code Flow - `webapp-keycloak-protected`
* PKCE Authorization Code Flow - `angular-spa-keycloak-protected`

## Keycloak MyMvcWelcomeApp
* Exported in `myrealm-keycloak-export.json`
* Realm name: `myrealm`
* Test user: test/Welcome1
* Clients:
    - MyMvcWelcomeApp: secured client (confidential access type), used for `webapp-keycloak-protected` app
    - MyAngularSpaApp: public client (public access type), used for `angular-spa-keycloak-protected`app
