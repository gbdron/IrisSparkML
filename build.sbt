name := "IrisSparkML"

version := "0.1"

scalaVersion := "2.12.18"

libraryDependencies += "org.apache.spark" %% "spark-core" % "3.5.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.1"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "3.5.1"


fork in run := true

javaOptions in run ++= Seq(
  "--add-exports", "java.base/sun.nio.ch=ALL-UNNAMED"
)