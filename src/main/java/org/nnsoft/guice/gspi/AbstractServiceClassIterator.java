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

import static java.lang.String.format;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.inject.ProvisionException;

abstract class AbstractServiceClassIterator<S>
    implements Iterator<Class<? extends S>>
{

    /**
     * The class or interface representing the service being loaded.
     */
    private final Class<S> service;

    /**
     * The class loader used to locate, load, and instantiate providers.
     */
    private final ClassLoader classLoader;

    private Iterator<String> pending = null;

    private String nextName = null;

    public AbstractServiceClassIterator( Class<S> service, ClassLoader classLoader )
    {
        this.service = service;
        this.classLoader = classLoader;
    }

    @Override
    public final boolean hasNext()
    {
        if ( nextName != null )
        {
            return true;
        }

        while ( ( pending == null ) || !pending.hasNext() )
        {
            if ( !hasMorePendingNames() )
            {
                return false;
            }
            pending = getPendingNames();
        }

        nextName = pending.next();
        return true;
    }

    protected abstract boolean hasMorePendingNames();

    protected abstract Iterator<String> getPendingNames();

    @Override
    public final Class<? extends S> next()
    {
        if ( !hasNext() )
        {
            throw new NoSuchElementException();
        }
        String className = nextName;
        nextName = null;
        try
        {
            Class<?> clazz = classLoader.loadClass( className );
            if ( !service.isAssignableFrom( clazz ) )
            {
                throw new ProvisionException( format( "Provider '%s' is not assignable to Service '%s'",
                                                      className, service.getName() ) );
            }
            return clazz.asSubclass( service );
        }
        catch ( ClassNotFoundException e )
        {
            throw new ProvisionException( format( "Provider '%s' not found: %s", className, e.getMessage() ) );
        }
        catch ( ClassCastException e )
        {
            throw new ProvisionException( format( "Provider '%s' is not assignable to Service '%s'",
                                                  className, service.getName() ) );
        }
    }

    @Override
    public final void remove()
    {
        throw new UnsupportedOperationException();
    }

}
