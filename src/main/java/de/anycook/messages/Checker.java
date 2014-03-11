/*
 * This file is part of anycook. The new internet cookbook
 * Copyright (C) 2014 Jan Graßegger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see [http://www.gnu.org/licenses/].
 */

package de.anycook.messages;

import org.apache.log4j.Logger;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.ConnectionCallback;
import javax.ws.rs.container.TimeoutHandler;

public abstract class Checker implements Runnable {
    protected final Logger logger;
    protected boolean timeout;

    public Checker() {
        logger = Logger.getLogger(getClass());
        timeout = false;
    }

    protected void timeout() {
        timeout = true;
    }

    public class ResponseListener implements ConnectionCallback {


        @Override
        public void onDisconnect(AsyncResponse disconnected) {
            Thread.currentThread().interrupt();
            logger.debug("disconnected");
        }
    }

    public static class TimeOut implements TimeoutHandler {
        @Override
        public void handleTimeout(AsyncResponse asyncResponse) {
            Thread.currentThread().interrupt();
            Logger.getLogger(TimeOut.class).debug("timeout");
        }
    }

    protected static class ContextObject {
        public final AsyncResponse response;

        public ContextObject(AsyncResponse response) {
            this.response = response;
        }
    }

}
