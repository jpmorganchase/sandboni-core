set -e

if [ ! -z "$TRAVIS_TAG" ]
then
    echo "Git tag detected - set Maven POM version to [$TRAVIS_TAG]"
    mvn versions:set -DnewVersion=$TRAVIS_TAG --settings $DEPLOY_DIR/maven/settings.xml
else
    echo "No Git tag detected - keep Maven POM version untouched"
fi

mvn clean deploy -Prelease -DskipTests=true --settings .maven.xml -B -U