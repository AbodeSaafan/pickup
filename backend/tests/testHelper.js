var crypto = require('crypto');

var baseApi = 'http://localhost:3000/api';

var refreshEndpoint = baseApi + '/refresh';
var registerEndpoint = baseApi + '/register';
var loginEndpoint = baseApi + '/login';
var extendedProfileEndpoint = baseApi + '/extended_profile';
var createGameEndpoint = baseApi + '/games';
var joinGameEndpoint = baseApi + '/games/%s/join?jwt=%s';
var leaveGameEndpoint = baseApi + '/games/%s/leave?jwt=%s';
var adminProfileEndpoint = baseApi + '/profile';
var sendfriendsEndpoint = baseApi + '/friends';
var acceptFriendEndpoint = baseApi + '/friends/accept'
var deleteFriendEndpoint = baseApi + '/friends/delete'

function randomEmail(){
	return crypto.randomBytes(4).toString('hex') + "@mail.com";
}

function randomUsername(){
	return crypto.randomBytes(4).toString('hex');
}

function randomLocation(){
	return {
		lat: (Math.random() * (180 - (-180)) - 180).toFixed(3) * 1,
		lng: (Math.random() * (180 - (-180)) - 180).toFixed(3) * 1
	}
}

function randomDob(){
	return '25/03/' + (Math.random() * (2000 - 1950) + 1950).toFixed(0) * 1
}

function randomSkillLevel(){
	return (Math.random() * (10)).toFixed(0) * 1
}

function createGenericUser(){
	return {
		username:randomUsername(),
		password:'password123',
		fname:'abode',
		lname:'saafan',
		gender:'m',
		dob:randomDob(),
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
		location: randomLocation(),
		location_notes: "Come around the back and knock on the blue door",
		description: "Casual basketball game",
		gender: "A",
		age_range: [20, 30],
		enforced_params: ["gender", "age"],
		jwt: jwt
	};
}

function createGenericExtendedProfile (jwt) {
	return {
		jwt: jwt,
		skill_level: randomSkillLevel(),
		location: randomLocation(),
	}
}

function createInvalidSkillLevelForExtendedProfile (jwt) {
	return {
		jwt: jwt,
		skill_level: '*',
		location: randomLocation(),
	}
}

function createInvalidLocationForExtendedProfile (jwt) {
	return {
		jwt: jwt,
		skill_level: randomSkillLevel(),
		location: '*',
	}
}

function createGenericFriendRequest (jwt, userID) {
	return {
		jwt: jwt,
		userID: userID
	}
}

module.exports = {
	refreshEndpoint,
	registerEndpoint,
	loginEndpoint,
	randomEmail,
	extendedProfileEndpoint,
	createGenericUser,
	createGameEndpoint,
	createGenericGame,
	joinGameEndpoint,
	leaveGameEndpoint,
	adminProfileEndpoint,
	createGenericExtendedProfile,
	sendfriendsEndpoint,
	createGenericFriendRequest,
	acceptFriendEndpoint,
	deleteFriendEndpoint,
	createInvalidSkillLevelForExtendedProfile,
	createInvalidLocationForExtendedProfile
}
