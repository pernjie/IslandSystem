/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import javax.mail.*;

/**
 *
 * @author nataliegoh
 */
public class SMTPAuthenticator extends javax.mail.Authenticator
{
// Replace with your actual unix id
private static final String SMTP_AUTH_USER = "a0101309";
// Replace with your actual unix password
private static final String SMTP_AUTH_PWD = "Hd*787nc";
public SMTPAuthenticator()
{
}
@Override
public PasswordAuthentication getPasswordAuthentication()
{
String username = SMTP_AUTH_USER;
String password = SMTP_AUTH_PWD;
return new PasswordAuthentication(username, password);
}
}