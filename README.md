# CALL CONTROLLER

### _Current version - OMNIBUS_

## Steps to run the application

### Code Checkout

- Use git cli to checkout the specific RELEASE

```bash
git clone https://git-codecommit.ap-south-1.amazonaws.com/v1/repos/callcontroller --branch omnibus
```

### Properties File
- Prepare the `global.properties` file and export the file location to the below environment variable
```bash
export APP_GLOBAL_PATH={PROPERTY FILE LOCATION}
```
- Configure the server IPs for the host names given in the above properties file
```bash
vim /etc/hosts
```
### Installation

- Use the below command to generate the .war file

```bash
 mvn clean package -DskipTests -Dmaven.test.skip -Pproduction
```
- .war file gets created in the following folder
 `{APPLICATION FOLDER}/target/MessageServer.war`

- Deploy the generated .war file into tomcat/jetty container as OCCDV2.war
 `/opt/deplopy/OCCDV2.war`

- Start the server by running the following command

```bash
sh /opt/jettyCC/jetty-base/run.sh
```
