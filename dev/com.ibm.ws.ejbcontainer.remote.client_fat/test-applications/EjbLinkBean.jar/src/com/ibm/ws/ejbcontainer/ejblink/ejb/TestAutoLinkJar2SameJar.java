/*******************************************************************************
 * Copyright (c) 2006, 2020 IBM Corporation and others.
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

package com.ibm.ws.ejbcontainer.ejblink.ejb;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 * Basic Stateless Bean implementation for testing AutoLink
 **/
@Stateless
@Remote(EjbLinkDriverRemote.class)
public class TestAutoLinkJar2SameJar extends BasicEjbLinkTest implements EjbLinkDriverLocal {
    @EJB
    public AutoLinkLocal2SameJar beanInCurrentModuleTwice;

    @Override
    public String verifyAmbiguousEJBReferenceException() {
        return "Failed";
    }

    public TestAutoLinkJar2SameJar() {
        // intentionally blank
    }
}
