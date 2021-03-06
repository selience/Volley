/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.cache;

import java.util.Map;
import java.util.Collections;

/**
 * An interface for a cache keyed by a String with a byte array as data.
 */
public interface Cache {
    /**
     * Retrieves an entry from the cache.
     * @param key Cache key
     * @return An {@link Entry} or null in the event of a cache miss
     */
    public Entry get(String key);

    /**
     * Adds or replaces an entry to the cache.
     * @param key Cache key
     * @param entry Data to store and metadata for cache coherency, TTL, etc.
     */
    public void put(String key, Entry entry);

    /**
     * Performs any potentially long-running actions needed to initialize the cache;
     * will be called from a worker thread.
     */
    public void initialize();

    /**
     * Invalidates an entry in the cache.
     * @param key Cache key
     * @param expireTime The new expireTime
     */
    public void invalidate(String key, long expireTime);

    /**
     * Removes an entry from the cache.
     * @param key Cache key
     */
    public void remove(String key);

    /**
     * Empties the cache.
     */
    public void clear();

    /**
     * Data and metadata for an entry returned by the cache.
     */
    public static class Entry {
        public Entry() {}

        public Entry(byte[] data, Map<String, String> headers, String charset) {
            this.data = data;
            this.charset = charset;
            this.responseHeaders = headers;
        }

        /** The data returned from cache. */
        public byte[] data;

        /** Expire time for cache entry. */
        public long expireTime;

        /** Charset for cache entry, retrieve by the http header. */
        public String charset;

        /** Immutable response headers as received from server; must be non-null. */
        public Map<String, String> responseHeaders = Collections.emptyMap();
        
        /** True if the entry is expired. */
        public boolean isExpired() {
            return expireTime < System.currentTimeMillis();
        }

        /** True if a refresh is needed from the original data source. */
        public boolean refreshNeeded() {
            // still unimplemented, might be use a constant like 'refreshTime'?
            return this.expireTime < System.currentTimeMillis();
        }

        /** Get the cache data size in byte. */
        public int getSize() {
            return data != null ? data.length : 0;
        }

        /** Invalidate cache entry by the expireTime. */
        public static boolean invalidate(Entry entry, long expireTime) {
            if (entry != null) {
                entry.expireTime = expireTime;
                return true;
            }
            return false;
        }

    }
}
