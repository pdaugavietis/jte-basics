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
     stage('Deploy artifacts to Nexus') {
       container('maven') {
         sh 'mvn -B -Dskip.tests=true deploy'
       }
     }
   }
 }

//   podTemplate(
//       yaml: yaml,
//       containers: [
//           containerTemplate(name: 'docker', image: 'docker:latest', ttyEnabled: true, command: 'cat')
//       ],
//       volumes: [
//           hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')
//       ],
//       workspaceVolume: persistentVolumeClaimWorkspaceVolume(claimName: 'agent-workspaces', readOnly: false)
//   ) {
//     node(POD_LABEL) {
//       stage('Build docker image') {
//         container('docker') {
//           sh 'ls -l /home/jenkins/agent/workspace'
//           sh 'cat /home/jenkins/agent/workspace/workspaces.txt'
//           sh 'docker build -t spring-boot-demo:latest /home/jenkins/agent/workspace/*gitlab-ci'
//         }
//       }
//     }
//   }
}