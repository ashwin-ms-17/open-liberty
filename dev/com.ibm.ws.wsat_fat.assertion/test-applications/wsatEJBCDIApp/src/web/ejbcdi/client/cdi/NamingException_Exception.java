/*******************************************************************************
 * Copyright (c) 2019, 2023 IBM Corporation and others.
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
package web.ejbcdi.client.cdi;

import javax.xml.ws.WebFault;

@WebFault(name = "NamingException", targetNamespace = "http://server.ejbcdi.web/")
public class NamingException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private NamingException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public NamingException_Exception(String message, NamingException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public NamingException_Exception(String message, NamingException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: web.ejbcdi.client.cdi.NamingException
     */
    public NamingException getFaultInfo() {
        return faultInfo;
    }

}
