/*exec { "apt-get update" :
	path => "/usr/bin",
} */

class { 'apt':
  always_apt_update    => true,
}

# Class:  apache2
# install and starts apache2
#
class  apache2 {
	package { "apache2":
		ensure => present,
		require => Class["apt"],
	}

	file { "sendfile":
		ensure => file,
		path => "/etc/apache2/conf.d/sendfile",
		require => Package["apache2"],
		source => "/tmp/vagrant-puppet/manifests/apache-conf/sendfile",
	}

	file { "charset":
		ensure => file,
		path => "/etc/apache2/conf.d/charset",
		require => Package["apache2"],
		source => "/tmp/vagrant-puppet/manifests/apache-conf/charset",
	}


	package { "libapache2-mod-jk":
		ensure => present,
		require => Package["apache2"],
	}


	file { "jk.conf":
		ensure => file,
		path => "/etc/apache2/mods-enabled/jk.conf",
		require => Package["libapache2-mod-jk"],
		source => "/tmp/vagrant-puppet/manifests/apache-conf/jk.conf",
	}

	file { "worker.properties":
		ensure => file,
		path => "/etc/apache2/workers.properties",
		require => Package["libapache2-mod-jk"],
		source => "/tmp/vagrant-puppet/manifests/apache-conf/workers.properties",
	}

	file { "000-default":
		ensure => file,
		path => "/etc/apache2/sites-enabled/000-default",
		require => Package["apache2"],
		source => "/tmp/vagrant-puppet/manifests/apache-conf/000-default",

	}

	file { "public":
		ensure => link,
		path => "/var/www/picapica-landingpage",
		require => Package["apache2"],
		target => "/public",
		force => true,
	}

	service { "apache2":
	    enable => true,
		ensure => running,
		#hasrestart => true,
		#hasstatus => true,
		require => Package["apache2"],
		subscribe => [File["worker.properties"], File["000-default"], File["jk.conf"], File["charset"], File["sendfile"]],
	}
}

# Class: java7
#
#
class java7 {
	# resources
	package { "openjdk-7-jdk":
		ensure => present,
		require => Class["apt"],
	}
}

# Class: tomcat7
#
#
class tomcat7 {

	# resources
	package { "tomcat7":
		ensure => present,
		require => [Class["java7"], Package["apache2"]],
	}

	file { "server.xml":
		ensure => link,
		force => true,
		path => "/etc/tomcat7/server.xml",
		require => Package["tomcat7"],
		target => "/tmp/vagrant-puppet/manifests/tomcat-conf/server.xml",
	}

	file { "setenv.sh":
        ensure => link,
        force => true,
        path => "/usr/share/tomcat7/bin/setenv.sh",
        require => Package["tomcat7"],
        target => "/tmp/vagrant-puppet/manifests/tomcat-conf/setenv.sh",
    }

	file { "/var/lib/tomcat7/webapps/ROOT":
		ensure => absent,
		require => Package["tomcat7"],
		force => true,
	}

	file { "war":
		ensure => link,
		path => "/var/lib/tomcat7/webapps/ROOT.war",
		target => "/war/anycook-api-0.1.0.war",
		force => true,
		require => [Package["tomcat7"], File["/var/lib/tomcat7/webapps/ROOT"]],
	}

    file { "/images":
        ensure => directory,
        owner => "tomcat7",
        recurse => true,
        require => Package["tomcat7"],

    }

	service { "tomcat7":
	    enable => true,
		ensure => running,
		#hasrestart => true,
		#hasstatus => true,
		require => [Package["tomcat7"], File["war"], File["setenv.sh"]],
		subscribe => [File["server.xml"], File["war"]],
	}
}

class glassfish {
  file { "war":
    ensure => link,
    path => "/home/vagrant/glassfish4/glassfish/domains/domain1/autodeploy/anycook-api.war",
    target => "/war/anycook-api-0.1.0.war",
    force => true,
    #require => [Package["tomcat7"], File["/var/lib/tomcat7/webapps/ROOT"]],
  }

  exec{ "glassfish":
      command => "asadmin start-domain",
      path => "/home/vagrant/glassfish4/glassfish/bin",

  }

}




# Class: sass
#
#
class sass {
	package { "sass":
		ensure => installed,
		provider => 'gem',
		
	}

	exec { "sass --watch scss:css &":
		require => Package["sass"],
		path => "/opt/vagrant_ruby/bin/",
		cwd => "/var/www"
		#hasrestart => true,
		#hasstatus => true,
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

include apache2
include java7
include tomcat7
include mongodb
#include glassfish
#include sass

