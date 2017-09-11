var crypto = require('crypto');

var baseApi = 'http://localhost:3000/api';

var refreshEndpoint = baseApi + '/refresh';
var registerEndpoint = baseApi + '/register';
var loginEndpoint = baseApi + '/login';
var extendedProfileEndpoint = baseApi + '/extended_profile';
var createGameEndpoint = baseApi + '/games';

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

function createGenericGame(jwt, start, duration){
	return {
      	name: "abode's game",
      	type: "casual",
      	skill_offset: 5,
      	total_players_required: 6,
      	start_time: start,
	    duration: duration,
      	location: {lat: 500.50, lng:-500.50},
	  	location_notes: "Come around the back and knock on the blue door",
      	description: "Casual basketball game",
      	gender: "A",
      	age_range: [20, 30],
    	enforced_params: ["gender", "age"],
    	jwt: jwt
    };
}

module.exports = {
	refreshEndpoint,
	registerEndpoint,
    loginEndpoint,
	randomEmail,
	extendedProfileEndpoint,
	createGenericUser,
	createGameEndpoint,
	createGenericGame
}
