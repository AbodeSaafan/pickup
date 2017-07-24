CREATE TABLE IF NOT EXISTS Users (
	user_id varchar(255) PRIMARY KEY,
	username varchar(255),
	fname varchar(255),
	lname varchar(255),
	dob varchar(255),
	gender varchar(255),
	email varchar(255),
	password varchar(255),
	salt varchar(255)
);