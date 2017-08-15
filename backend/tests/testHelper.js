var crypto = require('crypto');

var refreshEndpoint = 'http://localhost:3000/api/refresh';
var registerEndpoint = 'http://localhost:3000/api/register';
var loginEndpoint = 'http://localhost:3000/api/login';

function randomEmail(){
	return crypto.randomBytes(4).toString('hex') + "@mail.com";
}

function createGenericUser(){
	return {
    	nickname:'abode',
    	password:'password123',
    	fname:'abode',
    	lname:'saafan',
    	gender:'m',
    	dob:'25/03/1996',
        email:randomEmail()
	};
}

module.exports = {
	refreshEndpoint,
	registerEndpoint,
    loginEndpoint,
	randomEmail,
	createGenericUser
}
