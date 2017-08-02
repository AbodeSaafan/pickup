var nicknameRegex = /^[a-z0-9]{4,10}$/; // Alphanumerical lowercase 4 to 10 characters
var passwordRegex = /^[a-z0-9A-Z]{6,80}$/; // Alphanumerical 6 to 80 characters
var nameRegex = /^[a-zA-Z]*$/; // Alphabetical
var genderRegex = /^[m|M|f|F|o|O]$/; // m/M for Male, f/F for Female, o/O for Other
var dateRegex = /^[0-3][0-9]\/[0-1][0-9]\/[0-9]{4}$/; // DD/MM/YYYY
var emailRegex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

module.exports = {
	nicknameRegex,
	passwordRegex,
	nameRegex,
	genderRegex,
	dateRegex,
	emailRegex
}