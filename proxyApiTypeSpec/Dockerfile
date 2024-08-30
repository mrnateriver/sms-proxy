FROM node:lts-alpine
WORKDIR /app
COPY . .
RUN chown -R node:node /app
USER node
RUN npm audit signatures && npm install
CMD ["npm", "run", "build"]
