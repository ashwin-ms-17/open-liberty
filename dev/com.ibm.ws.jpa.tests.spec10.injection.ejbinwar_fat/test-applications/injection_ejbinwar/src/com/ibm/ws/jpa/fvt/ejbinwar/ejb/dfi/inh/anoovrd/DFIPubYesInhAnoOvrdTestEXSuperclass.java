/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
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

package com.ibm.ws.jpa.fvt.ejbinwar.ejb.dfi.inh.anoovrd;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import com.ibm.ws.testtooling.vehicle.ejb.BMTEJBTestVehicle;

/**
 * JPA Injection Test EJB Superclass
 *
 * Injection Type: Method
 * Field/Method Protection: Public
 * Inheritance: Yes, Annotation Override of Superclass Injection Methods
 *
 *
 */
public abstract class DFIPubYesInhAnoOvrdTestEXSuperclass extends BMTEJBTestVehicle {
    /*
     * JPA Resource Injection with No Override by Deployment Descriptor
     */

    // Container Managed Persistence Context

    // This EntityManager should refer to the COMMON_JTA in the EJB module
    @PersistenceContext(unitName = "COMMON_JTA", type = PersistenceContextType.EXTENDED)
    public EntityManager em_cmex_common_ejb;

    // This EntityManager should refer to the EJB_JTA in the EJB module
    @PersistenceContext(unitName = "EJB_JTA", type = PersistenceContextType.EXTENDED)
    public EntityManager em_cmex_ejb_ejb;

    // This EntityManager should refer to the COMMON_JTA in the jar in the Application's Library directory
    @PersistenceContext(unitName = "../lib/jpapulib.jar#COMMON_JTA", type = PersistenceContextType.EXTENDED)
    public EntityManager em_cmex_common_earlib;

    // This EntityManager should refer to the JPALIB_JTA in the jar in the Application's Library directory
    @PersistenceContext(unitName = "JPALIB_JTA", type = PersistenceContextType.EXTENDED)
    public EntityManager em_cmex_jpalib_earlib;

}
