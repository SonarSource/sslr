#!/bin/bash

set -euo pipefail

function installTravisTools {
  mkdir -p ~/.local
  curl -sSL https://github.com/SonarSource/travis-utils/tarball/v56 | tar zx --strip-components 1 -C ~/.local
  source ~/.local/bin/install
}
installTravisTools

# https://github.com/SonarSource/sonarqube/blob/6.4-RC3/travis.sh#L57-L110
function fixBuildVersion {
  export INITIAL_VERSION=`maven_expression "project.version"`

  # remove suffix -SNAPSHOT or -RC
  without_suffix=`echo $INITIAL_VERSION | sed "s/-.*//g"`

  IFS=$'.'
  fields_count=`echo $without_suffix | wc -w`
  unset IFS
  if [ $fields_count -lt 3 ]; then
    export BUILD_VERSION="$without_suffix.0.$TRAVIS_BUILD_NUMBER"
  else
    export BUILD_VERSION="$without_suffix.$TRAVIS_BUILD_NUMBER"
  fi

  if [[ "${INITIAL_VERSION}" == *"-SNAPSHOT" ]]; then
    # SNAPSHOT
    export PROJECT_VERSION=$BUILD_VERSION
    mvn org.codehaus.mojo:versions-maven-plugin:2.2:set -DnewVersion=$PROJECT_VERSION -DgenerateBackupPoms=false -B -e
  else
    # not a SNAPSHOT: milestone, RC or GA
    export PROJECT_VERSION=$INITIAL_VERSION
  fi

  echo "Build Version  : $BUILD_VERSION"
  echo "Project Version: $PROJECT_VERSION"
}

if [ "${TRAVIS_BRANCH}" == "master" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  git fetch --unshallow || true

  fixBuildVersion

  mvn org.jacoco:jacoco-maven-plugin:prepare-agent deploy sonar:sonar \
      -Pcoverage-per-test,deploy-sonarsource,release \
      -Dsonar.host.url=$SONAR_HOST_URL \
      -Dsonar.login=$SONAR_TOKEN \
      -Dsonar.projectVersion=$INITIAL_VERSION \
      -B -e -V

else
  regular_mvn_build_deploy_analyze
fi
