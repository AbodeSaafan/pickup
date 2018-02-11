var frisby = require("frisby");
var testHelper = require("./testHelper");
var strings = require("../api/universal_strings");

// Provide a valid update password request for a valid user
// Check if user can log in with new Password
// Check if user cannot log in with old Password

var user = testHelper.createGenericUserMale();
var new_password = testHelper.randomPassword();
frisby.create("Register a user using the API with valid credentials to use for profile testing")
	.post(testHelper.registerEndpoint, user)
	.expectStatus(200)
	.afterJSON(function (body) {
    frisby.create("Update User password")
    .put(testHelper.changePasswordEndpoint, {jwt: body.token, old_password: user.password, new_password: new_password})
    .expectStatus(200)
    .afterJSON(function(changePassword) {
      frisby.create("Log the user in with new password")
      .post(testHelper.loginEndpoint, {email: user.email, password: new_password})
      .expectStatus(200)
      .expectBodyContains("token")
			.expectBodyContains("user_id")
      .afterJSON(function(checkLogInWithOldPassword) {
        frisby.create("Log the user in with old password")
        .post(testHelper.loginEndpoint, {email: user.email, password: user.password})
        .expectStatus(400)
        .expectJSON({
      		error: strings.loginError
      	})
        .toss()
      })
      .toss()
    })
    .toss()
  })
	.toss();

  //Check if error is prompted if user logs in with incorrect old Password
  frisby.create("Register a user using the API with valid credentials to use for profile testing")
  	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
  	.expectStatus(200)
  	.afterJSON(function (body) {
      frisby.create("Update User password")
      .put(testHelper.changePasswordEndpoint, {jwt: body.token, old_password: "1234", new_password: new_password})
      .expectStatus(400)
      .expectJSON({
        error: strings.invalidOldPassword
      })
      .toss();
    })
    .toss();

  //Check if error is prompted if user enters an invalid new password
  var new_user = testHelper.createGenericUserMale();
  frisby.create("Register a user using the API with valid credentials to use for profile testing")
  	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
  	.expectStatus(200)
  	.afterJSON(function (body) {
      frisby.create("Update User password")
      .put(testHelper.changePasswordEndpoint, {jwt: body.token, old_password: new_user.password, new_password: "*/"})
      .expectStatus(400)
      .toss();
    })
    .toss();
