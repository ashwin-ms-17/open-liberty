/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
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
package com.ibm.ws.cdi20.fat.apps.secureAsyncEvents;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;

/**
 *
 */

public class CakeObserver {

    @ApplicationScoped
    public static class asyncCakeObserver {
        public void observes(@ObservesAsync CakeArrival cakearrival) {
            cakearrival.addCake(getClass().getSimpleName(), Thread.currentThread().getId());
        }
    }

    @ApplicationScoped
    public static class syncCakeObserver {
        public void observesSync(@Observes CakeArrival cakearrival) {
            cakearrival.addCake(getClass().getSimpleName(), Thread.currentThread().getId());
        }
    }
}
