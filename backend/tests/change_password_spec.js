var frisby = require("frisby");
var testHelper = require("./testHelper");
var strings = require("../api/universal_strings");

describe("Testing change password api", function () {
	it("Can change a user's password, making the old password invalid", function(doneFn) {
		var user = testHelper.createGenericUserMale();
		var new_password = testHelper.randomPassword();
		return frisby.post(testHelper.registerEndpoint, user)
			.expect("status", 200)
			.then(function (body) {
				body = body.json;
				return frisby.put(testHelper.changePasswordEndpoint, {jwt: body.token, old_password: user.password, new_password: new_password})
					.expect("status", 200)
					.then(function() {
						return frisby.post(testHelper.loginEndpoint, {email: user.email, password: new_password})
							.expect("status", 200)
							.expect("bodyContains", "token")
							.expect("bodyContains", "user_id")
							.then(function() {
								return frisby.post(testHelper.loginEndpoint, {email: user.email, password: user.password})
									.expect("status", 400)
									.expect("jsonStrict", {
										error: strings.loginError
									}).done(doneFn);
							});
					});
			});
	});
	
	
	it("User should not be able to change their password with an invalid password passed in", function(doneFn) {
		var user = testHelper.createGenericUserMale();
		var new_user = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, user)
			.expect("status", 200)
			.then(function (body) {
				body = body.json;
				return frisby.put(testHelper.changePasswordEndpoint, {jwt: body.token, old_password: new_user.password, new_password: "/"})
					.expect("status", 400)
					.expect("jsonStrict", {
						error: strings.invalidPassword
					})
					.then(function () {
						return frisby.post(testHelper.loginEndpoint, {email: user.email, password: user.password})
							.expect("status", 200)
							.expect("bodyContains", "token")
							.expect("bodyContains", "user_id").done(doneFn);
					});
			});
	});
});