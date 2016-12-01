
# react-native-mona-imap

## Getting started

`$ npm install react-native-mona-imap --save`

### Mostly automatic installation (Recommend)

`$ react-native link react-native-mona-imap`

### Manual installation


#### iOS

Will be supported soon.

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNMonaImapPackage;` to the imports at the top of the file
  - Add `new RNMonaImapPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-mona-imap'
  	project(':react-native-mona-imap').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-mona-imap/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-mona-imap')
  	```


## Usage
##### ES6 grammer, Generator and async function are used in this demo

```javascript
import RNMonaImap from 'react-native-mona-imap'

//You should call RNMonaImap.initEmailAccount first before take other operation
const function * initEmailAccount() {
  //init imap connection and create a folder
  //at present, brand of mail provider "126.com", "163.com", "gmail.com" are supported, I'll add more soon.

  const init = yield RNMonaImap.initEmailAccount({brand: '126.com', usermail: 'youremail@126.com', password: 'youpassword', folderName: 'imapFolderName'})

  if(init === true) {
    //means init imap connection successfully
  } else {
    //error message
    console.info(init)
  }

}

const function * getMessages() {
  //get all messages in folder that named "imapFolderName" above
  //you can use `console.info(messages)`
  const messages = yield RNMonaImap.getMessages()

  return messages
}

const async function sendMessage() {
  const message = {
    subject: 'a subject',
    content: 'a plain text content encoded in utf8',
    messageId: '' // sometimes, you want to update a message, then take the old message's messageId, or let it be an empty string
  }
  const resp = await RNMonaImap.sendMessage(message)
  resp === true ? 'Successfully Saved' : 'Save Failed'
}

const * deleteMessage(messageId) {
  const resp = yield RNMonaImap.deleteMessage(messageId)
}
```
