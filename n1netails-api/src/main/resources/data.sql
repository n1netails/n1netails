-- TailLevel seed data
INSERT INTO tail_level (id, name, description) VALUES
(1, 'LOW', 'Minor tail, minimal impact.'),
(2, 'MEDIUM', 'Moderate tail, needs attention.'),
(3, 'HIGH', 'Major tail, immediate action required.'),
(4, 'EXTREME', 'Extreme tail, highest priority deal with this issue first.');

-- TailStatus seed data
INSERT INTO tail_status (id, name) VALUES
(1, 'NEW'),
(2, 'IN_PROGRESS'),
(3, 'BLOCKED'),
(4, 'RESOLVED');

-- TailType seed data
INSERT INTO tail_type (id, name, description) VALUES
(1, 'SYSTEM_ALERT', 'Indicates a system-level issue.'),
(2, 'USER_REPORT', 'Reported by end users.'),
(3, 'SCHEDULED_MAINTENANCE', 'Planned tail due to maintenance.'),
(4, 'SECURITY_BREACH', 'Security incident or breach detected.'),
(5, 'PERFORMANCE_ISSUE', 'Performance degradation or resource spike.'),
(6, 'INTEGRATION_FAILURE', 'Failure in third-party integration or API.'),
(7, 'DATA_INCONSISTENCY', 'Mismatch or error in stored data.'),
(8, 'CONFIGURATION_CHANGE', 'System settings updated or misconfigured.'),
(9, 'DEPLOYMENT_EVENT', 'Tail generated during deployment or release process.'),
(10, 'MONITORING_ALERT', 'Generated from automated monitoring tools.'),
-- Informational / Successful tails
(11, 'SUCCESSFUL_DEPLOYMENT', 'Deployment completed successfully.'),
(12, 'USER_ACTION_COMPLETED', 'A user action was completed without issue.'),
(13, 'AUTOMATION_SUCCESS', 'Automated task or job finished successfully.'),
(14, 'DATA_SYNC_SUCCESS', 'Data sync across systems was successful.'),
(15, 'BACKUP_COMPLETED', 'System backup completed successfully.'),
(16, 'HEALTH_CHECK_PASSED', 'System health check passed with no issues.'),
(17, 'LOGIN_SUCCESS', 'User login was successful.'),
(18, 'PASSWORD_RESET_SUCCESS', 'Password was reset successfully.'),
(19, 'SYSTEM_RECOVERY', 'System recovered from previous failure.'),
(20, 'SLA_MET', 'Service level agreement goals were met.');

-- Create n1netails default organization
INSERT INTO ntail.organization (id, name, description, address, created_at, updated_at)
VALUES 1, 'n1netails', 'Default n1netails organization', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
