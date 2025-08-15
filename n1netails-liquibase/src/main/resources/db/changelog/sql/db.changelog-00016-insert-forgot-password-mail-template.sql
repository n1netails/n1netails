
INSERT INTO ntail.email_notification_template (id, name, subject, html_body)
VALUES (
  3,
  'forgot_password_reset',
  '🦊 N1netails Account Password Reset',
  $$
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Reset Password Request</title>
  </head>
  <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4">
    <table width="100%" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" style="padding: 40px 0;">
          <table width="600" cellpadding="0" cellspacing="0" style="background: #ffffff; border-radius: 8px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);">
            <tr>
              <td style="padding: 40px 40px 20px;">
                <h2 style="margin: 0; font-size: 24px; color: #222222;">
                  <span style="color: #F06D0F;">N1netails</span> Account Password Reset
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
                    Thanks for requesting a password reset. To create a new password, just use the button below to be redirected to the password reset link.
                    Remember the link will be expired in <strong>2 days</strong>.
                </p>
              </td>
            </tr>
            <tr>
            <td style="padding: 0 40px 20px; text-align: center;">
                <a href="{{resetPasswordLink}}"
                style="display: inline-block;
                        background-color: #F06D0F;
                        color: white;
                        padding: 10px 20px;
                        text-decoration: none;
                        border-radius: 15px;
                        font-family: Arial, sans-serif;
                        font-size: 16px;">
                Reset Password
                </a>
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
              <td style="padding: 0 40px 40px;">
                <p style="margin: 0; font-size: 12px; color: #999999;">
                  This email was sent to you because you recently request a password reset. If you didn’t make this request, you can ignore this email and carry on as usual
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