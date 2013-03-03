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

import jenkins.model.Jenkins;
import jenkins.model.lazy.AbstractLazyLoadRunMap;

import hudson.matrix.AxisList;
import hudson.matrix.MatrixConfiguration;
import hudson.matrix.MatrixProject;
import hudson.matrix.MatrixRun;
import hudson.matrix.TextAxis;
import hudson.model.RunMap;
import hudson.model.TopLevelItem;
import hudson.model.RunMap.Constructor;

import org.jvnet.hudson.test.Bug;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Tests for Quickfix Jenkins-15156 plugin.
 */
@Bug(15156)
public class Quickfix15156Test extends HudsonTestCase
{
    @SuppressWarnings("unchecked")
    protected Constructor<MatrixRun> getCons(MatrixConfiguration c) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        RunMap<MatrixRun> builds = c._getRuns();
        
        Field consField = RunMap.class.getDeclaredField("cons");
        consField.setAccessible(true);
        return (Constructor<MatrixRun>)consField.get(builds);
    }
    
    protected File getDir(MatrixConfiguration c) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        RunMap<MatrixRun> builds = c._getRuns();
        
        Field dirField = AbstractLazyLoadRunMap.class.getDeclaredField("dir");
        dirField.setAccessible(true);
        return (File)dirField.get(builds);
    }
    
    /**
     * Test whether added configurations are properly configured.
     * @throws IOException 
     * @throws IllegalAccessException 
     * @throws NoSuchFieldException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    @For(Quickfix15156SaveableListener.class)
    public void testOnUpdated() throws IOException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException
    {
        // create a matrix project
        MatrixProject project = createMatrixProject("OnUpdatedTest1");
        AxisList axes = new AxisList(
                new TextAxis("x", "x1", "x2"),
                new TextAxis("y", "y1", "y2")
                );
        project.setAxes(axes); // this causes rebuild configurations, and update the configuration.
        
        for(MatrixConfiguration c: project.getItems())
        {
            assertNotNull("builds for new added matrix configuration must not be null: " + c, c._getRuns());
            assertNotNull("builds.cons for new added matrix configuration must not be null: " + c, getCons(c));
            assertNotNull("builds.dir for new added matrix configuration must not be null: " + c, getDir(c));
            assertTrue("builds.dir is in the matrix job dir: " + c, getDir(c).getPath().startsWith(project.getRootDir().getPath()));
        }
    }
    
    
    /**
     * Test whether copied configurations are properly configured.
     * 
     * @throws IOException 
     * @throws IllegalAccessException 
     * @throws NoSuchFieldException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    @For(Quickfix15156ItemListener.class)
    public void testOnCreated() throws IOException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException
    {
        // create a matrix project
        MatrixProject project = createMatrixProject("OnCreateTest1");
        AxisList axes = new AxisList(
                new TextAxis("x", "x1", "x2"),
                new TextAxis("y", "y1", "y2")
                );
        project.setAxes(axes); // this causes rebuild configurations, and update the configuration.
        
        MatrixProject copiedProject = (MatrixProject)Jenkins.getInstance().copy((TopLevelItem)project, "OnCreateTest2");
        
        
        for(MatrixConfiguration c: copiedProject.getItems())
        {
            assertNotNull("builds for copied matrix configuration must not be null: " + c, c._getRuns());
            assertNotNull("builds.cons for copied matrix configuration must not be null: " + c, getCons(c));
            assertNotNull("builds.dir for copied matrix configuration must not be null: " + c, getDir(c));
            System.out.println("c="+getDir(c).getPath());
            System.out.println("job="+copiedProject.getRootDir().getPath());
            assertTrue("builds.dir is in the matrix job dir: " + c, getDir(c).getPath().startsWith(copiedProject.getRootDir().getPath()));
        }
    }
}
