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

-- email template welcome
INSERT INTO ntail.email_notification_template (id, name, subject, html_body)
VALUES (
  1,
  'welcome',
  '🦊 Welcome to N1netails {{username}}',
  $$<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Welcome Email</title>
  </head>
  <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4">
    <table width="100%" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" style="padding: 40px 0;">
          <table width="600" cellpadding="0" cellspacing="0" style="background: #ffffff; border-radius: 8px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);">
            <tr>
              <td style="padding: 40px 40px 20px;">
                <h2 style="margin: 0; font-size: 24px; color: #222222;">
                  Welcome to <span style="color: #F06D0F;">N1netails</span>!
                </h2>
              </td>
            </tr>
            <tr>
              <td style="padding: 0 40px 20px;">
                <p style="margin: 0; font-size: 16px; color: #222222;">
                  Hi <strong>{{username}}</strong>,
                </p>
              </td>
            </tr>
            <tr>
              <td style="padding: 0 40px 20px;">
                <p style="margin: 0; font-size: 16px; color: #444444; line-height: 1.5;">
                  We're thrilled to have you join our community! Your account has been successfully created and you're now ready to explore everything N1netails has to offer.
                </p>
              </td>
            </tr>
            <tr>
              <td style="padding: 0 40px 20px;">
                <p style="margin: 0; font-size: 16px; color: #444444; line-height: 1.5;">
                  If you have any questions or need assistance, don't hesitate to reach out to our support team. <a href="mailto:{{n1netailsEmail}}" style="color: #999;">{{n1netailsEmail}}</a>. We're here to help make your N1netails experience amazing!
                </p>
              </td>
            </tr>
            <tr>
              <td style="padding: 20px 40px 10px;">
                <hr style="border: none; border-top: 1px solid #e0e0e0;" />
              </td>
            </tr>
            <tr>
              <td style="padding: 0 40px 20px;">
                <p style="margin: 0; font-size: 14px; color: #444444;">
                  Welcome aboard, {{username}}! 🎉<br />
                  <strong>The N1netails Team</strong>
                </p>
              </td>
            </tr>
            <tr>
              <td style="padding: 0 40px 40px;">
                <p style="margin: 0; font-size: 12px; color: #999999;">
                  This email was sent to you because you recently created an account on N1netails.
                </p>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </body>
</html>
$$
);

-- email template alert
INSERT INTO ntail.email_notification_template (id, name, subject, html_body)
VALUES (
  2,
  'alert',
  '🚨 New N1netails {{tailLevel}} Alert Received',
  $$<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>N1netails Alert</title>
  </head>
  <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
    <table width="100%" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" style="padding: 40px 0;">
          <table width="600" cellpadding="0" cellspacing="0" style="background: #ffffff; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">

            <!-- Header -->
            <tr>
              <td style="padding: 40px 40px 20px; text-align: center;">
                <h2 style="margin: 0; font-size: 26px; color: #222;">
                  🚨 New <span style="color: #F06D0F;">N1netails</span> Alert!
                </h2>
              </td>
            </tr>

            <!-- Greeting -->
            <tr>
              <td style="padding: 0 40px 20px;">
                <p style="margin: 0; font-size: 16px; color: #333;">
                  Hi <strong>{{username}}</strong>,
                </p>
              </td>
            </tr>

            <!-- Alert Body -->
            <tr>
              <td style="padding: 0 40px 10px;">
                <p style="font-size: 16px; color: #444; line-height: 1.5;">
                  A new <strong>{{tailLevel}}</strong> alert has been triggered. Please review the details below:
                </p>
              </td>
            </tr>

            <!-- Alert Details -->
            <tr>
              <td style="padding: 0 40px;">
                <p style="font-size: 15px; color: #222;"><strong>🔖 Title:</strong> {{tailTitle}}</p>
                <p style="font-size: 15px; color: #222;"><strong>📝 Description:</strong> {{tailDescription}}</p>
              </td>
            </tr>

            <!-- CTA Button -->
            <tr>
              <td style="padding: 20px 40px; text-align: center;">
                <a href="{{n1netailsUi}}" style="display: inline-block; background-color: #F06D0F; color: #fff; text-decoration: none; padding: 12px 20px; border-radius: 6px; font-size: 16px;">
                  View on N1netails Dashboard
                </a>
              </td>
            </tr>

            <!-- Divider -->
            <tr>
              <td style="padding: 20px 40px 10px;">
                <hr style="border: none; border-top: 1px solid #e0e0e0;" />
              </td>
            </tr>

            <!-- Footer -->
            <tr>
              <td style="padding: 0 40px 40px;">
                <p style="font-size: 12px; color: #999; line-height: 1.5;">
                  🦊 If you have any questions or need assistance, feel free to reach out to our support team at <a href="mailto:{{n1netailsEmail}}" style="color: #999;">{{n1netailsEmail}}</a>.
                </p>
              </td>
            </tr>

          </table>
        </td>
      </tr>
    </table>
  </body>
</html>
$$
);
