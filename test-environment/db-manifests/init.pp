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
                'bind_address'  => '0.0.0.0',
            },
        },
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
      require   => [Exec["schema"], Mysql_user['anycook@10.1.0.200']],
    }

    mysql_user { 'anycook@10.1.0.202':
      ensure    => 'present',
      require   => Class['mysql::server'],
    }

    mysql_grant { 'anycook@10.1.0.202/anycook_db':
      privileges => ['ALL'],
      table     => "anycook_db.*",
      user      => 'anycook@10.1.0.202',
      require   => [Exec["schema"], Mysql_user['anycook@10.1.0.202']],
    }

    #exec { "testdata":
    #    path => "/usr/bin",
    #          command => "mysql -u${user} -p${password} picapica_cases < ${testData}",
    #          require => Exec["schema"]
    #
    #}

}

include install_mysql