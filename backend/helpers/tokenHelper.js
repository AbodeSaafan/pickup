var jwt = require("jsonwebtoken");
var fs = require("fs");
var cert = fs.readFileSync("api/private.key");
var strings = require("../api/universal_strings");

function createTokenForUser(user_id, email) {
	var payload = {user_id: user_id, email: email};
	var expiry = {expiresIn: "15m"};
	return jwt.sign(payload, cert, expiry);
}

function verifyToken(token){
	try {
		return jwt.verify(token, cert);
	} catch(err) { // catch expired or broken and so on
		throw new Error("Token Error: " + err.message);
	}
}

function getUserFromToken(token){
	var info = jwt.decode(token);
	try{
		if(info.user_id && info.email){
			return info;
		}
		else {
			throw new Error(strings.invalidJwt);
		}
	} catch(err){
		throw new Error(strings.invalidJwt);
	}
}

module.exports = {
	createTokenForUser,
	verifyToken,
	getUserFromToken
};