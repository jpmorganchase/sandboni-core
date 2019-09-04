git checkout $TRAVIS_BRANCH || travis_terminate 1
release_version=`mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec`
branch_name="release-${release_version}"

# prepare current branch for release, create release branch, increment development version on current branch
mvn --settings .maven.xml -B -DbranchName=${branch_name} release:branch -Dusername=${GITHUB_USERNAME} -Dpassword=${GITHUB_TOKEN} || travis_terminate 1
git checkout $branch_name || travis_terminate 1

# remove SNAPSHOT version suffix, create a tag in GitHub, increment development version on release branch
mvn --settings .maven.xml -B -DpushChanges=true release:prepare -Dusername=${GITHUB_USERNAME} -Dpassword=${GITHUB_TOKEN} || travis_terminate 1

# check out tag from GitHub, build then deploy to OSSRH
echo $GPG_SECRET_KEYS | base64 --decode | gpg --import
echo $GPG_OWNERTRUST | base64 --decode | gpg --import-ownertrust
mvn --settings .maven.xml -B release:perform || travis_terminate 1