# Build Stage
FROM node:18 AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build

# Serve Stage
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY ./env.js /usr/share/nginx/html/env.js
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
