## Start-Code for 3 semester 

### Technologies used
- Jdk 17
- Maven 3.8.8
- Tomcat 10.1.8

### First time setup

1. Clone this repository
2. Delete the .git folder
3. Create a new repository on github with the new project name
4. Change name and artifactId in pom.xml to the new project name
5. Change the current project name to the new project name from step 3.
6. Install Tomcat 10.1.8
7. Add the tomcat server to your project
8. Create a .env file in the root of the project
9. Add the following to the .env file
```
SECRET_KEY=add your secret key here (token)
TOKEN_EXPIRE_TIME=1800000 
ISSUER= add your token issuer here
DB_USERNAME= add your database username here
DB_PASSWORD= add your database password here
```
10. Create a new database in your local mysql server
11. Add the database name to the pom file under the properties tag
12. Add the tomcat manager url to the pom file under the properties tag. The url should look like this: https://your-domain.dk/manager/text
13. Remember to add the tomcat manager username and password to the environment variables on github.

### How to run
1. Start tomcat server
2. Use the rest.http file to test the endpoints

