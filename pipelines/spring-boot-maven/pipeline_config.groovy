libraries {
  java_maven
  sonarqube
}

stage {
  continuous_integration {
    build
    test
    scan
  }
}