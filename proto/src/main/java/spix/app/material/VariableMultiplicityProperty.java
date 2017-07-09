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
package spix.app.material;

import com.jme3.material.MatParam;
import com.jme3.material.plugins.ConditionParser;
import com.jme3.shader.ShaderNodeVariable;
import com.jme3.shader.VariableMapping;
import spix.app.material.hack.MatDefWrapper;
import spix.core.Blackboard;
import spix.core.Spix;
import spix.props.AbstractProperty;
import spix.swing.materialEditor.utils.MaterialDefUtils;
import spix.type.Type;
import spix.ui.MessageRequester;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bouquet on 05/10/16.
 */
public class VariableMultiplicityProperty extends AbstractProperty {

    private ShaderNodeVariable variable;
    private Spix spix;

    public VariableMultiplicityProperty(ShaderNodeVariable variable, String name, Spix spix) {
        super(name);
        this.variable = variable;
        this.spix = spix;
    }

    @Override
    public Type getType() {
        return new Type<>(String.class);
    }

    @Override
    public void setValue(Object value) {
        Object old = getValue();

        String newVal = (String) value;
        if (newVal.trim().equals("")) {
            newVal = null;
        } else {
            try {
                Integer.parseInt(newVal);
            } catch (NumberFormatException e) {
                spix.getService(MessageRequester.class).showMessage("Invalid input", "Multiplicity must be an integer", MessageRequester.Type.Error);
                return;
            }
        }
        variable.setMultiplicity(newVal);

        firePropertyChange(old, newVal, false);
    }


    @Override
    public Object getValue() {
        String val = variable.getMultiplicity();
        if (val == null) {
            val = "";
        }
        return val;
    }

}
