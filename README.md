# JADX Scripting
Plugin for [JADX decompiler](https://github.com/skylot/jadx) to add an option for dynamically running Java code to modify or print components of the jadx-gui output. This feature is inspired by IDAPython to help Android Reverse Engineers in analyzing their APKs in a more automated way. More features to come!

## Getting Started
Once you've opened the plugin, enter your code into the "Java Input" area. Your code must follow the same style as the examples used below or else jadx-scripting will not be able to handle the code call. The following are a few important guidelines to follow (Note: all of these are demonstrated in the template and examples shown below):
- Name your class "UserCodeClass"
- Implement the userCodeMain(MainWindow mainWindow) class
    - This will be the entrypoint to the program
- Have "userCodeMain" return a string
    - This return value will be the output of the console if the program ran successfully
    - Feel free to append any debug strings you wish
    
Once you've written your code, hit run and you should either see successful console output or the list of code compilation errors that Java encountered when compiling your jadx-scripting code.

### Simple Template
If you are already comfortable with the JADX API, simply start with the following template and add code as you normally would to call the JADX API methods.

```
import jadx.gui.ui.MainWindow;

public class UserCodeClass {
    public static String userCodeMain(MainWindow mainWindow) {
        String jadxScriptingOutput = "";
        
        // Your code goes here!

        return jadxScriptingOutput;
    }
}
```

## Code Examples

Find many more examples inside jadx.gui.JadxWrapper or in the JADX API classes! Use the following code as an example for initializing and using the JadxWrapper class:
```
import jadx.gui.ui.MainWindow;
import jadx.gui.JadxWrapper;
import jadx.api.JavaClass;

public class UserCodeClass {
    public static String userCodeMain(MainWindow mainWindow) {
        String jadxScriptingOutput = "Example output...";

        JadxWrapper wrapper = mainWindow.getWrapper();
        
        // Here's an example of using the wrapper object. Update here!
        for (JavaClass cls : wrapper.getClasses()) {
            jadxScriptingOutput += cls.getName() + "\n";
        }

        return jadxScriptingOutput;
    }
}
```

Additionally, the following includes some new examples for common needs.

### Print all class names
```
import jadx.gui.ui.MainWindow;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.RootNode;
import java.util.List;

public class UserCodeClass {
    public static String userCodeMain(MainWindow mainWindow) {
        String jadxScriptingOutput = "";
        
        // Add all strings to the jadx scripting output to be printed
        RootNode root = mainWindow.getWrapper().getDecompiler().getRoot();
        List<ClassNode> classes = root.getClasses();
        for (ClassNode cls : classes) {
            jadxScriptingOutput += cls.getFullName() + "\n";
        }

        return jadxScriptingOutput;
    }
}
```

### Print all method names in a class
```
import jadx.gui.ui.MainWindow;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.dex.nodes.RootNode;
import java.util.List;

public class UserCodeClass {
    public static String userCodeMain(MainWindow mainWindow) {
        String jadxScriptingOutput = "";
        ClassNode selectedClassNode = null;
        String searchClassName = "exampleClassName"; // Update this
        
        // Add all strings to the jadx scripting output to be printed
        RootNode root = mainWindow.getWrapper().getDecompiler().getRoot();
        List<ClassNode> classes = root.getClasses();
        for (ClassNode cls : classes) {
            if (cls.getFullName().contains(searchClassName)) {
                jadxScriptingOutput += "Found class: " + cls.getFullName() + "\n";
                selectedClassNode = cls;
            }
        }
        
        if (selectedClassNode != null) {
            jadxScriptingOutput += "Methods:\n";
            
            // Print all methods in the selected class
            for (MethodNode method : selectedClassNode.getMethods()) {
                jadxScriptingOutput += method.getAlias() + "\n"; // Use the alias since this includes user updates
            }
        } else {
            jadxScriptingOutput += "Could not find class " + searchClassName;
        }

        return jadxScriptingOutput;
    }
}
```

### Print all field names in a class
```
import jadx.gui.ui.MainWindow;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.FieldNode;
import jadx.core.dex.nodes.RootNode;
import java.util.List;

public class UserCodeClass {
    public static String userCodeMain(MainWindow mainWindow) {
        String jadxScriptingOutput = "";
        ClassNode selectedClassNode = null;
        String searchClassName = "exampleClassName"; // Update this
        
        // Add all strings to the jadx scripting output to be printed
        RootNode root = mainWindow.getWrapper().getDecompiler().getRoot();
        List<ClassNode> classes = root.getClasses();
        for (ClassNode cls : classes) {
            if (cls.getFullName().contains(searchClassName)) {
                jadxScriptingOutput += "Found class: " + cls.getFullName() + "\n";
                selectedClassNode = cls;
            }
        }
        
        if (selectedClassNode != null) {
            jadxScriptingOutput += "Fields:\n";
            
            // Print all field in the selected class
            for (FieldNode field : selectedClassNode.getFields()) {
                jadxScriptingOutput += field.getAlias() + "\n"; // Use the alias since this includes user updates
            }
        } else {
            jadxScriptingOutput += "Could not find class " + searchClassName;
        }

        return jadxScriptingOutput;
    }
}
```

### Print all classes that inherit from a particular class
```
import jadx.gui.ui.MainWindow;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.RootNode;
import java.util.List;
import jadx.core.dex.instructions.args.ArgType;

public class UserCodeClass {
    public static String userCodeMain(MainWindow mainWindow) {
        String jadxScriptingOutput = "";
        String searchClassName = "IntentService"; // Update this
        ArgType superClassType = null;
        
        // Add all strings to the jadx scripting output to be printed
        RootNode root = mainWindow.getWrapper().getDecompiler().getRoot();
        List<ClassNode> classes = root.getClasses();
        
        jadxScriptingOutput += "Classes extending " + searchClassName + ":\n";
        
        for (ClassNode cls : classes) {     
            superClassType = cls.getSuperClass();
            
            if (superClassType != null && superClassType.toString().contains(searchClassName)) {
                jadxScriptingOutput += cls.getFullName() + "\n";
            }
        }

        return jadxScriptingOutput;
    }
}
```

### Print all class names containing a substring
```
import jadx.gui.ui.MainWindow;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.RootNode;
import java.util.List;

public class UserCodeClass {
    public static String userCodeMain(MainWindow mainWindow) {
        String jadxScriptingOutput = "";
        String searchClassName = "exampleClassName"; // Update this
        
        // Loop through all classes and add desired name to return output
        RootNode root = mainWindow.getWrapper().getDecompiler().getRoot();
        List<ClassNode> classes = root.getClasses();
        for (ClassNode cls : classes) {

            // Example: finds all user-renamed classes renamed like "mw_MyClassA"
            if (cls.getFullName().contains(searchClassName)) {
                jadxScriptingOutput += cls.getFullName() + "\n";
            }
        }

        return jadxScriptingOutput;
    }
}
```

### Rename a class
```
import jadx.gui.ui.MainWindow;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.RootNode;
import java.util.List;
import jadx.gui.treemodel.JClass;
import jadx.api.JavaClass;
import jadx.gui.plugins.jadxscripting.RenameObjectHelper;

public class UserCodeClass {
    public static String userCodeMain(MainWindow mainWindow) {
        String jadxScriptingOutput = "";
        String searchClassName = "exampleClassName"; // Update this
        
        // Find desired class
        RootNode root = mainWindow.getWrapper().getDecompiler().getRoot();
        List<ClassNode> classes = root.getClasses();
        for (ClassNode cls : classes) {
            if (cls.getFullName().contains(searchClassName)) {
                RenameObjectHelper renameObjectHelper = new RenameObjectHelper();
                JClass jclass = new JClass(cls.getJavaNode());

                // Rename found class to desired name
                jadxScriptingOutput += renameObjectHelper.renameObject(mainWindow, jclass, "newClassName");
                
                // Optionally return here or you could add functionality to change all 
                //  matched objects to different names and return out of the loop
                return jadxScriptingOutput;
            }
        }

        return jadxScriptingOutput;
    }
}
```

### Print Java code in a class
```
import jadx.gui.ui.MainWindow;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.RootNode;
import java.util.List;
import jadx.gui.treemodel.JClass;
import jadx.api.JavaClass;
import jadx.gui.plugins.jadxscripting.RenameObjectHelper;

public class UserCodeClass {
    public static String userCodeMain(MainWindow mainWindow) {
        String jadxScriptingOutput = "";
        String searchClassName = "exampleClassName"; // Update this
        
        // Find desired class
        RootNode root = mainWindow.getWrapper().getDecompiler().getRoot();
        List<ClassNode> classes = root.getClasses();
        for (ClassNode cls : classes) {
            if (cls.getFullName().contains(searchClassName)) {
                RenameObjectHelper renameObjectHelper = new RenameObjectHelper();
                
                jadxScriptingOutput += "Found class " + searchClassName + ":\n\n";
                jadxScriptingOutput += cls.getJavaNode().getCodeInfo().toString();
                
                return jadxScriptingOutput;
            }
        }

        return jadxScriptingOutput;
    }
}
```

### Insert a new comment for an instruction
Note: This is based on the smali index of the instruction, but will appear in the Java code. The smali index for the method starts from 0 as the first instruction.
```
import jadx.gui.ui.MainWindow;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.dex.nodes.RootNode;
import java.util.List;
import jadx.gui.plugins.jadxscripting.AddCommentHelper;

public class UserCodeClass {
    public static String userCodeMain(MainWindow mainWindow) {
        String jadxScriptingOutput = "";
        ClassNode selectedClassNode = null;
        MethodNode selectedMethodNode = null;
        String searchClassName = "exampleClassName"; // Update this
        String searchMethodName = "exampleMethodName"; // Update this
        String commentToAdd = "This is a new comment!"; // Update this
        int smaliInstructionIndex = 0; // Update this
        
        // Add all strings to the jadx scripting output to be printed
        RootNode root = mainWindow.getWrapper().getDecompiler().getRoot();
        List<ClassNode> classes = root.getClasses();
        for (ClassNode cls : classes) {
            if (cls.getFullName().contains(searchClassName)) {
                jadxScriptingOutput += "Found class: " + cls.getFullName() + "\n";
                selectedClassNode = cls;
            }
        }
        
        if (selectedClassNode != null) {
            for (MethodNode method : selectedClassNode.getMethods()) {
                if (method.getAlias().(searchMethodName)) {
                    jadxScriptingOutput += "Found method: " + method.getAlias() + "\n";
                    selectedMethodNode = method;
                }
            }
            
            // Add the comment if the method was found
            AddCommentHelper addCommentHelper = new AddCommentHelper(mainWindow, selectedMethodNode.getJavaNode());
            jadxScriptingOutput += addCommentHelper.addInstructionComment(commentToAdd, smaliInstructionIndex);
            
        } else {
            jadxScriptingOutput += "Could not find class " + searchClassName;
        }

        return jadxScriptingOutput;
    }
}
```

