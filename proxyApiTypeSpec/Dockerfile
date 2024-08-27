FROM node:lts-alpine
WORKDIR /app
COPY . .
#RUN chown -R node:node /app
#USER node
RUN npm install
RUN mkdir tsp-output # && chown -R node:node /app/tsp-output
CMD ["npm", "run", "build"]
