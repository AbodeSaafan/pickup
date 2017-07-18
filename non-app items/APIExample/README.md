## Example API

To start up the API launch bash or powershell and navigate to this directory then: 

$ npm install
$ npm start 

### npm Install
This only needs to be ran the first time you start a project and anytime after you change dependecies or add modules. If your code does not start after a pull you can run it to see if it resolves the issues. Running it at anytime has no harm unless you are testing specific versions of modules.

### npm start

This runs a startup script that should launch the API. In this example app it starts and hosts the API on localhost port 3000 through nodemon which will allow you to make changes to the api.js or server.js code hit save and it will automatically reload for you without having to run npm start again. 