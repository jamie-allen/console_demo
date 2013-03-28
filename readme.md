# Console Test
Consider this a QuickStart guide for getting up and running with the Typesafe Console.  This repo contains a very simple little Akka application I built to get a feel for how to integrate the Console.  Feel free to copy and use anything you like in trying to get similar results with your own application.

Until the console-instrumented versions of the Akka JARs are made generally available (which should happen in the next few weeks), you must have the appropriate sbt credentials to access the repo and download them.  Credentials belong in an `atmos.credentials` file in your user home folder with this layout:

    realm=Artifactory Realm
    host=repo.typesafe.com
    user=xxxx
    password=xxxx

You must have the atmos and console application scripts to run the analytics collector and console locally.  I'm still not sure from where the general public will be able to access those, but we will definitely make them available when the Console is made generally available.

# How to use it with your application
* [Install Mongo 2.0.x](http://www.mongodb.org/downloads) (using 2.2.x will result in a ClassCastException converting a java.lang.Boolean to a java.lang.Integer or some such nonsense).
* Copy my [build.sbt](https://github.com/jamie-allen/console_demo/blob/master/build.sbt) resolvers and libraryDependencies definitions to fix transitive dependency issues.  Before running `sbt update`, make sure that you've configured your `atmos.credentials` file as shown above.
* Edit your `application.conf` to have the atmos tracing defined as shown in [mine](https://github.com/jamie-allen/console_demo/blob/master/src/main/resources/application.conf).
* Start your application.
* Start the bin/atmos script (./atmos -query -analyze -remote-collector).
* Start the bin/console script.
* Navigate your browser to `http://localhost:9000/`.  You will redirected to some `demo@typesafe.com` URL, and select the "Demo" application.
* The node panel may take a bit to appear, give it time.  Select what you want to see using the Scope drop-down - ActorSystem, Dispatcher or Actors.

Note that you may want to view the output without the Console.  This is possible by using a MongoDB viewer like [MongoHub](https://github.com/bububa/MongoHub-Mac), which is a pretty nice graphical tool.  Or you can use `curl` commands, like this: `curl http://127.0.0.1:28017/monitoring/metadata/nodes`.  See [this link](http://console-demo.typesafe.com:9898/monitoring/api.html) for more information about the RESTful URIs created.

Also, note that MongoDB will allow you to use `curl` commands, but you cannot use the 27017 port through which you connected MongoHub.  Mongo will tell you to add 1000 to the port number, which is why my `curl` command above uses port 28017. 
