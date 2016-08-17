node {
    def gradleHome = tool 'gradle3'
    env.PATH="${env.PATH}:${gradleHome}/bin"

    stage 'Checkout'
    checkout scm

    stage 'Test'
    sh 'gradle test'

    stage 'Build'
    sh 'gradle war'

    step([$class: 'ArtifactArchiver', artifacts: 'build/libs/*.war', fingerprint: true])

    if (env.BRANCH_NAME == 'master') {
        stage 'Deploy'
        sshagent(credentials: ['deeeb519-0366-4e72-9e1e-caf3d3e05f97']) {
            sh 'scp -o StrictHostKeyChecking=no build/libs/*.war jenkins@chef.gesundkrank.de:/srv/tomcat/anycook-api.war'
        }
    }
}