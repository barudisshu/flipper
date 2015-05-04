-- description: creates accesstokens index on token
-- authoredAt: 20150405083709
-- up:

CREATE INDEX accesstokens_access_token ON accesstokens (access_token);

-- down: