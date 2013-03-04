Quickfix Jenkins-15156 plugin
=============================

Quickfix plugin for [Jenkins-15156](https://issues.jenkins-ci.org/browse/JENKINS-15156).

What's this?
------------

This plugin is quick fix for [Jenkins-15156](https://issues.jenkins-ci.org/browse/JENKINS-15156), "Builds disappear from build history after completion".

How does this work?
-------------------

Jenkins-15156 is caused for MatrixConfiguration is not properly initialized when newly created.
For this plugin force to initialize MatrixConfiguration.
It is triggered when MatrixProject is created (that is, when copied) or updated (that is, when new axe added).

Why this plugin?
----------------

Jenkins-15156 will be fixed with [42ee6113b7beff8dccba48593cefe0ad23636051] (https://github.com/jenkinsci/jenkins/commit/42ee6113b7beff8dccba48593cefe0ad23636051), which seems to be included in Jenkins 1.505.
But there are some problems:

* Jenkins-15156 can be very annoying problem depending on how Jenkins is used.
* Jenkins 1.505 is not released now.
* Jenkins 1.505 contains not only fix for Jenkins-15156, but other changes. This plugin is especially useful for those who don't want to update their Jenkins.

How to reproduce Jenkins-15156
------------------------------

There is no way to surely reproduce Jenkins-15156.
But it often happens in the following steps:

1. Create a matrix project, and add some axe.
2. Copy it and create some matrix projects.
3. Run all the matrix projects.
4. Open Manage Jenkins > Script Console, and execute System.gc()
5. Some builds are lost. They recover when Jenkins reloads the configuration.

Target Versions
---------------

Jenkins-15156 happens in 1.485 and later.
This plugin tested with Jenkins 1.485, 1.487, 1.502.

Jenkins 1.485 have the bug that builds of any new created project disapper.
This plugin does not care that bug.

How to install
--------------

See [Jenkins update center for ikedam plugins](http://ikedam.github.com/jenkins-update-center/), and follow the instruction to have your Jenkins to access my update center.

Bug Reports
-----------

If you find a problem with this plugin, please report with [github repositry issue tracker](https://github.com/ikedam/quickfix15156/issues).

TODO
----

* Test with Jenkins 1.505 (see that this plugin does not work at all).
