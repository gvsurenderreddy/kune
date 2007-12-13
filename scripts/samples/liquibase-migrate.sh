#java -jar $M2_REPO/org/liquibase/liquibase-core/1.4.1/liquibase-core-1.4.1.jar \
#      --driver=com.mysql.jdbc.Driver \
#      --classpath=/home/vjrj/.m2/repository/mysql/mysql-connector-java/5.0.5/mysql-connector-java-5.0.5.jar \
#      --changeLogFile=src/main/resources/db/liquibase_changelog.xml \
#      --url="jdbc:mysql://localhost/kune_dev" \
#      --username=kune \
#      --password=db4kune \
#      migrate

java -jar $M2_REPO/org/liquibase/liquibase-core/1.4.1/liquibase-core-1.4.1.jar \
  --defaultsFile=src/main/resources/db/liquibase.properties \
  migrate