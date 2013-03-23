/*
 * Copyright 2013 Simon Taddiken
 *
 * This file is part of Polly HTTP API.
 *
 * Polly HTTP API is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 *
 * Polly HTTP API is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with Polly HTTP API. If not, see http://www.gnu.org/licenses/.
 */
package de.skuzzle.polly.http.api;

/**
 * This encapsulates traffic information for a {@link HttpSession}.
 * 
 * @author Simon Taddiken
 */
public interface TrafficInformation {

    /**
     * Gets the amount of data (in bytes) that was sent towards the client.
     * 
     * @return Bytes sent towards a client.
     */
    public int getUpload();
    
    /**
     * Gets the amount of data (in bytes) that was sent to us from a client.
     * 
     * @return Bytes sent from a client.
     */
    public int getDownload();
}