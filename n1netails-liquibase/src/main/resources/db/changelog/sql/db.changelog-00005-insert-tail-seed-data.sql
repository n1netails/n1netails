-- TailLevel seed data
INSERT INTO ntail.tail_level (id, name, description, is_deletable) VALUES
(1, 'INFO', 'Info tail, minimal impact.', false),
(2, 'SUCCESS', 'Success tail, notify on successful actions.', false),
(3, 'WARN', 'Warning tail, indicates a non-breaking issue immediate action not required.', false),
(4, 'ERROR', 'Error tail, immediate action required.', false),
(5, 'CRITICAL', 'Critical tail, highest priority deal with this issue first.', false);

-- TailStatus seed data
INSERT INTO ntail.tail_status (id, name, is_deletable) VALUES
(1, 'NEW', false),
(2, 'IN_PROGRESS', false),
(3, 'BLOCKED', false),
(4, 'RESOLVED', false);

-- TailType seed data
INSERT INTO ntail.tail_type (id, name, description, is_deletable) VALUES
(1, 'SYSTEM_ALERT', 'Indicates a system-level issue.', false),
(2, 'USER_REPORT', 'Reported by end users.', false),
(3, 'SCHEDULED_MAINTENANCE', 'Planned tail due to maintenance.', true),
(4, 'SECURITY_BREACH', 'Security incident or breach detected.', true),
(5, 'PERFORMANCE_ISSUE', 'Performance degradation or resource spike.', true),
(6, 'INTEGRATION_FAILURE', 'Failure in third-party integration or API.', true),
(7, 'DATA_INCONSISTENCY', 'Mismatch or error in stored data.', true),
(8, 'CONFIGURATION_CHANGE', 'System settings updated or misconfigured.', true),
(9, 'DEPLOYMENT_EVENT', 'Tail generated during deployment or release process.', true),
(10, 'MONITORING_ALERT', 'Generated from automated monitoring tools.', true),
-- Informational / Successful tails
(11, 'SUCCESSFUL_DEPLOYMENT', 'Deployment completed successfully.', true),
(12, 'USER_ACTION_COMPLETED', 'A user action was completed without issue.', true),
(13, 'AUTOMATION_SUCCESS', 'Automated task or job finished successfully.', true),
(14, 'DATA_SYNC_SUCCESS', 'Data sync across systems was successful.', true),
(15, 'BACKUP_COMPLETED', 'System backup completed successfully.', true),
(16, 'HEALTH_CHECK_PASSED', 'System health check passed with no issues.', true),
(17, 'LOGIN_SUCCESS', 'User login was successful.', true),
(18, 'PASSWORD_RESET_SUCCESS', 'Password was reset successfully.', true),
(19, 'SYSTEM_RECOVERY', 'System recovered from previous failure.', true),
(20, 'SLA_MET', 'Service level agreement goals were met.', true);