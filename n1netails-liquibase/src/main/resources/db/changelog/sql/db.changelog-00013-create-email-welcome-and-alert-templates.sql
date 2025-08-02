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
