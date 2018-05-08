CREATE TABLE IF NOT EXISTS Extended_profile (
	user_id int REFERENCES Users(user_id),
	username varchar(255) REFERENCES Users(username) on update cascade,
	skilllevel int default 0,
	location varchar(255),
	average_review real default 0,
	PRIMARY KEY(user_id)
);
