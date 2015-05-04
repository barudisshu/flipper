-- description: creates clients table
-- authoredAt: 20150411110913
-- up:
	
CREATE TABLE clients (
	id text,
	secret text,
	user_id uuid,
	redirect_uri text,
	scope text,
	grant_types set<text>,
	
	PRIMARY KEY(id,secret) 
);

-- down: