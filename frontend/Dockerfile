# I need to build a Dockerfile for an Angular frontend application. The Dockerfile should use a multi-stage build to first build the Angular application using a Node.js image, and then serve the built application using an Nginx image. The Angular application source code is located in the current directory. The final Docker image should expose port 4200. Please provide the complete Dockerfile code.
FROM node:24-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN apk update && apk add npm
RUN npm install
COPY . .
RUN npm run build --prod

FROM nginx:alpine AS runtime
COPY --from=builder /app/dist/frontend/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Set default environment variable
ENV API_BASE_URL=http://localhost:8080

EXPOSE 80
ENTRYPOINT ["/docker-entrypoint.sh"]
