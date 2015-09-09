# JDK
Downloads the Oracle JDK from a given URL and creates a binary Debian package. 

Example configuration for a binary Debian package 'my-jdk8_1.8.0.60_amd64.deb' 
```xml
<fuin-deb-config>

    <!-- The 'modules' section defines standards that are inherited by all childs -->
    <modules prefix="my-" maintainer="your@domain.tld" arch="amd64" installation-path="/opt">

        <jdk version="1.8.0.60" 
             description="Java SE Development Kit 8" 
             url="http://download.oracle.com/otn-pub/java/jdk/8u60-b27/jdk-8u60-linux-x64.tar.gz">
            
            <!-- The full name of the package is 'my-jdk8' (prefix+name) --> 
            <package name="jdk8" />
            
        </jdk>

    </modules>

</fuin-deb-config>
```

 