CREATE TABLE IF NOT EXISTS Reviews (
	user_id int REFERENCES Users(user_id),
	game_id int REFERENCES Game(game_id),
	rating int, 
	comment varchar(255),
	review_time timestamptz default current_timestamp,
	PRIMARY KEY(user_id)
);