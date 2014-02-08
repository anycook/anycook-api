anycook-api
===========

REST Api for anycook

[![Build Status](https://travis-ci.org/anycook/anycook-api.png?branch=master)](https://travis-ci.org/anycook/anycook-api)

## Documentation
A detailed API documentation can be found [here](http://docs.anycook.apiary.io/) (still in progress).

## Test environment
To use the full test stack you will also need to clone and start the [anycook-page](https://github.com/anycook/anycook-page) project.

### Required dependencies
- Install [Java7 JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
- Install [Gradle](http://www.gradle.org/). To install it via brew execute ```brew install gradle```.
- Install [Virtual Box](https://www.virtualbox.org/)
- Install [Vagrant](http://www.vagrantup.com/) (Having problems with vagrant in OSX Mavericks? Execute ```sudo /Library/StartupItems/VirtualBox/VirtualBox restart``` and try again ([source](http://www.asquera.de/development/2013/06/20/vagrant-on-mavericks/)))
- Execute ```git submodule init && git submodule update``` in project root to download required submodules. These are:
  - [anycook-core](https://github.com/anycook/anycook-core)
  - [puppetlabs/mysql](https://forge.puppetlabs.com/puppetlabs/mysql)
  - [puppetlabs/apt](https://forge.puppetlabs.com/puppetlabs/apt)
  - [puppetlabs/stdlib](https://forge.puppetlabs.com/puppetlabs/stdlib)
- If you are an anycook internal and want to commit changes to anycook-core change your git remotes (Execute the following command inside the __anycook-core__ folder): ```git remote set-url --push origin git@github.com:anycook/anycook-core.git``

### Configuration
Available properties can be found [here](https://github.com/anycook/anycook-core/wiki/Configuration-File).

If you want to set your own global conf in the test environment place it in the project as ```test-environment/api-manifests/anycook.properties```

### Starting
- Compile Java sources with ```$ gradle war```
- Run ```$ vagrant up --provision``` in project root directory.
- The anycook API can be accessed on [http://localhost:8080](http://localhost:8080). The MySQL server via port 3333

### Adding database data
- The puppet scripts create two MySQL databases. 'anycook_db' is the main database. 'zombiecooking_db' is only created to import old db data.
- You can import a db schema by 
  - using a terminal command: ```$ mysql -P 3333 -u root anycook_db < sql-file.sql```
  - using MySQL Workbench (can be downloaded [here](https://www.mysql.com/products/workbench/))
    1. In the menu 'Database' select 'Connect to Database...'
    2. Change the port to 3333 and click OK
    3. Load the latest testdump. (For anycook internals: Latest dump is located in the root of the BTSync folder)
    4. Click on the lighning symbol
