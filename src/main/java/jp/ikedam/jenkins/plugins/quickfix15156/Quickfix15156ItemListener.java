/*
 * The MIT License
 * 
 * Copyright (c) 2013 IKEDA Yasuyuki
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jp.ikedam.jenkins.plugins.quickfix15156;

import hudson.Extension;
import hudson.matrix.MatrixProject;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;

/**
 * Quick fix for https://issues.jenkins-ci.org/browse/JENKINS-15156
 * 
 * This problem caused for MatrixConfiguration is not properly
 * initialized when new MatrixProject is created or
 * new MatrixConfiguration is added.
 * 
 * Fix up MatrixConfiguration.
 * onCreated is not called for MatrixConfiguration,
 * so call it manually in following cases.
 * <ul>
 *     <li>onCreated is called for MatrixProject (in case a MatrixProject is copied)
 *     <li>onUpdated is called for MatrixProject (in case a new MatrixProject is added)
 * </ul>
 *
 */
@Extension
public class Quickfix15156ItemListener extends ItemListener
{
    /**
     * Called when a new item is created.
     * 
     * If called for MatrixProject, 
     * initialize MatrixConfiguration contained in it.
     * 
     * @param item Updated item.
     * @see hudson.model.listeners.ItemListener#onCreated(hudson.model.Item)
     */
    @Override
    public void onCreated(Item item)
    {
        // only applies to MatrixProject.
        if(!(item instanceof MatrixProject))
        {
            return;
        }
        Quickfix15156Util.fixMatrixConfigurations((MatrixProject)item);
    }
    
    /**
     * Called when an existing item is updated.
     * 
     * If called for MatrixProject, 
     * initialize MatrixConfiguration contained in it.
     * (it's the case new MatrixConfiguration is added).
     * 
     * @param item
     * @see hudson.model.listeners.ItemListener#onUpdated(hudson.model.Item)
     */
    @Override
    public void onUpdated(Item item)
    {
        // only applies to MatrixProject.
        if(!(item instanceof MatrixProject))
        {
            return;
        }
        Quickfix15156Util.fixMatrixConfigurations((MatrixProject)item);
    }
    
}
