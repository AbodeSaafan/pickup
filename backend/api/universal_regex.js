var usernameRegex = /^[a-z0-9_]{4,10}$/; // Alphanumerical lowercase 4 to 10 characters
var passwordRegex = /^[a-z0-9A-Z?!~$#%*]{6,80}$/; // Alphanumerical 6 to 80 characters with ? ! ~ $ # % * allowed
var nameRegex = /^[a-zA-Z'-]*$/; // Alphabetical
var genderRegex = /^[m|f|o]$/i; // m/M for Male, f/F for Female, o/O for Other
var dateRegex = /^[0-1][0-9]\/[0-3][0-9]\/[0-9]{4}$/; // MM/DD/YYYY
var emailRegex = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
var gameNameRegex = /^[a-z0-9A-Z' ]{6,}$/; // Alphanumerical atleast 6 letters ' allowed and spaces
var gameTypeRegex = /^serious|casual$/i; // Serious or casual 
var gameDurationRegex = /^[0-9]*$/; // Any integer number for now
var gameTotalPlayersRegex = /^[0-9]*$/; // Any integer number for now
var gameGenderRegex = /^[m|f|a]$/i; // m/M for Male, f/F for Female, a/A for Any/No preferance
var gameDescriptionRegex = /^[a-zA-Z0-9'\-: ]*$/; // Alphanumerical with : - ' allowed and spaces
var gameLocationNotesRegex = /^[a-zA-Z0-9'\-: ]*$/; // Alphanumerical with : - ' allowed and spaces
var gameEnforcedParamRegex = /^age|gender$/i; // Skill, age or gender can be enforced
var idRegex = /^[0-9]*$/; // Any integer number for now
var searchObjectRegex = /^(game)|(user)$/;
var ratingRegex = /^[0-9]*$/; // Any integer number for now
var reviewTagRegex = /^[0-9]*$/; // Any integer number for now

module.exports = {
	usernameRegex,
	passwordRegex,
	nameRegex,
	genderRegex,
	dateRegex,
	emailRegex,
	gameNameRegex,
	gameTypeRegex,
	gameDurationRegex,
	gameTotalPlayersRegex,
	gameGenderRegex,
	gameDescriptionRegex,
	gameLocationNotesRegex,
	gameEnforcedParamRegex,
	idRegex,
	searchObjectRegex,
	ratingRegex,
	reviewTagRegex
};