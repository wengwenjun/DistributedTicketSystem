lazy val root = (project in file(".")).
settings (
  name := "Wenjun’s Distributed Ticket System",
  version := "1.0",
  scalaVersion := "2.11.8",
  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.3",
  libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.4.3"
)


