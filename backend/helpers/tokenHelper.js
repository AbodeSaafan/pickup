var jwt = require('jsonwebtoken');
var fs = require('fs');
var cert = fs.readFileSync('api/private.key');

module.exports = {
	createTokenForUser(user_id, email) {
		var payload = {user_id: user_id, email: email};
		var expiry = {expiresIn: '1h'};
		return jwt.sign(payload, cert, expiry);
	},
	verifyToken(token){
		try {
			return jwt.verify(token, cert);
		} catch(err) { // catch expired or broken and so on
  			throw new Error("Token Error: " + err.message);
		}
	}
}
