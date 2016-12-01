
package com.reactlibrary;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.io.IOException;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import mona.mail.Imap;
import mona.mail.MailFactory;
import mona.mail.MailProviders;

public class RNMonaImapModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  private String brand;
  private String usermail;
  private String password;
  private String folderName;

  private MailFactory getMailFactory() {
    MailFactory factory = MailFactory.getInstance(MailProviders.get(brand), usermail, password);
    return factory;
  }

  public RNMonaImapModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public boolean canOverrideExistingModule() {
    return true;
  }

  @Override
  public String getName() {
    return "RNMonaImap";
  }

  @ReactMethod
  public void initEmailAccount(ReadableMap args, Promise promise) {
    String brand = args.getString("brand");
    String usermail = args.getString("usermail");
    String password = args.getString("password");
    String folderName = args.getString("folderName");

    try {
      MailFactory factory = MailFactory.getInstance(MailProviders.get(brand), usermail, password);
      Imap imap = new Imap(factory.getSession());
      imap.createFolder(folderName);
      imap.selectFolder(folderName, Folder.READ_WRITE);

      this.brand = brand;
      this.usermail = usermail;
      this.password = password;
      this.folderName = folderName;

      promise.resolve(true);
    } catch (Exception e) {
      e.printStackTrace();
      promise.reject("INIT_EMAIL_ACCOUNT_FAIL", e);
    }
  }

  @ReactMethod
  public void getMessages(Promise promise) {
    try {
      MailFactory mailFactory = getMailFactory();
      Imap imap = new Imap(mailFactory.getSession());
      imap.selectFolder(folderName, Folder.READ_ONLY);
      Message[] messages = imap.getSelectedFolder().getMessages();

      WritableArray res = Arguments.createArray();
      for(Message message : messages) {
        MimeMessage m = (MimeMessage)message;

        WritableMap map = Arguments.createMap();
        map.putString("messageId", m.getMessageID());
        map.putString("subject", m.getSubject());
        map.putString("content", m.getContent().toString());

        res.pushMap(map);
      }
      promise.resolve(res);
    } catch (MessagingException e) {
      e.printStackTrace();
      promise.reject("GET_MESSAGES_FAIL", e);
    } catch (IOException e) {
      e.printStackTrace();
      promise.reject("GET_MESSAGES_FAIL", e);
    }
  }

  @ReactMethod
  public void sendMessage(ReadableMap args, Promise promise) {
    try {
      MailFactory mailFactory = getMailFactory();
      Imap imap = new Imap(mailFactory.getSession());
      imap.selectFolder(this.folderName, Folder.READ_WRITE);

      String subject = args.getString("subject");
      String content = args.getString("content");
      String messageId = args.getString("messageId");
      imap.appendMessage(subject, content, usermail);

      deleteMessageById(imap, messageId);
      promise.resolve(true);
    } catch (MessagingException e) {
      e.printStackTrace();
      promise.reject("SEND_MESSAGE_FAIL", e);
    }
  }

  @ReactMethod
  public void deleteMessage(String messageId, Promise promise) {
    MailFactory mailFactory = getMailFactory();
    Imap imap = new Imap(mailFactory.getSession());
    try {
      imap.selectFolder(this.folderName, Folder.READ_WRITE);

      deleteMessageById(imap, messageId);
      promise.resolve(true);
    } catch (MessagingException e) {
      e.printStackTrace();
      promise.reject("DELETE_MESSAGE_FAIL", e);
    }
  }

  private void deleteMessageById(Imap imap, String messageId) throws MessagingException {
    if(messageId == null || messageId.trim().length() == 0) {
      return;
    }
    Message[] messages = imap.getSelectedFolder().getMessages();
    for(Message message : messages) {
      MimeMessage m = (MimeMessage)message;
      if(messageId.equals(m.getMessageID())) {
        imap.deleteMessage(new Message[]{message});
      }
    }
  }
}
