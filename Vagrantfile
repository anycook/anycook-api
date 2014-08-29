Vagrant.configure("2") do |config|

    config.vm.box = "ubuntu-14.04"
    config.vm.box_url = "https://oss-binaries.phusionpassenger.com/vagrant/boxes/latest/ubuntu-14.04-amd64-vbox.box"

    config.vm.provider :virtualbox do |vb|
    #   vb.gui = true
        vb.customize ["modifyvm", :id, "--memory", "2048"]
    end

    config.vm.network :private_network, ip: "10.1.0.200"
    config.vm.network "forwarded_port", guest: 80, host: 8080
    config.vm.network "forwarded_port", guest: 3306, host: 3333

    config.vm.synced_folder "build/libs", "/war"
    config.vm.synced_folder "images", "/images"
    config.vm.synced_folder "doc/mysql", "/mysql"

    config.vm.provision :puppet do |puppet|
        puppet.manifests_path = "test-environment/manifests"
        puppet.manifest_file  = "init.pp"
        puppet.module_path = "test-environment/puppet-modules"
    end

end
