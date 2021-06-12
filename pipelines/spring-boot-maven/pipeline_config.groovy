libraries {
  maven
  sonarqube
}

stages {
  continuous_integration {
    build
    scan
  }
}
