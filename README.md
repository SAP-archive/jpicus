![](https://img.shields.io/badge/STATUS-NOT%20CURRENTLY%20MAINTAINED-red.svg?longCache=true&style=flat)

# Important Notice
We have decided to stop the maintenance of this public GitHub repository.

JPicus
======

Overview
--------

*JPicus* is an I/O analysis tool that helps you analyze the I/O operations of your Java application. It addresses problems such as leaking file handles (the "Too many open files" exception) and I/O performance. In order to analyze a Java process, you attach the JPicus agent which will then collect data about the I/O operations. At any point in time you can connect to the agent and take a snapshot of this data. Depending on the agent startup options that you provided, you will find information in the snapshot, such as:

* File open/close operations - shows the exact stack trace and point in time when a file was opened and closed.
* Failed/Successful delete operations - same for the delete operations
* I/O operations - shows the type of I/O objet (e.g FileInputStream, FileChannel etc), the amount of data transfered, the time it took, the size of the buffer etc



Building
--------
JPicus uses Apache Ant as a build tool. Its default target *dist* produces two artifacts:

 * *jpicus.jar* - a javaagent that you have to attach to the java process to be analyzed
 * *com.sap.tools.jpicus.client.jar* - a client API that can be used to connect to and control the running agent

Usage
-----
1. Enable the agent.
>   java -javaagent:/home/pavel/jpicus.jar -jar myapp.jar

2. Connect to the agent, take a snapshot and analyze it:

```
Connection con  = JPicus.connect(host);
Snapshot s = con.getSnapshot();

Map<String, Set<HandleDescriptor>> handles = s.getHandles();
System.out.println(handles);

Map<String, Set<DeleteOperation>> deleteOperations = s.getDeleteOperations();
System.out.println(deleteOperations);
```


GUI
---
JPicus does not have a GUI yet. The closed source version of JPicus had an Eclipse based UI, but decided to drop it in favor of a web based UI. We believe that this will make JPicus easier to deploy and will also lower our maintenance efforts.


JVM compatibility
-----------------
Current version of JPicus shall work fine with OpenJDK/OracleJVM versions 5 and 6. Version 7 is still not supported, but is on our list.

Contributing
------------
File an issue or send us a pull request.
