-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=io.openliberty.passwordUtilities1.1.internal.ee-6.0
singleton=true
-features=\
  com.ibm.websphere.appserver.appSecurity-2.0; ibm.tolerates:="1.0", \
  com.ibm.websphere.appserver.authData-1.0, \
  com.ibm.websphere.appserver.servlet-3.0; ibm.tolerates:="3.1", \
  com.ibm.websphere.appserver.transaction-1.1; ibm.tolerates:="1.2", \
  com.ibm.websphere.appserver.javax.connector.internal-1.6; ibm.tolerates:="1.7"
kind=ga
edition=core