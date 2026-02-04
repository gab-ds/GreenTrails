#!/bin/sh
# Script to inject environment variables at runtime

# Generate config.json with environment variable
cat > /usr/share/nginx/html/assets/config.json <<EOF
{
  "apiBaseUrl": "${API_BASE_URL}"
}
EOF

echo "Generated config.json with API_BASE_URL=${API_BASE_URL}"

# Start nginx
exec nginx -g 'daemon off;'
