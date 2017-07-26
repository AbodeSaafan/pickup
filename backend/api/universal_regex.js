var nicknameRegex = /^[a-z0-9]{4,10}$/;
var passwordRegex = /^[a-z0-9A-Z]{6,20}$/;
var nameRegex = /^[a-zA-Z]*$/;
var genderRegex = /^[m|f|o]$/;
var dateRegex = /^[0-3][0-9]\/[0-1][0-9]\/[0-9]{4}$/; // DD/MM/YY
var emailRegex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

module.exports = {
	nicknameRegex,
	passwordRegex,
	nameRegex,
	genderRegex,
	dateRegex,
	emailRegex
}