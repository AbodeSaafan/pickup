var crypto = require("crypto");
var rNameg = require("random-name");

var baseApi = "http://localhost:3000/api";

var refreshEndpoint = baseApi + "/refresh";
var registerEndpoint = baseApi + "/register";
var loginEndpoint = baseApi + "/login";
var extendedProfileEndpoint = baseApi + "/extended_profile";
var createGameEndpoint = baseApi + "/games";
var getUsersOfGameEndpoint = baseApi + "/games/getUsers";
var joinGameEndpoint = baseApi + "/games/%s/join?jwt=%s";
var leaveGameEndpoint = baseApi + "/games/%s/leave?jwt=%s";
var adminProfileEndpoint = baseApi + "/profile";
var sendfriendsEndpoint = baseApi + "/friends";
var acceptFriendEndpoint = baseApi + "/friends/accept";
var deleteFriendEndpoint = baseApi + "/friends/delete";
var blockFriendEndpoint = baseApi + "/friends/block";
var listFriendsEndpoint = baseApi + "/friends/listFriends";
var listBlockedUsersEndpoint = baseApi + "/friends/listBlockedUsers";
var listFriendRequestEndpoint = baseApi + "/friends/listFriendRequest";
var searchEndpoint = baseApi + "/search";
var deleteAccountEndpoint = baseApi + "/delete";
var setReviewEndpoint = baseApi + "/reviews/setReview";
var changePasswordEndpoint = baseApi + "/changePassword";

function randomEmail(){
	return crypto.randomBytes(4).toString("hex") + "@mail.com";
}

function randomUsername(){
	return crypto.randomBytes(4).toString("hex");
}

function randomLocation(){
	return JSON.stringify({
		lng: (Math.random() * (180 - (-180)) - 180).toFixed(3) * 1,
		lat: (Math.random() * (180 - (-180)) - 180).toFixed(3) * 1
	});
}

function randomDob(){
	return "03/25/" + (Math.random() * (2000 - 1950) + 1950).toFixed(0) * 1;
}

function randomSkillLevel(){
	return (Math.random() * (10)).toFixed(0) * 1;
}


function randomPassword(){
	return crypto.randomBytes(4).toString("hex");
}

function createGenericUserMale(){
	return {
		username:randomUsername(),
		password:randomPassword(),
		fname:rNameg.first(),
		lname:rNameg.last(),
		gender:"m",
		dob:randomDob(),
		email:randomEmail()
	};
}

function createGenericUserUpdate(jwt){
	return {
		jwt: jwt,
		username:randomUsername(),
		password:"",
		fname:rNameg.first(),
		lname: rNameg.last(),
		gender:"m",
		dob:randomDob(),
		email:randomEmail()
	};
}

function createGenericUserUpdateWithFnameLname(jwt){
	return {
		jwt: jwt,
		username:"",
		password:"",
		fname:rNameg.first(),
		lname: rNameg.last(),
		gender:"",
		dob:"",
		email:""
	};
}

function createGenericUserUpdateWithUsernameEmail(jwt, password){
	return {
		jwt: jwt,
		username:randomUsername(),
		password:password,
		fname:"",
		lname: "",
		gender:"",
		dob:"",
		email:randomEmail()
	};
}

function createGenericUserUpdateWithGenderDob(jwt){
	return {
		jwt: jwt,
		username:"",
		password:"",
		fname:"",
		lname: "",
		gender:"f",
		dob:randomDob(),
		email:""
	};
}

function createGenericUserFemale(){
	return {
		username:randomUsername(),
		password:randomPassword(),
		fname:rNameg.first(),
		lname:rNameg.last(),
		gender:"f",
		dob:randomDob(),
		email:randomEmail()
	};
}


function createGenericUserFixedBirth(){
	return {
		username:randomUsername(),
		password:randomPassword(),
		fname:rNameg.first(),
		lname:rNameg.last(),
		gender:"m",
		dob:"03/25/1992",
		email:randomEmail()
	};
}

function createGenericGame(jwt, start, duration){
	return {
		name: rNameg.place() + " game",
		type: "casual",
		skill_offset: randomSkillLevel(),
		total_players_required: 3 + randomSkillLevel(),
		start_time: start,
		duration: duration,
		location: randomLocation(),
		location_notes: "Come around the back and knock on the blue door",
		description: "Casual basketball game",
		gender: "f",
		age_range: "[20, 30]",
		enforced_params: JSON.stringify(["gender", "age"]),
		jwt: jwt
	};
}

function createUnrestrictedGame(jwt, start, duration){
	var game = createGenericGame(jwt, start, duration);
	game.enforced_params = JSON.stringify([]);
	return game;
}

function createGenericExtendedProfile (jwt) {
	return {
		jwt: jwt,
		skill_level: randomSkillLevel(),
		location: randomLocation(),
	};
}

function createInvalidSkillLevelForExtendedProfile (jwt) {
	return {
		jwt: jwt,
		skill_level: "*",
		location: randomLocation(),
	};
}

function createInvalidLocationForExtendedProfile (jwt) {
	return {
		jwt: jwt,
		skill_level: randomSkillLevel(),
		location: "*",
	};
}

function createGenericFriendRequest (jwt, userID) {
	return {
		jwt: jwt,
		userId: userID
	};
}

function createGenericUsersRequest (jwt, gameId){
	return{
		game_id: gameId,
		jwt: jwt
	};
}

function createGenericReviewRequest(jwt, gameId, userId){
	return{
		gameId: gameId,
		userId: userId,
		rating: 1,
		tags: [1,1],
		jwt: jwt
	};

}

module.exports = {
	refreshEndpoint,
	registerEndpoint,
	loginEndpoint,
	randomEmail,
	extendedProfileEndpoint,
	createGenericUserMale,
	createGenericUserFemale,
	createGameEndpoint,
	createGenericGame,
	joinGameEndpoint,
	leaveGameEndpoint,
	adminProfileEndpoint,
	createGenericExtendedProfile,
	createGenericUserFixedBirth,
	sendfriendsEndpoint,
	createGenericFriendRequest,
	acceptFriendEndpoint,
	deleteFriendEndpoint,
	createInvalidSkillLevelForExtendedProfile,
	createInvalidLocationForExtendedProfile,
	blockFriendEndpoint,
	listFriendsEndpoint,
	listBlockedUsersEndpoint,
	searchEndpoint,
	createUnrestrictedGame,
	listFriendRequestEndpoint,
	deleteAccountEndpoint,
	getUsersOfGameEndpoint,
	createGenericUsersRequest,
	setReviewEndpoint,
	createGenericReviewRequest,
	createGenericUserUpdate,
	createGenericUserUpdateWithFnameLname,
	createGenericUserUpdateWithUsernameEmail,
	createGenericUserUpdateWithGenderDob,
	changePasswordEndpoint,
	randomPassword
};
