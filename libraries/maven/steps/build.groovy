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
          containerTemplate(name: 'maven', image: 'maven:3.6.3-jdk-11', ttyEnabled: true, command: 'cat')
      ],
      volumes: [
          persistentVolumeClaim(claimName: 'maven-local-repo', mountPath: '/root/.m2/repository')
      ],
      workspaceVolume: persistentVolumeClaimWorkspaceVolume(claimName: 'agent-workspaces', readOnly: false)
  ) {

    node(POD_LABEL) {
      stage('Get a Maven project') {
        checkout scm
        stage('Compile a Maven project') {
          container('maven') {
            sh 'mvn -B -Dskip.tests=true clean compile'
          }
        }
      }
    }
  }
}