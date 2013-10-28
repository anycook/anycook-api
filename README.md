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
  - and its dependency [puppetlabs/stdlib](https://forge.puppetlabs.com/puppetlabs/stdlib)
- Clone [anycook-core](https://github.com/anycook/anycook-core) into the same parent directory


### Starting
- Compile Java sources with ```$ gradle war``
- Run ```$ vagrant up``` in project root directory
- The anycook API can be accessed on [http://localhost:8080](http://localhost:8080)
- The MySQL server via port 3333
