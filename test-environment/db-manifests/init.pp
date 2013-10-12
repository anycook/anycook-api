exec { "apt-get update" :
	path => "/usr/bin",
	before => Class["install_mysql"],
}

class install_mysql {
    $user = 'anycook'
    $mysql_schema = "/mysql/anycook_db.sql"

    require mysql


    class { 'mysql::server':
        config_hash => {
            'root_password' => 'foo',
            'bind_address'  => '0.0.0.0'
        },
        require => Exec["apt-get update"],
    }

    exec { "schema":
        path => "/usr/bin",
        command => "mysql -uroot -pfoo < ${mysql_schema}",
        require => Class['mysql::server']

    }

    database_user { 'anycook@10.1.0.200':
      ensure                   => 'present',
    }

    database_grant { 'anycook@10.1.0.200/anycook_db':
      privileges => ['ALL'],
      require   => Exec["schema"],
    }

    #exec { "testdata":
    #    path => "/usr/bin",
    #          command => "mysql -u${user} -p${password} picapica_cases < ${testData}",
    #          require => Exec["schema"]
    #
    #}

}

include install_mysql