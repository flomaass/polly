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
package de.skuzzle.polly.http.internal;

import de.skuzzle.polly.http.api.TrafficInformation;


class TrafficInformationImpl implements TrafficInformation {

    private int upload;
    private int download;

    
    public synchronized void updateUpload(int bytes) {
        this.upload += bytes;
    }
    
    
    
    public synchronized void updateDownload(int bytes) {
        this.download += bytes;
    }
    
    
    
    @Override
    public int getUpload() {
        return this.upload;
    }
    
    

    @Override
    public int getDownload() {
        return this.download;
    }
}