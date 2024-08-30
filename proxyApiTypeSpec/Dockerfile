FROM node:lts-alpine
WORKDIR /app
COPY . .
RUN chown -R node:node /app
USER node
RUN npm install
RUN npm audit signatures
CMD ["npm", "run", "build"]
