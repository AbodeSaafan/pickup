var jwt = require('jsonwebtoken');
var fs = require('fs');
var cert = fs.readFileSync('api/private.key');
var strings = require('../api/universal_strings')

module.exports = {
	createTokenForUser(user_id, email) {
		var payload = {user_id: user_id, email: email};
		var expiry = {expiresIn: '15m'};
		return jwt.sign(payload, cert, expiry);
	},
	verifyToken(token){
		try {
			return jwt.verify(token, cert);
		} catch(err) { // catch expired or broken and so on
  			throw new Error("Token Error: " + err.message);
		}
	},
	getUserFromToken(token){
		var info = jwt.decode(token);
		try{
			if(info.user_id && info.email){
				return info;
			}
		} catch(err){
			throw new Error(strings.invalidJwt);
		}
			
	}
}
