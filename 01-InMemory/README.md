# Spring Security Experiments

1. Spring Security
2. Oauth2
3. H2

base64(lfdajr:lfdajr-pass) = bGZkYWpyOmxmZGFqci1wYXNz

~~~~
curl --request POST \
  --url http://localhost:8080/oauth/token \
  --header 'authorization: Basic bGZkYWpyOmxmZGFqci1wYXNz' \
  --header 'content-type: application/x-www-form-urlencoded' \
  --data grant_type=password \
  --data username=Lourival \
  --data password=senha
~~~~

~~~~
curl --request POST \
  --url http://localhost:8080/oauth/token \
  --header 'authorization: Basic bGZkYWpyOmxmZGFqci1wYXNz' \
  --header 'content-type: application/x-www-form-urlencoded' \
  --data grant_type=client_credentials
~~~~

# Criando Keystore (.jks)

 Chave .jks criada a partir do comando
 
 ~~~~
 keytool -genkeypair -alias lfdajr-oauth-jwt -keyalg RSA -keypass lfdajr_dv -keystore lfdajr-keystore-file.jks -storepass lfdajr_dv
~~~~

Buscando a chave pública da Keystore

~~~~
 keytool -list -rfc --keystore lfdajr-keystore-file.jks | openssl x509 -inform pem -pubkey
~~~~
