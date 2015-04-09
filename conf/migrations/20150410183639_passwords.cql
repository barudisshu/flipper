-- description: creates password table
-- authoredAt: 20150410183639
-- up:

CREATE TABLE passwords (
	email text PRIMARY KEY,
	password text 
);

-- down: