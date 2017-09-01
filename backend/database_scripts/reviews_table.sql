CREATE TABLE IF NOT EXISTS Reviews (
	user_id int REFERENCES Users(user_id),
	game_id int REFERENCES Game(game_id),
	reviewer_id int,
	rating int, 
	tags int[],
	PRIMARY KEY(user_id)
);