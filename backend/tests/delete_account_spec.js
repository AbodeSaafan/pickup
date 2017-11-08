var frisby = require('frisby');
var strings = require('../api/universal_strings');
var testHelper = require('./testHelper');

// Get admin profile properly
var userDetails = testHelper.createGenericUser()
frisby.create('Register a user using the API with valid credentials to use for delete account testing')
.post(testHelper.registerEndpoint, userDetails)
.expectStatus(200)
 .afterJSON(function (user) {
   frisby.create('Delete account')
   .delete(deleteAccountEndpoint+"?jwt="+user.token+"&email="+userDetails.email+"&password="+userDetails.password)
   .expectStatus(200)
   .after(function() {
      frisby.create('Get admin profile of user')
      .get(adminProfileEndpoint+"?jwt="+user.token)
      .expectStatus(400)
      .expectJSON({
         'error': strings.userIdFail
      })
      .toss();
   })
   .toss();
})
 .toss();