# Console Test
This is a repo where I was playing around with using the Typesafe Console with my own sample application, to get a feel for what was involved in getting it up and running.  Feel free to copy and use anything you like in trying to get similar results with your own application.

# How to use it with your application
* Install Mongo 2.0.x (using 2.2.x will result in a ClassCastException converting a java.lang.Boolean to a java.lang.Integer or some such nonsense).
* Copy my [build.sbt](https://github.com/jamie-allen/console_demo/blob/master/build.sbt) resolvers and libraryDependencies definitions to fix transitive dependency issues.
* Edit your application.conf to have the atmos tracing defined as shown in [mine](https://github.com/jamie-allen/console_demo/blob/master/src/main/resources/application.conf).
* Start your application.
* Start the bin/atmos script.
* Start the bin/console script.
* Navigate your browser to http://localhost:9000/.  You will redirected to some demo@typesafe.com URL, and select the "Demo" application.
* The node panel may take a bit to appear, give it time.  Select what you want to see using the Scope drop-down - ActorSystem, Dispatcher or Actors.