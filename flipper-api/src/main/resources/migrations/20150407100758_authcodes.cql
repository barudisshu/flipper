-- description: creates authcodes
-- authoredAt: 20150407100758
-- up:

CREATE TABLE authcodes (
	authorization_code text PRIMARY KEY,
  	user_id uuid,
  	redirect_uri text,
  	created_at timestamp,
  	scope text,
  	client_id text,
  	expires_in bigint  		
);

-- down: