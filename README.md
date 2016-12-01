
# react-native-mona-imap

## Getting started

`$ npm install react-native-mona-imap --save`

### Mostly automatic installation

`$ react-native link react-native-mona-imap`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-mona-imap` and add `RNMonaImap.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNMonaImap.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

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

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNMonaImap.sln` in `node_modules/react-native-mona-imap/windows/RNMonaImap.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Cl.Json.RNMonaImap;` to the usings at the top of the file
  - Add `new RNMonaImapPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNMonaImap from 'react-native-mona-imap';

// TODO: What do with the module?
RNMonaImap;
```
  