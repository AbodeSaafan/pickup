var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");

var genericUser = testHelper.createGenericUserMale();

describe("Login api tests", function () {
	it("Attempt to login a user using the wrong password", function() {
		return frisby.post(testHelper.loginEndpoint, {
			email: genericUser.email,
			password: "wrongPassword"
		})
			.expect("status", 400)
			.expect("jsonStrict", {
				error: strings.invalidPasswordSignIn
			});
	});
	it("Sending a login request with a correct email/password combination", function() {
		return frisby.post(testHelper.registerEndpoint, genericUser) 
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.expect("bodyContains", "refresh")
			.then(function () {
				return frisby.post(testHelper.loginEndpoint, {
					email: genericUser.email,
					password: genericUser.password
				})
					.expect("status", 200)
					.expect("bodyContains", "token")
					.expect("bodyContains", "user_id")
					.expect("bodyContains", "token");
			});
	});
});