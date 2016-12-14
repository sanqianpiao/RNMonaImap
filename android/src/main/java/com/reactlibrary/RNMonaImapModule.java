
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
  public void initEmailAccount(ReadableMap args, final Promise promise) {
    final String _brand = args.getString("brand");
    final String _usermail = args.getString("usermail");
    final String _password = args.getString("password");
    final String _folderName = args.getString("folderName");

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          MailFactory factory = MailFactory.getInstance(MailProviders.get(_brand), _usermail, _password);
          Imap imap = new Imap(factory.getSession());
          imap.createFolder(_folderName);
          imap.selectFolder(_folderName, Folder.READ_WRITE);

          brand = _brand;
          usermail = _usermail;
          password = _password;
          folderName = _folderName;

          promise.resolve(true);
        } catch (Exception e) {
          e.printStackTrace();
          promise.reject("INIT_EMAIL_ACCOUNT_FAIL", e);
        }
      }
    }).start();
  }

  @ReactMethod
  public void getMessages(final Promise promise) {
    new Thread(new Runnable() {
      @Override
      public void run() {
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
    }).start();

  }

  @ReactMethod
  public void sendMessage(final ReadableMap args, final Promise promise) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          MailFactory mailFactory = getMailFactory();
          Imap imap = new Imap(mailFactory.getSession());
          imap.selectFolder(folderName, Folder.READ_WRITE);

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
    }).start();
  }

  @ReactMethod
  public void deleteMessage(final String messageId, final Promise promise) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        MailFactory mailFactory = getMailFactory();
        Imap imap = new Imap(mailFactory.getSession());
        try {
          imap.selectFolder(folderName, Folder.READ_WRITE);

          deleteMessageById(imap, messageId);
          promise.resolve(true);
        } catch (MessagingException e) {
          e.printStackTrace();
          promise.reject("DELETE_MESSAGE_FAIL", e);
        }
      }
    }).start();

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
