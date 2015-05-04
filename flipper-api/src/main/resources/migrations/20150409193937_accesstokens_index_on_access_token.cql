-- description: creates accesstokens index on refresh_token
-- authoredAt: 20150409193937
-- up:

CREATE INDEX accesstokens_refresh_token ON accesstokens (refresh_token);

-- down: