/*
 * $Id$
 * 
 * Copyright (c) 2016, Simsilica, LLC
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the 
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package spix.props;

import java.beans.*;
import java.lang.reflect.*;

import com.google.common.base.MoreObjects;


/**
 *
 *
 *  @author    Paul Speed
 */
public class BeanProperty extends AbstractProperty {
    private Object object;
    private Method getter;
    private Method setter;
    
    public BeanProperty( Object object, String name, Method getter, Method setter ) {
        super(name);
        this.object = object;
        this.getter = getter;
        this.setter = setter;
    }

    private static Method findSetter( Class type, String methodName, Class parameterType ) {
        try {
            // Try the easy lookup first
            return type.getMethod(methodName, parameterType);
        } catch( NoSuchMethodException e ) {
            // Do a looser search
            for( Method m : type.getMethods() ) {
                if( m.getParameterTypes().length != 1 ) {
                    continue;
                }
                if( !methodName.equals(m.getName()) ) {
                    continue;
                }
                if( m.getParameterTypes()[0].isAssignableFrom(parameterType) ) {
                    return m;
                }                
            }
            return null;
        }
    } 
 
    public static BeanProperty create( Property parent, String name ) {
        return create(parent.getValue(), name);
    }
    
    public static BeanProperty create( Object object, String name ) {
        try {
            // Use the bean property info to find the appropriate property
            // We'll move this method later as we will likely have different 
            // BeanProperty implementations for lists, etc.
            Class type = object.getClass();
            BeanInfo info = Introspector.getBeanInfo(type);
        
            //System.out.println("info:" + info);
            for( PropertyDescriptor pd : info.getPropertyDescriptors() ) {
                //System.out.println("    pd:" + pd);
                if( !name.equals(pd.getName()) ) {
                    continue;
                }
                
                Method write = pd.getWriteMethod();
                Method read = pd.getReadMethod();
                if( write == null ) {
                    // JME has some setters that return non-void types and Java Beans
                    // standard introspection doesn't like that.
                    String n = "s" + read.getName().substring(1);
                    write = findSetter(type, n, read.getReturnType());
                }
                
                return new BeanProperty(object, name, read, write);
            }            
        
            return null;
        } catch( IntrospectionException e ) {
            throw new RuntimeException("Error creating bean property:" + name, e);
        }
    }
 
    @Override   
    public Class getType() {
        return getter.getReturnType();
    }
    
    @Override   
    public void setValue( Object value ) {
        try {
            Object old = getValue();           
            setter.invoke(object, value);
            firePropertyChange(old, value, true);
        } catch( IllegalAccessException | InvocationTargetException e ) {
            throw new RuntimeException("Error setting property:" + getName(), e);
        }
    }
    
    @Override   
    public Object getValue() {
        try {
            return getter.invoke(object);
        } catch( IllegalAccessException | InvocationTargetException e ) {
            throw new RuntimeException("Error getting property:" + getName(), e);
        }
    }
    
    @Override   
    public String toString() {
        return MoreObjects.toStringHelper(getClass().getSimpleName())
                .add("object", object)
                .add("name", getName())
                .add("getter", getter)
                .add("setter", setter)
                .toString();
    }    
}