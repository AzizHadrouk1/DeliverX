#!/usr/bin/env bash
# Validation script: driver-client-service + Keycloak auth
# Run against the full docker compose stack (gateway on :8090, keycloak on :8080)

set -e

get_admin_token() {
  curl -s -X POST http://localhost:8080/realms/master/protocol/openid-connect/token \
    -d client_id=admin-cli -d grant_type=password -d username=admin -d password=admin \
    | grep -o '"access_token":"[^"]*"' | sed 's/"access_token":"//;s/"//'
}

echo "== Fetching driver-client-service client secret from Keycloak =="
ADMIN_TOKEN=$(get_admin_token)
CID=$(curl -s -H "Authorization: Bearer $ADMIN_TOKEN" \
  "http://localhost:8080/admin/realms/deliverx/clients?clientId=driver-client-service" \
  | grep -o '"id":"[^"]*"' | head -1 | sed 's/"id":"//;s/"//')
SECRET=$(curl -s -H "Authorization: Bearer $ADMIN_TOKEN" \
  "http://localhost:8080/admin/realms/deliverx/clients/$CID/client-secret" \
  | grep -o '"value":"[^"]*"' | sed 's/"value":"//;s/"//')

get_user_token() {
  curl -s -X POST http://localhost:8080/realms/deliverx/protocol/openid-connect/token \
    -d client_id=driver-client-service -d client_secret="$SECRET" \
    -d grant_type=password -d username="$1" -d password="$2" \
    | grep -o '"access_token":"[^"]*"' | sed 's/"access_token":"//;s/"//'
}

ADMIN=$(get_user_token admin1 admin123)
USER=$(get_user_token client1 client123)

st() { curl -s -o /dev/null -w "%{http_code}" "$@"; }

BODY='{"firstName":"Val","lastName":"Test","email":"val.test@deliverx.tn","phone":"+21620000099","licenseNumber":"DL-VAL-1","status":"AVAILABLE"}'

echo
echo "== Results (through the Gateway, :8090) =="
echo "GET  /drivers          (public)              -> $(st http://localhost:8090/drivers)                (expect 200)"
echo "POST /drivers  no token                       -> $(st -X POST http://localhost:8090/drivers -H 'Content-Type: application/json' -d "$BODY") (expect 401)"
echo "POST /drivers  user role   (wrong role)        -> $(st -X POST http://localhost:8090/drivers -H "Authorization: Bearer $USER" -H 'Content-Type: application/json' -d "$BODY") (expect 403)"
echo "POST /drivers  admin role  (allowed)            -> $(st -X POST http://localhost:8090/drivers -H "Authorization: Bearer $ADMIN" -H 'Content-Type: application/json' -d "$BODY") (expect 201)"
echo "GET  /clients/me  any authenticated user        -> $(st http://localhost:8090/clients/me -H "Authorization: Bearer $USER") (expect 200 or 404 if no matching client email)"
