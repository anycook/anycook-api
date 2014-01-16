exec { "apt-get update" :
	path => "/usr/bin",
	before => Class["install_mysql"],
}

class install_mysql {
    $user = 'anycook'
    $mysql_schema = "/mysql/anycook_db.sql"

    #include '::mysql::server'


    class { '::mysql::server':
        
        override_options => { 
            'mysqld' => { 
                'bind-address'  => '10.1.0.201',
            },
        },
        service_enabled => true,
        service_manage => true,
        require => Exec["apt-get update"],
    }

    exec { "schema":
        path => "/usr/bin",
        command => "mysql -uroot < ${mysql_schema}",
        require => Class['::mysql::server']

    }

    mysql_user { 'anycook@10.1.0.200':
      ensure    => 'present',
      require   => Class['mysql::server'],
    }

    mysql_grant { 'anycook@10.1.0.200/anycook_db':
      privileges => ['ALL'],
      table     => "anycook_db.*",
      user      => 'anycook@10.1.0.200',
      require   => [Mysql_user['anycook@10.1.0.200']],
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

    mysql_user { 'anycook@10.1.0.202':
      ensure    => 'present',
      require   => Class['mysql::server'],
    }

    mysql_grant { 'anycook@10.1.0.202/anycook_db':
      privileges => ['ALL', 'SELECT'],
      table     => "anycook_db.*",
      user      => 'anycook@10.1.0.202',
      require   => [Mysql_user['anycook@10.1.0.202']],
    }

    #GRANT SELECT ON mysql.proc TO 'user'@'localhost';;
    mysql_grant { 'anycook@10.1.0.200/mysql.proc':
      privileges => ['SELECT'],
      table     => 'mysql.proc',
      user      => 'anycook@10.1.0.200',
      require   => [Mysql_user['anycook@10.1.0.200']],
    }

    mysql_grant { 'anycook@10.1.0.202/mysql.proc':
      privileges => ['SELECT'],
      table     => 'mysql.proc',
      user      => 'anycook@10.1.0.202',
      require   => [Mysql_user['anycook@10.1.0.202']],
    }

    #exec { "testdata":
    #    path => "/usr/bin",
    #          command => "mysql -u${user} -p${password} picapica_cases < ${testData}",
    #          require => Exec["schema"]
    #
    #}


    # Table for old backup data
    mysql_database { 'zombiecooking_db':
      ensure  => 'present',
      charset => 'utf8',
      collate => 'utf8_unicode_ci',
    }

    mysql_grant { 'anycook@10.1.0.202/zombiecooking_db.*':
      privileges => ['SELECT'],
      table     => 'zombiecooking_db.*',
      user      => 'anycook@10.1.0.202',
      require   => [Mysql_database['zombiecooking_db'], Mysql_user['anycook@10.1.0.202']],
    }

}

include install_mysql