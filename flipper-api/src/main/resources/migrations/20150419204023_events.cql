-- description: creates events table
-- authoredAt: 20150419204023
-- up:
	
CREATE TABLE events (
	id timeuuid,
	schema_id uuid,
	bucket_id uuid,
	collected_at timestamp,
	timestamp timestamp,
	date text,
	fields blob,
	
	PRIMARY KEY((bucket_id,date),id)  
) with clustering order by (id desc);

-- down: