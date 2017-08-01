var pg = require('pg');
const conString = "postgres://postgres:123@localhost:5432/pickup";
var crypto = require('crypto');

module.exports = {
	checkEmailUniqueness(user, callback){
		var queryString = "SELECT * FROM users WHERE email = '" + user.email +"';";

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, (err, res) => {
  				callback(!(res.rows[0] || err));
  				done();
				pool.end();
			});
		});
	},
	registerUser(user, callback){
		var queryString = "INSERT INTO users(nickname, fname, lname, dob, gender, email, password, salt) VALUES($1, $2, $3, $4, $5, $6, $7, $8)";
		var queryParams = [user.nickname, user.fname, user.lname, user.dob, user.gender, user.email, user.hashedPassword, user.salt];

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				callback(!err);
  				done();
				pool.end();
			});
		});
	},
	getUserId(email, callback){
		var queryString = "SELECT user_id FROM users WHERE email =  $1;";
		var queryParams = [email];

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				if(!err && res.rows[0].user_id){
	  				callback(res.rows[0].user_id);
  				} else {
	  				console.log("Failed to get user id");
					callback(false);
  				}
  				done();
  				pool.end();
			});
		});
	},
	getUserRowById(userId, callback){
		var queryString = "SELECT * FROM users WHERE user_id = $1";
		var queryParams = [userId];

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				if(!err && res.rows[0]){
	  				callback(res.rows[0]);
  				} else {
	  				console.log("Failed to get user row");
					callback(false);
  				}
  				done();
  				pool.end();
			});
		});
	},
	getRefreshToken(userId, refreshToken, callback){
		var queryString = "SELECT * FROM refresh WHERE user_id = $1 and refresh_token = $2;";
		var queryParams = [userId, refreshToken];

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				if(!err && res.rows[0].refresh_token){
  					callback(res.rows[0].refresh_token);
  				} else {
	  				console.log("Failed to get refresh token" + err);
					callback(false);
  				}
  				done();
  				pool.end();
			});
		});
	},
	createRefreshToken(userId, callback){
		var refreshToken = crypto.randomBytes(50).toString('hex');
		var queryString = "INSERT INTO refresh(user_id, refresh_token) VALUES($1, $2);";
		var queryParams = [userId, refreshToken];

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				var result = err ? null : refreshToken;
  				callback(result);
  				done();
				pool.end();
			});
		});
	}
}