scalaVersion := "2.9.2"

name := "Console Playground"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-optimize")

resolvers ++= Seq("akka-snapshots" at "http://repo.akka.io/snapshots",
                  "akka-releases"  at "http://repo.akka.io/releases",
                  "Atmos Repo" at "http://repo.typesafe.com/typesafe/atmos-releases",
                  "Cloudera Repo" at "https://repository.cloudera.com/content/groups/public",
                  "OpenCastProject Repo" at "http://repository.opencastproject.org/nexus/content/repositories/terracotta",
                  "JBoss Repo" at "https://repository.jboss.org/nexus/content/groups/public",
                  "releases"  at "http://oss.sonatype.org/content/repositories/releases")

externalResolvers <<= resolvers map { rs =>
  Resolver.withDefaultResolvers(rs, mavenCentral = false)
}
                  
credentials += Credentials(Path.userHome / "atmos.credentials")

libraryDependencies ++= Seq(
                  "com.typesafe.atmos" % "atmos-akka-actor" % "2.0.3",
                  "com.typesafe.atmos" % "atmos-akka-remote" % "2.0.3",
                  "com.typesafe.atmos" % "atmos-akka-slf4j" % "2.0.3",
                  "com.typesafe.atmos" % "atmos-akka-kernel" % "2.0.3",
                  "org.fusesource" % "sigar" % "1.6.4",
                  "javax.jms" % "jms" % "1.1",
                  "com.typesafe.akka" % "akka-testkit" % "2.0.3" % "test",
                  "org.scalatest" % "scalatest_2.9.2" % "1.8" % "test",
                  "junit" % "junit" % "4.7" % "test"
		)

