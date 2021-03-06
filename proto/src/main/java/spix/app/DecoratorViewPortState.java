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

package spix.app;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import com.jme3.app.*;
import com.jme3.app.state.*;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

import com.simsilica.lemur.event.PickState;

/**
 *  A state that manages a viewport that can be used for scene annotations
 *  like selector widgets, grid backgrounds, etc. that don't affect the
 *  regular scene.
 *
 *  @author    Paul Speed
 */
public class DecoratorViewPortState extends BaseAppState {

    public static final String PICK_LAYER_DECORATOR = "decorator";

    private ViewPort viewport;
    private Node root = new Node("Decorator Root");

    public DecoratorViewPortState() {
    }

    public Node getRoot() {
        return root;
    }

    @Override
    protected void initialize( Application app ) {

        // Override the ordering... probably best to do this at the application level
        String[] layersArray = getState(PickState.class).getPickLayerOrder();
        List<String> layers = new ArrayList<>(Arrays.asList(layersArray));
        if( layers.indexOf(PICK_LAYER_DECORATOR) < 0 ) {
            // Insert ourselves after the GUI layer
            int after = layers.indexOf(PickState.PICK_LAYER_GUI);
            layers.add(after+1, PICK_LAYER_DECORATOR);

            getState(PickState.class).setPickLayerOrder(layers.toArray(layersArray));
        }

        viewport = app.getRenderManager().createMainView("decorator", app.getCamera());
        viewport.setEnabled(false);

        viewport.attachScene(root);
    }

    @Override
    protected void cleanup( Application app ) {
        app.getRenderManager().removeMainView(viewport);
    }

    @Override
    protected void onEnable() {
        viewport.setEnabled(true);
        getState(PickState.class).addCollisionRoot(viewport, PICK_LAYER_DECORATOR);
    }

    @Override
    protected void onDisable() {
        getState(PickState.class).removeCollisionRoot(viewport);
        viewport.setEnabled(false);
    }

    @Override
    public void update( float tpf ) {
        root.updateLogicalState(tpf);
    }

    @Override
    public void render( RenderManager renderManager ) {
        root.updateGeometricState();
    }
}
