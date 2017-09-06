CREATE TABLE IF NOT EXISTS Game (
	game_id BIGSERIAL PRIMARY KEY,
	name varchar(255),
	type varchar(255),
	skill int,
	total_players_required int,
	start_time int,
	duration int,
	location point,
	creator_id int REFERENCES Users(user_id),
	description varchar(255),
	location_notes varchar(255),
	gender varchar(255),
	age_range int[2],
	enforced_params varchar(255)[3],
	time_created int DEFAULT extract(epoch from now())
);