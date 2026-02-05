-- Deduplicate tail_level
UPDATE ntail.tail
SET level_id = (
    SELECT MIN(tl_inner.id)
    FROM ntail.tail_level tl_inner
    WHERE tl_inner.name = (
        SELECT tl_outer.name
        FROM ntail.tail_level tl_outer
        WHERE tl_outer.id = ntail.tail.level_id
    )
)
WHERE level_id IN (
    SELECT tl_dup.id
    FROM ntail.tail_level tl_dup
    WHERE EXISTS (
        SELECT 1
        FROM ntail.tail_level tl_orig
        WHERE tl_orig.name = tl_dup.name
        AND tl_orig.id < tl_dup.id
    )
);

DELETE FROM ntail.tail_level
WHERE id IN (
    SELECT tl_dup.id
    FROM ntail.tail_level tl_dup
    WHERE EXISTS (
        SELECT 1
        FROM ntail.tail_level tl_orig
        WHERE tl_orig.name = tl_dup.name
        AND tl_orig.id < tl_dup.id
    )
);

-- Deduplicate tail_status
UPDATE ntail.tail
SET status_id = (
    SELECT MIN(ts_inner.id)
    FROM ntail.tail_status ts_inner
    WHERE ts_inner.name = (
        SELECT ts_outer.name
        FROM ntail.tail_status ts_outer
        WHERE ts_outer.id = ntail.tail.status_id
    )
)
WHERE status_id IN (
    SELECT ts_dup.id
    FROM ntail.tail_status ts_dup
    WHERE EXISTS (
        SELECT 1
        FROM ntail.tail_status ts_orig
        WHERE ts_orig.name = ts_dup.name
        AND ts_orig.id < ts_dup.id
    )
);

DELETE FROM ntail.tail_status
WHERE id IN (
    SELECT ts_dup.id
    FROM ntail.tail_status ts_dup
    WHERE EXISTS (
        SELECT 1
        FROM ntail.tail_status ts_orig
        WHERE ts_orig.name = ts_dup.name
        AND ts_orig.id < ts_dup.id
    )
);

-- Deduplicate tail_type
-- First update tail table
UPDATE ntail.tail
SET type_id = (
    SELECT MIN(tt_inner.id)
    FROM ntail.tail_type tt_inner
    WHERE tt_inner.name = (
        SELECT tt_outer.name
        FROM ntail.tail_type tt_outer
        WHERE tt_outer.id = ntail.tail.type_id
    )
)
WHERE type_id IN (
    SELECT tt_dup.id
    FROM ntail.tail_type tt_dup
    WHERE EXISTS (
        SELECT 1
        FROM ntail.tail_type tt_orig
        WHERE tt_orig.name = tt_dup.name
        AND tt_orig.id < tt_dup.id
    )
);

-- Then update runbook_related_tail_types
UPDATE ntail.runbook_related_tail_types
SET tail_type_id = (
    SELECT MIN(tt_inner.id)
    FROM ntail.tail_type tt_inner
    WHERE tt_inner.name = (
        SELECT tt_outer.name
        FROM ntail.tail_type tt_outer
        WHERE tt_outer.id = ntail.runbook_related_tail_types.tail_type_id
    )
)
WHERE tail_type_id IN (
    SELECT tt_dup.id
    FROM ntail.tail_type tt_dup
    WHERE EXISTS (
        SELECT 1
        FROM ntail.tail_type tt_orig
        WHERE tt_orig.name = tt_dup.name
        AND tt_orig.id < tt_dup.id
    )
);

-- Deduplicate runbook_related_tail_types itself
CREATE TABLE ntail.runbook_related_tail_types_temp AS
SELECT DISTINCT runbook_id, tail_type_id FROM ntail.runbook_related_tail_types;

TRUNCATE TABLE ntail.runbook_related_tail_types;

INSERT INTO ntail.runbook_related_tail_types (runbook_id, tail_type_id)
SELECT runbook_id, tail_type_id FROM ntail.runbook_related_tail_types_temp;

DROP TABLE ntail.runbook_related_tail_types_temp;

-- Delete duplicate tail_type rows
DELETE FROM ntail.tail_type
WHERE id IN (
    SELECT tt_dup.id
    FROM ntail.tail_type tt_dup
    WHERE EXISTS (
        SELECT 1
        FROM ntail.tail_type tt_orig
        WHERE tt_orig.name = tt_dup.name
        AND tt_orig.id < tt_dup.id
    )
);

-- Add unique constraints
ALTER TABLE ntail.tail_level ADD CONSTRAINT uk_tail_level_name UNIQUE (name);
ALTER TABLE ntail.tail_status ADD CONSTRAINT uk_tail_status_name UNIQUE (name);
ALTER TABLE ntail.tail_type ADD CONSTRAINT uk_tail_type_name UNIQUE (name);
