libraries {
  maven
  sonarqube
}

stage {
  continuous_integration {
    build
    scan
  }
}
