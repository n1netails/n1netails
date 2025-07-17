-- TailLevel seed data
INSERT INTO tail_level (id, name, description, is_deletable) VALUES
(1, 'LOW', 'Minor tail, minimal impact.', false),
(2, 'MEDIUM', 'Moderate tail, needs attention.', false),
(3, 'HIGH', 'Major tail, immediate action required.', false),
(4, 'EXTREME', 'Extreme tail, highest priority deal with this issue first.', false);

-- TailStatus seed data
INSERT INTO tail_status (id, name, is_deletable) VALUES
(1, 'NEW', false),
(2, 'IN_PROGRESS', false),
(3, 'BLOCKED', false),
(4, 'RESOLVED', false);

-- TailType seed data
INSERT INTO tail_type (id, name, description, is_deletable) VALUES
(1, 'SYSTEM_ALERT', 'Indicates a system-level issue.', false),
(2, 'USER_REPORT', 'Reported by end users.', false),
(3, 'SCHEDULED_MAINTENANCE', 'Planned tail due to maintenance.', false),
(4, 'SECURITY_BREACH', 'Security incident or breach detected.', false),
(5, 'PERFORMANCE_ISSUE', 'Performance degradation or resource spike.', false),
(6, 'INTEGRATION_FAILURE', 'Failure in third-party integration or API.', false),
(7, 'DATA_INCONSISTENCY', 'Mismatch or error in stored data.', false),
(8, 'CONFIGURATION_CHANGE', 'System settings updated or misconfigured.', false),
(9, 'DEPLOYMENT_EVENT', 'Tail generated during deployment or release process.', false),
(10, 'MONITORING_ALERT', 'Generated from automated monitoring tools.', false),
-- Informational / Successful tails
(11, 'SUCCESSFUL_DEPLOYMENT', 'Deployment completed successfully.', false),
(12, 'USER_ACTION_COMPLETED', 'A user action was completed without issue.', false),
(13, 'AUTOMATION_SUCCESS', 'Automated task or job finished successfully.', false),
(14, 'DATA_SYNC_SUCCESS', 'Data sync across systems was successful.', false),
(15, 'BACKUP_COMPLETED', 'System backup completed successfully.', false),
(16, 'HEALTH_CHECK_PASSED', 'System health check passed with no issues.', false),
(17, 'LOGIN_SUCCESS', 'User login was successful.', false),
(18, 'PASSWORD_RESET_SUCCESS', 'Password was reset successfully.', false),
(19, 'SYSTEM_RECOVERY', 'System recovered from previous failure.', false),
(20, 'SLA_MET', 'Service level agreement goals were met.', false);

-- Create n1netails default organization
INSERT INTO ntail.organization (id, name, description, address, created_at, updated_at)
VALUES 1, 'n1netails', 'Default n1netails organization', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
