void call(app_env) {

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
          /* groovylint-disable-next-line LineLength */
          containerTemplate(name: 'dind', image: 'docker:18.05-dind', privileged: true, ttyEnabled: true, command: 'cat')
      ],
      volumes: [
          emptyDirVolume(mountPath: '/var/lib/docker')
      ],
      workspaceVolume: persistentVolumeClaimWorkspaceVolume(claimName: 'agent-workspaces', readOnly: false)
  ) {

    node(POD_LABEL) {
      stage('Build Docker Image') {
        sh "echo from inside: ${docker build --help}"
      }
    }
  }

}