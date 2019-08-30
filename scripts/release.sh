set -e

echo "Ensuring that pom  matches $TRAVIS_TAG"
mvn org.codehaus.mojo:versions-maven-plugin:2.5:set -DnewVersion=$TRAVIS_TAG

echo "Uploading to oss repo and GitHub"
mvn clean deploy --settings .maven.xml -DskipTests=true -Ddependency-check.skip=true --batch-mode --update-snapshots -Prelease