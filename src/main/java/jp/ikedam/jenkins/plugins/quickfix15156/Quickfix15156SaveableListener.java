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
import hudson.XmlFile;
import hudson.matrix.MatrixProject;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;

/**
 * Quick fix for https://issues.jenkins-ci.org/browse/JENKINS-15156
 * 
 * For details, see {@link Quickfix15156ItemListener}.
 * 
 * There are cases ItemLister.onUpate is not called but
 * SaveableLister.onChange is called.
 * This listener is to manage these cases.
 */
@Extension
public class Quickfix15156SaveableListener extends SaveableListener
{
    @Override
    public void onChange(Saveable o, XmlFile file)
    {
        // only applies to MatrixProject.
        if(!(o instanceof MatrixProject))
        {
            return;
        }
        Quickfix15156Util.fixMatrixConfigurations((MatrixProject)o);
    }
}
