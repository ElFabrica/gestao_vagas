docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:9.9.0-community

mvn clean verify sonar:sonar \
-Dsonar.projectKey=gestao_vagas \
-Dsonar.host.url=http://localhost:9000 \
-Dsonar.login=sqp_354026640f6580bdae9f5a1319be484daf13855b