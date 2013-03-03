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

* Jenkins-15156 can be fatal problem depending on how Jenkins is used.
* Jenkins 1.505 is not released now.
* Jenkins 1.505 contains not only fix for Jenkins-15156, but other changes. This plugin is especially useful for those who don't want to update Jenkins.

Target Versions
---------------

This plugin tested with Jenkins 1.502.
It may run with older versions of Jenkins, assumed 1.488 and later (the version Jenkins-15156 happened).

How to install
--------------

See [Jenkins update center for ikedam plugins](http://ikedam.github.com/jenkins-update-center/), and follow the instruction to have your Jenkins to access my update center.

Bug Reports
-----------

If you find a problem with this plugin, please report with [github repositry issue tracker](https://github.com/ikedam/quickfix15156/issues).

TODO
----

* Test with Jenkins 1.488.
* Test with Jenkins 1.505.
