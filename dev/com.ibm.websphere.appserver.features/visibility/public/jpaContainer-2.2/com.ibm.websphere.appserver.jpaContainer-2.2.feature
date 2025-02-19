-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.jpaContainer-2.2
WLP-DisableAllFeatures-OnConflict: false
visibility=public
singleton=true
kind=ga
edition=core
Subsystem-Name: Java Persistence API Container 2.2
IBM-ShortName: jpaContainer-2.2
IBM-API-Package: javax.persistence; type="spec", \
 javax.persistence.spi; type="spec", \
 javax.persistence.criteria; type="spec", \
 javax.persistence.metamodel; type="spec", \
 javax.activation; type="spec"; require-java:="9", \
 javax.xml.bind; type="spec"; require-java:="9", \
 javax.xml.bind.annotation; type="spec"; require-java:="9", \
 javax.xml.bind.annotation.adapters; type="spec"; require-java:="9", \
 javax.xml.bind.attachment; type="spec"; require-java:="9", \
 javax.xml.bind.helpers; type="spec"; require-java:="9", \
 javax.xml.bind.util; type="spec"; require-java:="9"
IBM-App-ForceRestart: uninstall, \
 install
-features=com.ibm.websphere.appserver.optional.jaxb-2.2, \
  com.ibm.websphere.appserver.jdbc-4.2; ibm.tolerates:="4.3", \
  com.ibm.websphere.appserver.transaction-1.2, \
  com.ibm.websphere.appserver.javax.annotation-1.3; apiJar=false, \
  com.ibm.websphere.appserver.javax.persistence-2.2, \
  com.ibm.websphere.appserver.eeCompatible-8.0, \
  com.ibm.websphere.appserver.jndi-1.0
-bundles=com.ibm.ws.jpa.container.v22, \
 com.ibm.ws.jpa.container, \
 com.ibm.ws.jpa.container.thirdparty
WLP-Activation-Type: parallel
WLP-InstantOn-Enabled: true
WLP-Platform: javaee-8.0,jakartaee-8.0
