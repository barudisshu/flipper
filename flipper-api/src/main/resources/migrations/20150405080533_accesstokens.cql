-- description: creates accesstokens
-- authoredAt: 20150405080533
-- up:

CREATE TABLE accesstokens (
	user_id uuid,
	client_id text,
	access_token text,
	refresh_token text,	
	scope text,
	expires_id bigint,
	created_at timestamp,

	PRIMARY KEY( (user_id, client_id) )  		
);

-- down: