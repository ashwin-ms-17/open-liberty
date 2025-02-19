/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.javaeesec.properties;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.runtime.metadata.MetaData;
import com.ibm.ws.security.javaeesec.CDIHelper;
import com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl;
import com.ibm.ws.webcontainer.osgi.metadata.WebModuleMetaDataImpl;
import com.ibm.ws.webcontainer.security.util.WebConfigUtils;
import com.ibm.wsspi.webcontainer.metadata.WebModuleMetaData;

public class ModulePropertiesUtils {
    private static final TraceComponent tc = Tr.register(ModulePropertiesUtils.class);

    private static ModulePropertiesUtils self = new ModulePropertiesUtils();
    private final Map<MetaData, HamObject> ModuleToHam = Collections.synchronizedMap(new WeakHashMap<MetaData, HamObject>());
    private final Map<MetaData, HamLookupObject> ModuleToHamLookup = Collections.synchronizedMap(new WeakHashMap<MetaData, HamLookupObject>());

    protected ModulePropertiesUtils() {}

    public static ModulePropertiesUtils getInstance() {
        return self;
    }

    public String getJ2EEModuleName() {
        WebModuleMetaData wmmd = getWebModuleMetaData();
        if (wmmd != null) {
            return wmmd.getJ2EEName().getModule();
        } else {
            // this condition happens during processing postinvoke, fallback.
            ComponentMetaData cmd = getComponentMetaData();
            if (cmd != null) {
                return cmd.getModuleMetaData().getJ2EEName().getModule();
            }
        }
        return null;
    }

    public String getJ2EEApplicationName() {
        WebModuleMetaData wmmd = getWebModuleMetaData();
        if (wmmd != null) {
            return wmmd.getJ2EEName().getApplication();
        } else {
            // this condition happens during processing postinvoke, fallback.
            ComponentMetaData cmd = getComponentMetaData();
            if (cmd != null) {
                return cmd.getJ2EEName().getApplication();
            }
        }
        return null;
    }

    public boolean isHttpAuthenticationMechanism() {
        HttpAuthenticationMechanism ham = getHttpAuthenticationMechanism(false);
        if (ham != null) {
            return true;
        }
        return false;
    }

    public HttpAuthenticationMechanism getHttpAuthenticationMechanism() {
        return getHttpAuthenticationMechanism(true);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private HttpAuthenticationMechanism getHttpAuthenticationMechanism(boolean logError) {
        MetaData metadata = null;

        // if we already know this web module doesn't have HAM, we can skip it
        WebModuleMetaDataImpl wmmd = (WebModuleMetaDataImpl) getWebModuleMetaData();
        if (wmmd != null) {
            metadata = wmmd;
            if (!wmmd.checkForHAM()) {
                return null;
            }
        }

        if (metadata == null) {
            metadata = getComponentMetaData();
        }
        Class hamClass = null;
        HttpAuthenticationMechanism ham = null;

        // lookup ApplicationScoped cache first.
        HamObject hamObject = ModuleToHam.get(metadata);
        if (hamObject != null) {
            ham = hamObject.getHam();
        } else {
            // lookup non ApplicationScoped cache. if hits, it's already looked up for BeanManager and HAM impl class.
            HamLookupObject hamLookupObject = ModuleToHamLookup.get(metadata);
            if (hamLookupObject != null) {
                ham = hamLookupObject.getHam();
            } else {
                // find HAM from BeanManagers
                boolean isCacheable = true;
                BeanManager beanManager = null;
                CDI cdi = getCDI();
                if (cdi != null) {
                    beanManager = cdi.getBeanManager();
                    if (beanManager != null) {
                        Instance<ModulePropertiesProvider> mppi = cdi.select(ModulePropertiesProvider.class);
                        if (mppi != null && !mppi.isUnsatisfied() && !mppi.isAmbiguous()) {
                            List<Class> implClassList = mppi.get().getAuthMechClassList();
                            if (implClassList != null) {
                                if (implClassList.size() == 0) {
                                    if (logError) {
                                        Tr.error(tc, "JAVAEESEC_ERROR_NO_HAM", getJ2EEModuleName(), getJ2EEApplicationName());
                                    }
                                } else {
                                    Bean<HttpAuthenticationMechanism> bean = null;
                                    for (Class implClass : implClassList) {
                                        hamClass = implClass;
                                        bean = getBean(beanManager, implClass);
                                        if (bean != null) {
                                            break;
                                        }
                                    }
                                    if (bean != null) {
                                        ham = (HttpAuthenticationMechanism) beanManager.getReference(bean, hamClass, beanManager.createCreationalContext(bean));
                                        isCacheable = isCacheable(bean);
                                        if (tc.isDebugEnabled()) {
                                            Tr.debug(tc, "HAM from the current CDI : " + ham);
                                        }
                                    } else {
                                        BeanManager moduleBeanManager = CDIHelper.getBeanManager();
                                        if (!beanManager.equals(moduleBeanManager)) {
                                            // try module level.
                                            beanManager = moduleBeanManager;
                                            bean = getBean(beanManager, hamClass);
                                            if (bean != null) {
                                                ham = (HttpAuthenticationMechanism) beanManager.getReference(bean, hamClass, beanManager.createCreationalContext(bean));
                                                isCacheable = isCacheable(bean);
                                                if (tc.isDebugEnabled()) {
                                                    Tr.debug(tc, "HAM from the module BeanManager : " + ham);
                                                }
                                            }
                                        }
                                    }
                                    if (ham == null) {
                                        Tr.error(tc, "JAVAEESEC_ERROR_NO_HAM", getJ2EEModuleName(), getJ2EEApplicationName());
                                    }
                                }
                            } else {
                                if (tc.isDebugEnabled()) {
                                    Tr.debug(tc, "No HAM implementation class defined. Module Name : " + getJ2EEModuleName() + ", Application Name : " + getJ2EEApplicationName());
                                }
                            }
                        } else if (logError) {
                            throw new RuntimeException("ModulePropertiesProvider object cannot be identified.");
                        }
                    }
                }
                if (!isCacheable) {
                    // when isCacheable is false, beanManager and hamClass always exist.
                    ModuleToHamLookup.put(metadata, new HamLookupObject(beanManager, hamClass));

                    if (wmmd != null) {
                        wmmd.setHasHAM(true);
                    }

                } else {
                    // in order to avoid filling up the same error message, cache the data even though there is an error
                    ModuleToHam.put(metadata, new HamObject(ham));

                    if (wmmd != null) {
                        wmmd.setHasHAM(ham != null);
                    }
                }
            }

        }
        return ham;

    }

    public boolean isELExpression(String elExpression) {
        if (elExpression != null) {
            return elExpression.startsWith("#{") || elExpression.startsWith("${");
        }
        return false;
    }

    public boolean isImmediateEval(String elExpression) {
        if (elExpression != null) {
            return elExpression.startsWith("${");
        }
        return false;
    }

    public String extractExpression(String elExpression) {
        if (elExpression != null && (elExpression.startsWith("#{") || elExpression.startsWith("${")) && elExpression.endsWith("}")) {
            return elExpression.substring(2, elExpression.length() - 1);
        }
        return elExpression;
    }

    protected WebModuleMetaData getWebModuleMetaData() {
        return WebConfigUtils.getWebModuleMetaData();
    }

    protected ComponentMetaData getComponentMetaData() {
        return ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData();
    }

    @SuppressWarnings("unchecked")
    private Bean<HttpAuthenticationMechanism> getBean(BeanManager beanManager, Class<HttpAuthenticationMechanism> hamClass) {
        Bean<HttpAuthenticationMechanism> bean = null;
        Set<Bean<?>> beans = beanManager.getBeans(hamClass);
        if (beans.size() == 1) {
            bean = (Bean<HttpAuthenticationMechanism>) beans.iterator().next();
        }
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "HAM : " + hamClass + " Number of HAM bean: " + beans.size() + " bean.toString(): " + (bean != null ? bean.toString() : "<NO bean>"));
        }
        return bean;
    }

    private boolean isCacheable(Bean<?> bean) {
        boolean isCacheable = false;
        Class<? extends Annotation> scope = bean.getScope();
        if (scope != null) {
            if (scope.isAssignableFrom(ApplicationScoped.class)) {
                isCacheable = true;
            }
        }
        return isCacheable;
    }

    //This is here so it can be overridden by a unit test.
    protected CDI getCDI() {
        return CDIHelper.getCDI();
    }

    /**
     * this is for unit test.
     */
    protected void clearModuleTable() {
        ModuleToHam.clear();
    }

    private MetaData getMetaData() {
        WebModuleMetaData wmmd = getWebModuleMetaData();
        if (wmmd != null) {
            return wmmd;
        }
        return getComponentMetaData();
    }

    /**
     * this wrapper object is used for WeakHashMap which does not support null object.
     */
    class HamObject {
        private final HttpAuthenticationMechanism ham;

        HamObject(HttpAuthenticationMechanism ham) {
            this.ham = ham;
        }

        HttpAuthenticationMechanism getHam() {
            return ham;
        }
    }

    /**
      */
    class HamLookupObject {
        private final BeanManager beanManager;
        private final Class<HttpAuthenticationMechanism> hamClass;

        HamLookupObject(BeanManager beanManager, Class<HttpAuthenticationMechanism> hamClass) {
            this.beanManager = beanManager;
            this.hamClass = hamClass;
        }

        // when the object is cached, it's already validated that there is only one bean class.
        HttpAuthenticationMechanism getHam() {
            Set<Bean<?>> beans = beanManager.getBeans(hamClass);
            Bean<? extends Object> bean = beanManager.resolve(beans);
            return (HttpAuthenticationMechanism) beanManager.getReference(bean, hamClass, beanManager.createCreationalContext(bean));
        }
    }
}
