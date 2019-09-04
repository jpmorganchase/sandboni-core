echo $GPG_SECRET_KEYS | base64 --decode | $GPG_EXECUTABLE --import
echo $GPG_OWNERTRUST | base64 --decode | $GPG_EXECUTABLE --import-ownertrust

# get project version
export project_version=$(mvn help:evaluate -N -Dexpression=project.version|grep -v '\[')

# build then deploy SNAPSHOT to OSSRH
mvn --settings .maven.xml -B -U clean deploy -DskipTests=true -Prelease