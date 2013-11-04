anycook-api
===========

REST Api for anycook 

More soon!

## Test environment
### Required dependencies
- Install [Java7 JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
- Install [Gradle](http://www.gradle.org/)
- Install [Virtual Box](https://www.virtualbox.org/)
- Install [Vagrant](http://www.vagrantup.com/) (Having problems with vagrant in OSX Mavericks? [see](http://www.asquera.de/development/2013/06/20/vagrant-on-mavericks/))
- Execute ```$ git submodule init``` to download required puppet modules. These are:
  - [puppetlabs/mysql](https://forge.puppetlabs.com/puppetlabs/mysql)
  - [puppetlabs/apt](https://forge.puppetlabs.com/puppetlabs/apt)
  - and their dependency [puppetlabs/stdlib](https://forge.puppetlabs.com/puppetlabs/stdlib)
- Clone [anycook-core](https://github.com/anycook/anycook-core) into the same parent directory

### Starting
- Compile Java sources with ```$ gradle war``
- Run ```$ vagrant up``` in project root directory
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
