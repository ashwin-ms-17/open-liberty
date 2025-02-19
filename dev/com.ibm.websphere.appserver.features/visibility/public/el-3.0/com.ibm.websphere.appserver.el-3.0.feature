-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.el-3.0
WLP-DisableAllFeatures-OnConflict: false
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
 uninstall
IBM-API-Package: javax.el; type="spec", \
 org.apache.el;  type="internal", \
 org.apache.el.lang; type="internal", \
 org.apache.el.util; type="internal", \
 org.apache.el.stream; type="internal"
IBM-ShortName: el-3.0
Subsystem-Version: 3.0.0
Subsystem-Name: Expression Language 3.0
-features=com.ibm.websphere.appserver.eeCompatible-7.0; ibm.tolerates:="6.0,8.0", \
  com.ibm.websphere.appserver.javax.el-3.0
-bundles=com.ibm.ws.org.apache.jasper.el.3.0
kind=ga
edition=core
WLP-Activation-Type: parallel
WLP-InstantOn-Enabled: true
WLP-Platform: javaee-7.0,javaee-8.0,jakartaee-8.0
