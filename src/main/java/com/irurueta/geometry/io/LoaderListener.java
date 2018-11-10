/*
 * Copyright (C) 2012 Alberto Irurueta Carro (alberto@irurueta.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.irurueta.geometry.io;

/**
 * Interface of listener in charge of notifying when the loading starts, ends
 * or to notify loading progress.
 */
public interface LoaderListener {
    /**
     * This method is called when a Loader starts decoding a file.
     * @param loader Loader decoding a file.
     */
    void onLoadStart(Loader loader);
    
    /**
     * This method is called when a Loader ends decoding a file.
     * @param loader Loader decoding a file.
     */
    void onLoadEnd(Loader loader);
    
    /**
     * This method is called each time a substantial amount of the file is 
     * loaded.
     * @param loader Loader decoding a file.
     * @param progress Amount of progress that has been decoded. Progress is
     * within the range 0.0 and 1.0.
     */
    void onLoadProgressChange(Loader loader, float progress);
}
