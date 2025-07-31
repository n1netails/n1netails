CREATE TABLE IF NOT EXISTS ntail.email_notification_template
(
    id character varying(100),
    subject TEXT,
    html_body TEXT,
    CONSTRAINT email_notification_template_pkey PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS ntail.email_notification_template
    OWNER to n1netails;
