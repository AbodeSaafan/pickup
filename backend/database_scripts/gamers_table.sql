CREATE TABLE IF NOT EXISTS gamers (
	user_id int REFERENCES Users(user_id),
	game_id int REFERENCES Games(game_id),
	time_joined int DEFAULT extract(epoch from now()),
	PRIMARY KEY(user_id, game_id)
);