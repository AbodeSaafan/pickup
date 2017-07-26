var pg = require('pg');
const conString = "postgres://postgres:123@localhost:5432/pickup";

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
	var queryString = "SELECT user_id FROM users WHERE email =  '" + email + "';";

	const pool = new pg.Pool({connectionString: conString});

	pool.connect((err, client, done) => {
		client.query(queryString, (err, res) => {
  			if(res.rows[0]){
  				callback(res.rows[0].user_id);
  			} else {
  				console.log("Failed to get user id");
				callback(false);
  			}
  			done();
  			pool.end();
		});
	});
	}
}