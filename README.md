![JADXecute logo](https://user-images.githubusercontent.com/123765654/226205850-ef2e6e68-ae65-41ee-b4e9-23bb82aac52b.png)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Follow @lauriewired](https://img.shields.io/twitter/follow/lauriewired?style=social)](https://twitter.com/lauriewired)
# Description

**JADXecute** is a plugin for **[JADX](https://github.com/skylot/jadx)** that enhances its functionality by adding **Dynamic Code Execution** abilities.

With **JADXecute**, you can dynamically run Java code to modify or print components of the jadx-gui output. **JADXecute** is inspired by **IDAPython** to help Android Reverse Engineers in analyzing APKs in a more automated way.


https://user-images.githubusercontent.com/123765654/226236645-e2b05f49-6c5d-4ee0-a42a-69f324e880d8.mp4



# Installation

This code is based on the latest release of JADX version 1.4.6. It contains an additional plugin to enable dynamic Java coding using all of the standard Java libraries as well as the JADX libs and APIs. simply download this release of JADX with the embedded plugin and run as you normally would! To run JADXecute, click the coffee cup in the plugins section at the top of JADX or select "Tools -> JADXecute".


# Usage

Once you've opened the plugin, enter your code into the "Java Input" area. Your code must follow the same style as the examples used below or else jadx-scripting will not be able to handle the code call. The following are a few important guidelines to follow (Note: all of these are demonstrated in the template and examples shown below):
- Name your class "UserCodeClass"
- Implement the userCodeMain(MainWindow mainWindow) class
    - This will be the entrypoint to the program
- Have "userCodeMain" return a string
    - This return value will be the output of the console if the program ran successfully
    - Feel free to append any debug strings you wish
    
Once you've written your code, hit run and you should either see successful console output or the list of code compilation errors that Java encountered when compiling your jadx-scripting code.

Optionally, you can also select from the many templates to get you started or even input your own Java file as long as it follows the guidelines from above.

# Contribute
- Make a pull request
- Add an Example Snippet to our Wiki
- Report an error/issue
- Suggest an improvement
- Share with others or give a star!

# Screenshots

### Basic JADXecute view
![jadxecutewithicon](https://user-images.githubusercontent.com/123765654/226281090-c38da099-9b47-4918-ade0-015ec0acc7d2.png)

### Printing all classes inheriting from a class
![inheritingexample](https://user-images.githubusercontent.com/123765654/226281266-7a62bbaa-3d22-412f-ae50-3ab2500f7a19.png)

### Syntax errors
![syntaxerror](https://user-images.githubusercontent.com/123765654/226281371-aac333f5-acb7-4b41-a4b5-2efaaa05b144.png)
