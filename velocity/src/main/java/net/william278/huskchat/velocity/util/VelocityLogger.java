/*
 * This file is part of HuskChat, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskchat.velocity.util;

import net.william278.huskchat.util.Logger;

import java.util.logging.Level;

public class VelocityLogger implements Logger {

    private org.slf4j.Logger parent;

    private VelocityLogger() {
    }

    private static VelocityLogger instance;

    public static VelocityLogger get(org.slf4j.Logger parent) {
        if (instance == null) {
            instance = new VelocityLogger();
            instance.parent = parent;
        }
        return instance;
    }

    @Override
    public void log(Level level, String message, Exception e) {
        logMessage(level, message);
        e.printStackTrace();
    }

    @Override
    public void log(Level level, String message) {
        logMessage(level, message);
    }

    @Override
    public void info(String message) {
        logMessage(Level.INFO, message);
    }

    @Override
    public void severe(String message) {
        logMessage(Level.SEVERE, message);
    }

    @Override
    public void config(String message) {
        logMessage(Level.CONFIG, message);
    }

    // Logs the message using SLF4J
    private void logMessage(Level level, String message) {
        switch (level.intValue()) {
            case 1000 -> parent.error(message); // Severe
            case 900 -> parent.warn(message); // Warning
            case 70 -> parent.warn("[Config] " + message);
            default -> parent.info(message);
        }
    }
}