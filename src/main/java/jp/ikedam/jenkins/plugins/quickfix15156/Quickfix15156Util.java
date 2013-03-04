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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import jenkins.model.lazy.AbstractLazyLoadRunMap;

import hudson.matrix.MatrixConfiguration;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixProject;
import hudson.model.RunMap;
import hudson.model.RunMap.Constructor;
import hudson.model.AbstractProject;

public class Quickfix15156Util
{
    private static Logger LOGGER = Logger.getLogger(Quickfix15156ItemListener.class.getName());
    
    /**
     * Test whether MatrixConfiguration is properly initialized.
     * 
     * I can say MatrixConfiguration is properly initialized if:
     * 
     * <ul>
     *   <li>MatrixConfiguration.builds.cons is properly set (not null)</li>
     *   <li>MatrixConfiguration.builds.dir is properly set (not null)</li>
     * </ul>
     * 
     * Sad to say, there's no way to access RunMap.cons
     * nor AbstractLazyLoadRunMap.dir in proper way.
     * So here I use Java reflections to access these members.
     * 
     * @param c MatrixConfiguration to test
     * @return true if c is properly initialized, or cannot determine.
     */
    static protected boolean isInitialized(MatrixConfiguration c)
    {
        RunMap<MatrixRun> builds = c._getRuns();
        
        if(builds == null)
        {
            return false;
        }
        
        try
        {
            // first, test MatrixConfiguration.builds.cons(RunMap.cons)
            {
                Field consField = RunMap.class.getDeclaredField("cons");
                consField.setAccessible(true);
                if(consField.get(builds) == null)
                {
                    // cons is not set properly
                    return false;
                }
            }
            
            // second, test MatrixConfiguration.builds.dir(AbstractLazyLoadRunMap.dir)
            {
                Field dirField = AbstractLazyLoadRunMap.class.getDeclaredField("dir");
                dirField.setAccessible(true);
                if(dirField.get(builds) == null)
                {
                    // dir is not set properly
                    return false;
                }
            }
            
            
        }
        catch (Exception e)
        {
            LOGGER.log(Level.WARNING, "Failed to access private fields", e);
        }
        
        return true;
    }
    
    static private class MatrixRunConstructor implements Constructor<MatrixRun>
    {
        private MatrixConfiguration c;
        
        MatrixRunConstructor(MatrixConfiguration c)
        {
            this.c = c;
        }
        
        @Override
        public MatrixRun create(File dir) throws IOException
        {
            return new MatrixRun(c, dir);
        }
    }
    
    static protected void createBuilds(MatrixConfiguration c)
    {
        try
        {
            RunMap<MatrixRun> builds = new RunMap<MatrixRun>(
                    Jenkins.getInstance().getBuildDirFor(c),
                    new MatrixRunConstructor(c)
            );
            Field buildsField = AbstractProject.class.getDeclaredField("builds");
            
            buildsField.setAccessible(true);
            buildsField.set(c, builds);
        }
        catch (Exception e)
        {
            LOGGER.log(Level.WARNING, "Failed to access private fields", e);
        }
    }
    
    static public void fixMatrixConfigurations(MatrixProject project)
    {
        try
        {
            project.getItems();
        }
        catch(NullPointerException e)
        {
            // In some versions (I saw it with 1.487), MatrixConfiguration.configurations
            // is not set with onChange for a new created job.
            // In that case, I can do nothing.
            return;
        }
        for(MatrixConfiguration c: project.getItems())
        {
            if(!isInitialized(c))
            {
                LOGGER.info(String.format("Initialize %s", c.toString()));
                // this works only for Jenkins 1.502 and later...
                c.onCreatedFromScratch();
                if(!isInitialized(c))
                {
                    // for prior to 1.502.
                    createBuilds(c);
                }
            }
        }
    }
}
