libraries {
  maven
  sonarqube
  ansible
}

stages {
  continuous_integration {
    build
    scan
  }
}

application_environments {
    dev {
        hosts = [
          "devhost1",
          "devhost2"
        ]
    }
    prod {
        longName = "Production"
        hosts = [
          "prodhost1",
          "prodhost2"
        ]
    }
}
