class install_mysql {
    $user = 'anycook'
    $mysql_schema = "/mysql/anycook_db.sql"

    require mysql

    class { 'mysql::server':
        config_hash => { 'root_password' => 'foo' },
    }

    exec { "schema":
        path => "/usr/bin",
              command => "mysql -uroot -pfoo < ${mysql_schema}",

    }

    #exec { "testdata":
    #    path => "/usr/bin",
    #          command => "mysql -u${user} -p${password} picapica_cases < ${testData}",
    #          require => Exec["schema"]
    #
    #}

}

include install_mysql