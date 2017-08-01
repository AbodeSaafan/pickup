var uniqueEmailError = "Register failed: Email is not unique";
var registerFailError = "Failed to register user into the database";
var userIdFail = "Failed to retrieve user id";
var invalidNickname = "Invalid Nickname";
var invalidPassword = "Invalid password";
var invalidName = "Invalid name";
var invalidGender = "Invalid gender parameter";
var invalidDob = "Invalid date of birth";
var invalidEmail = "Invalid email";
var noRefreshToken = "No refresh token has been found for you, please attempt to login to acquire a new token";
var createRefreshFail = "Unable to create refresh token";
var invalidJwt = "The JWT token you have provided is invalid";

module.exports = {
	uniqueEmailError,
	registerFailError,
	userIdFail,
	invalidNickname,
	invalidPassword,
	invalidName,
	invalidGender,
	invalidDob,
	invalidEmail,
	noRefreshToken,
	createRefreshFail,
	invalidJwt
}