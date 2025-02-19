/*******************************************************************************
 * Copyright (c) 2015, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.ibm.ws.jsf22.fat.cdicommon.managed.factories;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;
import javax.inject.Inject;

import com.ibm.ws.jsf22.fat.cdicommon.beans.factory.FactoryAppBean;
import com.ibm.ws.jsf22.fat.cdicommon.beans.factory.FactoryDepBean;

/**
 *
 */
public class CustomPartialViewContextFactory extends PartialViewContextFactory {

    private PartialViewContextFactory pvcf = null;

    // Field injected bean
    @Inject
    private FactoryAppBean fieldBean;

    // Method Injected bean
    private FactoryDepBean methodBean;

    public CustomPartialViewContextFactory(PartialViewContextFactory fac) {
        pvcf = fac;
    }

    String _postConstruct = ":PostConstructNotCalled";

    @PostConstruct
    public void start() {
        _postConstruct = ":PostConstructCalled";
    }

    @PreDestroy
    public void stop() {
        System.out.println(this.getClass().getSimpleName() + " preDestroy called.");
    }

    @Inject
    public void setMethodBean(FactoryDepBean bean) {
        methodBean = bean;
    }

    @Override
    public PartialViewContext getPartialViewContext(FacesContext context) {
        String output = "Field Injected App Bean is NULL";

        if (fieldBean != null) {
            output = fieldBean.getName();
        }

        output += _postConstruct;

        if (methodBean != null) {
            methodBean.incrementAppCount();
            methodBean.logFirst(FacesContext.getCurrentInstance().getExternalContext(), this.getClass().getSimpleName(), "getPartialViewContext", output);
        } else {
            FacesContext.getCurrentInstance().getExternalContext().log("CustomPartialViewContextFactory method injection failed.");
        }
        return pvcf.getPartialViewContext(context);
    }

}
