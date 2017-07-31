CREATE TABLE IF NOT EXISTS Game (
	game_id BIGSERIAL PRIMARY KEY,
	name varchar(255),
	type varchar(255),
	intended_skill int,
	total_players_required int,
	start_time timestamptz,
	duration time,
	location varchar(255),
	creator_id int REFERENCES Users(user_id),
	description varchar(255),
	time_created timestamptz
);