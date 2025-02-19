/*******************************************************************************
 * Copyright (c) 2020, 2024 IBM Corporation and others.
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
package com.ibm.ws.recoverylog.spi;

import java.time.Duration;

import com.ibm.tx.TranConstants;
import com.ibm.tx.config.ConfigurationProviderManager;
import com.ibm.tx.util.alarm.Alarm;
import com.ibm.tx.util.alarm.AlarmListener;
import com.ibm.tx.util.alarm.AlarmManager;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.wsspi.kernel.service.utils.FrameworkState;

/**
 * Manage the HA DB Log Availability timer. Based on other timers in the codebase.
 *
 * A thread is spawned which will update a timestamp every HEARTBEAT_FREQUENCY seconds.
 *
 */
public class HeartbeatLogManager {
    private static final TraceComponent tc = Tr.register(HeartbeatLogManager.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    private static TimeoutInfo _info;

    public static void setTimeout(HeartbeatLog heartbeatLog, Duration peerLockTimeBetweenHeartbeats) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "setTimeout", heartbeatLog, peerLockTimeBetweenHeartbeats);

        // Stop any existing timeout
        stopTimeout();

        _info = new TimeoutInfo(heartbeatLog, peerLockTimeBetweenHeartbeats); // 5 seconds is the default
        if (tc.isEntryEnabled())
            Tr.exit(tc, "setTimeout", _info);
    }

    /**
     * This class records information for a timeout for a transaction.
     */
    private static class TimeoutInfo implements AlarmListener {
        protected HeartbeatLog _heartbeatLog;

        protected final Duration _duration;

        private Alarm _alarm;

        private volatile boolean _isHeartbeating = true;

        private final AlarmManager _alarmManager = ConfigurationProviderManager.getConfigurationProvider().getAlarmManager();

        protected TimeoutInfo(HeartbeatLog heartbeatLog, Duration duration) {
            if (tc.isEntryEnabled())
                Tr.entry(tc, "TimeoutInfo", heartbeatLog, duration);

            _duration = duration;

            _heartbeatLog = heartbeatLog;

            if (_heartbeatLog != null)
                _alarm = _alarmManager.scheduleAlarm(_duration.toMillis(), this, null);

            if (tc.isEntryEnabled())
                Tr.exit(tc, "TimeoutInfo");
        }

        /**
         * Takes appropriate action for a timeout.
         * The entry in the pendingTimeouts hashtable will be removed by
         * the transaction completion code.
         */
        @Override
        @FFDCIgnore({ LogClosedException.class })
        public void alarm(Object alarmContext) {
            if (tc.isEntryEnabled())
                Tr.entry(tc, "alarm");

            if (_heartbeatLog != null && _isHeartbeating) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Update the HADB timestamp");
                try {
                    _heartbeatLog.heartBeat();
                } catch (LogClosedException e) {
                    _isHeartbeating = false;
                }
            } else {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "NULL heartbeatLog or heartbeating cancelled");
                _isHeartbeating = false;
            }

            // Respawn the alarm
            synchronized (this) {
                if (_heartbeatLog != null && _isHeartbeating && !FrameworkState.isStopping())
                    _alarm = _alarmManager.scheduleAlarm(_duration.toMillis(), this, null);
            }

            if (tc.isEntryEnabled())
                Tr.exit(tc, "alarm");
        }

        public synchronized void cancelAlarm() {
            if (tc.isEntryEnabled())
                Tr.entry(tc, "cancelAlarm", _alarm);

            _isHeartbeating = false;

            if (_alarm != null) {
                _alarm.cancel();
                _alarm = null;
            }

            if (tc.isEntryEnabled())
                Tr.exit(tc, "cancelAlarm");
        }
    }

    /**
     *
     */
    public static void stopTimeout() {
        if (_info != null) {
            _info.cancelAlarm();
        }
    }
}