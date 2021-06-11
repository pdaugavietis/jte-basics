void call() {
  def yaml = """
spec:
  securityContext:
      fsGroup: 1000
  containers:
  - name: jnlp
    env:
      - name: HOME
        value: /home/jenkins
    securityContext:
      fsGroup: 1000
      runAsGroup: 1000
      runAsUser: 1000
"""
  podTemplate(
      yaml: yaml,
      containers: [
          containerTemplate(
              envVars: [
                  envVar(key: 'SONAR_HOST_URL', value: 'https://sonarqube.pdaugavietis.staff.adaptavist.com'),
                  envVar(key: 'SONAR_TOKEN', value: '128bf3763faee56f5c5d579a1dff6f857154b1b1')
              ],
              name: 'maven',
              image: 'maven:3.6.3-jdk-11',
              ttyEnabled: true,
              command: 'cat')
      ],
      volumes: [
          persistentVolumeClaim(claimName: 'maven-local-repo', mountPath: '/root/.m2/repository')
      ],
      workspaceVolume: persistentVolumeClaimWorkspaceVolume(claimName: 'agent-workspaces', readOnly: false)
  ) {
    node(POD_LABEL) {
      // stage('Scan with Dependency Scanner') {
      //   container('maven') {
      //     sh "mvn -B dependency-check:check"
      //   }
      // }
      stage('Scan with Sonarqube') {
        container('maven') {
          sh "mvn -B sonar:sonar -Dsonar.dependencyCheck.securityHotspot=true -Dsonar.dependencyCheck.htmlReportPath=target/dependency-check-report.html"
        }
      }
    }
  }
}