/*exec { "apt-get update" :
	path => "/usr/bin",
} */

class { 'apt':
  always_apt_update    => true
}

class anycook {
  file { '/etc/anycook' :
      ensure => directory,
  }

  file { '/etc/anycook/anycook.properties' :
      ensure => present,
      source => "${settings::manifestdir}/anycook.properties",
      #onlyif => 'test -f /etc/anycook/anycook.properties',
      require => File['/etc/anycook'],
      #before => Class['tomcat7'],
  }
}

# Class:  apache2
# install and starts apache2
#
class  apache2 {
	package { "apache2":
		ensure => present,
		require => Class["apt"],
	}

	file { "sendfile.conf":
		ensure => file,
		path => "/etc/apache2/conf-enabled/sendfile.conf",
		require => Package["apache2"],
		source => "${settings::manifestdir}/apache-conf/sendfile.conf",
	}

	file { "charset.conf":
		ensure => file,
		path => "/etc/apache2/conf-enabled/charset.conf",
		require => Package["apache2"],
		source => "${settings::manifestdir}/apache-conf/charset.conf",
	}


	package { "libapache2-mod-jk":
		ensure => present,
		require => Package["apache2"],
	}


	file { "jk.conf":
		ensure => file,
		path => "/etc/apache2/mods-enabled/jk.conf",
		require => Package["libapache2-mod-jk"],
		source => "${settings::manifestdir}/apache-conf/jk.conf",
	}

	file { "worker.properties":
		ensure => file,
		path => "/etc/apache2/workers.properties",
		require => Package["libapache2-mod-jk"],
		source => "${settings::manifestdir}/apache-conf/workers.properties",
	}

	file { "000-default.conf":
        ensure => link,
        force => true,
		path => "/etc/apache2/sites-enabled/000-default.conf",
		require => Package["apache2"],
		target => "${settings::manifestdir}/apache-conf/000-default.conf",

	}

    file { "/localImages":
        ensure => directory,
        owner => "tomcat7",
        source => "/images",
        require => Package["tomcat7"],
        recurse => true,
        mode => '0644',
    }

	service { "apache2":
	    enable => true,
		ensure => running,
		#hasrestart => true,
		#hasstatus => true,
		require => Package["apache2"],
		subscribe => [File["worker.properties"], File["000-default.conf"], File["jk.conf"], File["charset.conf"],
      File["sendfile.conf"]],
	}
}

# Class: java7
#
#
class java8 {
  apt::source { 'oracle-java':
    location   => 'http://ppa.launchpad.net/webupd8team/java/ubuntu',
    release    => 'trusty',
    repos      => ' main',
    key        => 'EEA14886',
    key_server => 'keyserver.ubuntu.com',
    include_src       => false,
  }

  exec { 'oracle-license':
    command => 'echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections',
    path => ['/bin', '/usr/bin'],
    require => Apt::Source['oracle-java'],
    onlyif => 'test -z `debconf-show oracle-java7-installer | grep true | echo`'
  }

	# resources
	package { "oracle-java8-installer":
		ensure => present,
		require => Exec['oracle-license'],
	}
}

# Class: tomcat7
#
#
class tomcat7 {

	# resources
	package { "tomcat7":
		ensure => present,
		require => [Class["java8"], Package["apache2"]],
	}

	file { "server.xml":
		ensure => link,
		force => true,
		path => "/etc/tomcat7/server.xml",
		require => Package["tomcat7"],
		target => "${settings::manifestdir}/tomcat-conf/server.xml",
	}

	file { "setenv.sh":
        ensure => link,
        force => true,
        path => "/usr/share/tomcat7/bin/setenv.sh",
        require => Package["tomcat7"],
        target => "${settings::manifestdir}/tomcat-conf/setenv.sh",
  }

  file { 'tomcat7':
    ensure => link,
    force => true,
    path => '/etc/default/tomcat7',
    require => Package["tomcat7"],
    target => "${settings::manifestdir}/tomcat-conf/tomcat7",
  }

	file { "/var/lib/tomcat7/webapps/ROOT":
		ensure => absent,
  	require => Package["tomcat7"],
    before => File['war'],
		force => true,
	}

	file { "war":
		ensure => link,
		path => "/var/lib/tomcat7/webapps/ROOT.war",
		target => "/war/anycook-api-0.1.0.war",
		force => true,
		require => [Package["tomcat7"]],
	}



	service { "tomcat7":
	    enable => true,
		ensure => running,
		#hasrestart => true,
		#hasstatus => true,
		require => [Package["tomcat7"], File["war"], File["setenv.sh"], File['tomcat7']],
		subscribe => [File["server.xml"], File["war"]],
	}
}

class mongodb {
  apt::source { 'mongodb':
    location   => 'http://downloads-distro.mongodb.org/repo/ubuntu-upstart/',
    release    => 'dist',
    repos      => ' 10gen',
    key        => '7F0CEB10',
    key_server => 'keyserver.ubuntu.com',
    include_src       => false,
  }

  package { "mongodb-10gen":
      ensure => installed,
      require => Apt::Source["mongodb"],
  }

  service { "mongodb" :
      enable => true,
      ensure => running,
      require => Package["mongodb-10gen"],
  }
}



class install_mysql {
  $user = 'anycook'
  $mysql_schema = "/mysql/anycook_db.sql"

#include '::mysql::server'


  class { '::mysql::server':

    override_options => {
      'mysqld' => {
        'bind-address'  => '0.0.0.0',
      },
    },
    service_enabled => true,
    service_manage => true,
    restart => true,
    require => Class["apt"],
  }

  mysql_database { 'anycook_db':
    ensure  => 'present',
    charset => 'utf8',
    collate => 'utf8_unicode_ci'
  }

  exec { "schema":
    path => "/usr/bin",
    command => "mysql -uroot anycook_db < ${mysql_schema}",
    require => Class['::mysql::server']

  }

  mysql_user { 'anycook@localhost':
    ensure    => 'present',
    require   => Mysql_database['anycook_db'],
  }

  mysql_user { 'root@%':
    ensure    => 'present',
    require   => Class['mysql::server'],
  }

  mysql_grant { 'root@%/*.*':
    privileges => ['ALL'],
    table     => "*.*",
    user      => 'root@%',
    require   => [Mysql_user['root@%']],
  }

  mysql_grant { 'anycook@localhost/anycook_db':
    privileges => ['ALL', 'SELECT'],
    table     => "anycook_db.*",
    user      => 'anycook@localhost',
    require   => [Mysql_user['anycook@localhost']],
  }

  mysql_grant { 'anycook@localhost/mysql.proc':
    privileges => ['SELECT'],
    table     => 'mysql.proc',
    user      => 'anycook@localhost',
    require   => [Mysql_user['anycook@localhost']],
  }
}

include apache2
include java8
include anycook
include tomcat7
include mongodb
include install_mysql