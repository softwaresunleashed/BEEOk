package com.unleashed.android.beeokunleashed.sendemail;

import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.security.auth.Subject;

/**
 * Created by gupta on 7/22/2015.
 */
public class Mail extends javax.mail.Authenticator {
    private String _user;
    private String _pass;

    private String[] _to;
    private String _from;

    private String _port;
    private String _sport;

    private String _host;

    private String _subject;
    private String _body;

    private boolean _auth;

    private boolean _debuggable;

    private Multipart _multipart;


    public Mail(String User, String PassWord, String Subject, String EmailBody, String SenderFrom, String[] RecipientsTo) {
        _host = "smtp.gmail.com"; // default smtp server
        _port = "465"; // default smtp port
        _sport = "465"; // default socketfactory port

        _user = User; // username
        _pass = PassWord; // password
        _from = SenderFrom; // email sent from

        _subject = Subject; // email subject
        _body = EmailBody; // email body
        _to = RecipientsTo;//new String[]{"softwares.unleashed@gmail.com", RecipientsTo};

        _debuggable = false; // debug mode on or off - default off
        _auth = true; // smtp authentication - default on

        _multipart = new MimeMultipart();

        // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added.
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

//    public Mail(String user, String pass, String subject, String emailBody, String senderFrom, String recipientsTo) {
//    }

//    public Mail(String user, String pass) {
//        this();
//
//        _user = user;
//        _pass = pass;
//    }



    public boolean send() {

            Properties props = _setProperties();

            if(!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") && !_subject.equals("") && !_body.equals("")) {
                Session session = Session.getInstance(props, this);

                try{


                    MimeMessage msg = new MimeMessage(session);

                    msg.setFrom(new InternetAddress(_from));
                    msg.setSubject(_subject);
                    msg.setSentDate(new Date());


                    // setup message body
                    BodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setText(_body);
                    _multipart.addBodyPart(messageBodyPart);

                    // Put parts in message
                    msg.setContent(_multipart);

                    InternetAddress addressTo;// = new InternetAddress[_to.length];
                    for (int i = 0; i < _to.length; i++) {
                        addressTo = new InternetAddress(_to[i]);
                        msg.setRecipient(MimeMessage.RecipientType.TO, addressTo);

                        // send email
                        Transport.send(msg);


                    }

                }catch (Exception ex){
                        Log.e("Bulk SMS: ", "Mail.java:send() caught exception");
						ex.printStackTrace();
                }


                return true;

            } else {
                return false;
            }



    }

    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);

        _multipart.addBodyPart(messageBodyPart);
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
    }

    private Properties _setProperties() {
        Properties props = new Properties();

        props.put("mail.smtp.host", _host);

        if(_debuggable) {
            props.put("mail.debug", "true");
        }

        if(_auth) {
            props.put("mail.smtp.auth", "true");
        }

        props.put("mail.smtp.port", _port);
        props.put("mail.smtp.socketFactory.port", _sport);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        return props;
    }

    // the getters and setters
    public String getBody() {
        return _body;
    }

    public void setBody(String _body) {
        this._body = _body;
    }

    // more of the getters and setters …..
}
