CREATE TYPE enforced_param_type AS ENUM ('gender', 'age');

CREATE TABLE IF NOT EXISTS Games (
	game_id BIGSERIAL PRIMARY KEY,
	name varchar(255),
	type varchar(255),
	min_skill int,
	max_skill int,
	total_players_required int,
	total_players_added int DEFAULT 0,
	start_time int,
	end_time int,
	location point,
	creator_id int REFERENCES Users(user_id),
	description varchar(255),
	location_notes varchar(255),
	gender varchar(255),
	age_range int[2],
	enforced_params enforced_param_type[2],
	time_created int DEFAULT extract(epoch from now())
);