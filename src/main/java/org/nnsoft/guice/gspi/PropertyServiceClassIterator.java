package org.nnsoft.guice.gspi;

/*
 *  Copyright 2012 The 99 Software Foundation
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

import java.util.Iterator;
import java.util.StringTokenizer;

final class PropertyServiceClassIterator<S>
    extends AbstractServiceClassIterator<S>
{

    private static final String DEFAULT_SEPARATOR = ",";

    private final String systemServiceNames;

    private boolean consumed = false;

    public PropertyServiceClassIterator( Class<S> service, ClassLoader classLoader, String systemServiceNames )
    {
        super( service, classLoader );
        this.systemServiceNames = systemServiceNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasMorePendingNames()
    {
        return !consumed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Iterator<String> getPendingNames()
    {
        consumed = true;
        return new Iterator<String>()
        {

            private final StringTokenizer tokenizer = new StringTokenizer( systemServiceNames, DEFAULT_SEPARATOR );

            @Override
            public boolean hasNext()
            {
                return tokenizer.hasMoreTokens();
            }

            @Override
            public String next()
            {
                return tokenizer.nextToken();
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }

        };
    }

}
