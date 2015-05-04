-- description: creates users table
-- authoredAt: 20150321171230
-- up:

CREATE TABLE users (
	email text PRIMARY KEY,
	password text,
	id uuid,
	locale text,
	created_at timestamp,
	last_update timestamp 
);

-- down: