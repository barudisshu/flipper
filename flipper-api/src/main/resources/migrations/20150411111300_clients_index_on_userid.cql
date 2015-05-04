-- description: creates clients index on user_id
-- authoredAt: 20150411111300
-- up:

CREATE INDEX clients_user_id ON clients (user_id);


-- down: