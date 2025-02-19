-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.concurrent-1.0
WLP-DisableAllFeatures-OnConflict: false
visibility=public
singleton=true
IBM-API-Package: javax.enterprise.concurrent; type="spec"
IBM-ShortName: concurrent-1.0
IBM-API-Service: javax.enterprise.concurrent.ContextService; id="DefaultContextService", \
 javax.enterprise.concurrent.ManagedExecutorService; id="DefaultManagedExecutorService", \
 javax.enterprise.concurrent.ManagedScheduledExecutorService; id="DefaultManagedScheduledExecutorService"
Subsystem-Name: Concurrency Utilities for Java EE 1.0
-features=com.ibm.websphere.appserver.containerServices-1.0, \
  com.ibm.websphere.appserver.contextService-1.0, \
  com.ibm.websphere.appserver.eeCompatible-7.0; ibm.tolerates:="6.0,8.0", \
  com.ibm.websphere.appserver.concurrencyPolicy-1.0, \
  com.ibm.websphere.appserver.org.eclipse.microprofile.contextpropagation-1.0; ibm.tolerates:="1.2"
-bundles=com.ibm.ws.javaee.platform.defaultresource, \
 com.ibm.websphere.javaee.concurrent.1.0; location:="dev/api/spec/,lib/"; mavenCoordinates="javax.enterprise.concurrent:javax.enterprise.concurrent-api:1.0", \
 com.ibm.ws.resource, \
 com.ibm.ws.concurrent, \
 io.openliberty.concurrent.internal.basictrigger, \
 io.openliberty.concurrent.internal.compat
kind=ga
edition=core
WLP-Activation-Type: parallel
WLP-InstantOn-Enabled: true
WLP-Platform: javaee-7.0,javaee-8.0,jakartaee-8.0
