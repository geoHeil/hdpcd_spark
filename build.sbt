organization := "com.github.geoheil"
scalaVersion := "2.11.12"
name := "hdpcd_spark"

lazy val sparkVersion = "1.6.3"
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")
scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding",
  "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  "-Xlint:missing-interpolator",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-dead-code",
  "-Ywarn-unused"
)
parallelExecution in Test := false
fork := true

coverageHighlighting := true

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core"         % sparkVersion  % "provided",
  "org.apache.spark" %% "spark-sql"          % sparkVersion  % "provided",
  "org.apache.spark" %% "spark-hive"         % sparkVersion  % "provided",
  "org.apache.spark" %% "spark-streaming"    % sparkVersion  % "provided",
  "org.scalatest"    %% "scalatest"          % "3.0.4"       % "test",
  "org.scalacheck"   %% "scalacheck"         % "1.13.5"      % "test",
  "com.holdenkarau"  %% "spark-testing-base" % "1.6.3_0.8.0" % "test"
)

// uses compile classpath for the run task, including "provided" jar (cf http://stackoverflow.com/a/21803413/3827)
fullClasspath in reStart := (fullClasspath in Compile).value
run in Compile := Defaults
  .runTask(fullClasspath in Compile, mainClass.in(Compile, run), runner.in(Compile, run))
  .evaluated

// pomIncludeRepository := { x => false },
publishMavenStyle := true

resolvers ++= Seq(
  "Artima Maven Repository" at "http://repo.artima.com/releases",
  "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
  Resolver.sonatypeRepo("public")
)

// publish settings
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

scalafmtOnCompile := true
scalafmtTestOnCompile := true
scalafmtVersion := "1.4.0"

wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Throw,
                                                           Wart.Any,
                                                           Wart.PublicInference,
                                                           Wart.NonUnitStatements,
                                                           Wart.DefaultArguments,
                                                           Wart.ImplicitParameter,
                                                           Wart.Nothing)

assemblyJarName in assembly := name.value + ".jar"
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("rootdoc.txt")             => MergeStrategy.discard
  case _                                   => MergeStrategy.deduplicate
}
assemblyShadeRules in assembly := Seq(ShadeRule.rename("shapeless.**" -> "new_shapeless.@1").inAll)
test in assembly := {}

//initialCommands in console :=
//"""
//|import spark.implicits._
//""".stripMargin
