# Sample apps protected with Keycloak with OIDC

## Projects
* `heroes-rest-api-keycloak-protected` - REST API secured as resource server using **spring-boot-starter-oauth2-resource-server** it checks for `access_to_heroes_api` scope (cleint must have it for access)
* `webapp-keycloak-protected` - Spring MVC (Thymeleaf) app secured using **spring-boot-starter-oauth2-client**
* `angular-spa-keycloak-protected` - Angular SPA secured using **angular-oauth2-oidc**
* `simple-job-rest-client` - Job-like app which calls protected rest api. Uses RestTemplate from Spring and **Nimbus** library for Auth token request
* `javaee-war-app-protected` - Servlet-based app protected with custom Filter which support autorization code flow. Uses  **Nimbus** SDK for Auth token request, JWT parsing and validation.

## OAuth2 Flows
* Authorization Code Flow - `webapp-keycloak-protected`
* PKCE Authorization Code Flow - `angular-spa-keycloak-protected`
* Client Credentials Flow - `simple-job-rest-client`

## Keycloak MyMvcWelcomeApp
* Exported in `myrealm-keycloak-export.json`
* Realm name: `myrealm`
* Test user: test/Welcome1
* Clients:
    - MyMvcWelcomeApp: secured client (confidential access type), used for `webapp-keycloak-protected` app
    - MyAngularSpaApp: public client (public access type), used for `angular-spa-keycloak-protected`app
    - MyJobServiceRestClient: secured client (confidential access type), used for `simple-job-rest-client` app
    - MyServletJavaEEApp: secured client (confidential access type), used for `javaee-war-app-protected` app
