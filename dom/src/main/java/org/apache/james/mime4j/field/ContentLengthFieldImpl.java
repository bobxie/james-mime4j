/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.mime4j.field;

import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.dom.field.ContentLengthField;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.FieldParser;

/**
 * Represents a <code>Content-Length</code> field.
 */
public class ContentLengthFieldImpl extends AbstractField implements ContentLengthField {

    private boolean parsed = false;
    private long contentLength;

    ContentLengthFieldImpl(Field rawField, DecodeMonitor monitor) {
        super(rawField, monitor);
    }

    private void parse() {
        parsed = true;
        String body = getBody();
        if (body != null) {
            try {
                long v = Long.parseLong(body);
                if (v < 0) {
                    if (monitor.warn("Negative content length: " + body, 
                            "ignoring Content-Length header")) {
                        contentLength = -1;
                    }
                } else {
                    contentLength = v;
                }
            } catch (NumberFormatException e) {
                if (monitor.warn("Invalid content length: " + body, 
                        "ignoring Content-Length header")) {
                    contentLength = -1;
                }
            }
        } else {
            contentLength = -1;
        }
    }
    
    public long getContentLength() {
        if (!parsed) {
            parse();
        }
        return contentLength;
    }

    public static final FieldParser<ContentLengthField> PARSER = new FieldParser<ContentLengthField>() {
        
        public ContentLengthField parse(final Field rawField, final DecodeMonitor monitor) {
            return new ContentLengthFieldImpl(rawField, monitor);
        }
        
    };
}
