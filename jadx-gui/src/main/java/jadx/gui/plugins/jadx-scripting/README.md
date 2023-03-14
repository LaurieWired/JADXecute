# JADX Scripting

## Notes

### Template
You must implement the userCodeMain(MainWindow mainWindow) class as this will be the entrypoint to the program. Anything to be printed to the JADX Scripting console can be appended to the return string.

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

Find many more examples inside jadx.gui.JadxWrapper or in the JADX API classes! Here is a list of public methods that are potentially useful inside JadxWrapper class:
- List<JavaClass> getClasses() : Get the complete list of classes
- List<JavaClass> getIncludedClasses() : Get all classes that are not excluded by the excluded packages settings
- List<JavaClass> getIncludedClassesWithInners() : Get all classes that are not excluded by the excluded packages settings including inner classes

Use the following code as an example for initializing and using the JadxWrapper class:
```
import jadx.gui.ui.MainWindow;
import jadx.gui.JadxWrapper;

public class UserCodeClass {
    public static String userCodeMain(MainWindow mainWindow) {
        String jadxScriptingOutput = "Example output...";

        // FIXME
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

### Replace a line of Java code
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
        String searchClassName = "mw_StringEncodeClass"; // Update this
        
        // Find desired class
        RootNode root = mainWindow.getWrapper().getDecompiler().getRoot();
        List<ClassNode> classes = root.getClasses();
        for (ClassNode cls : classes) {
            if (cls.getFullName().contains(searchClassName)) {
                RenameObjectHelper renameObjectHelper = new RenameObjectHelper();
                
                jadxScriptingOutput += "Found class " + searchClassName + ":\n\n";
                
                String classCode = cls.getJavaNode().getCodeInfo().toString();
				jadxScriptingOutput += classCode;
                //classCode = classCode.replaceAll("mw_decodeString\\(\"xbtmarfenhgfY2Y5MDE3OTcyMQ==\"\\)", "\"encoded string value\"");
                
                //cls.getJavaNode().setCode(classCode);
                
                jadxScriptingOutput += "Replaced string";
                return jadxScriptingOutput;
            }
        }

        return jadxScriptingOutput;
    }
}


### Insert a new comment
