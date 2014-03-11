Vagrant.configure("2") do |config|

  config.vm.define "api", primary: true do |api|
    api.vm.box = "precise64"
    api.vm.box_url = "http://files.vagrantup.com/precise64.box"

    api.vm.provider :virtualbox do |vb|
    #   vb.gui = true
        vb.customize ["modifyvm", :id, "--memory", "2048"]
    end

    api.vm.network :private_network, ip: "10.1.0.200"
    api.vm.network "forwarded_port", guest: 80, host: 8080

    api.vm.synced_folder "build/libs", "/war"
    api.vm.synced_folder "images", "/images"

    api.vm.provision :puppet do |puppet|
        puppet.manifests_path = "test-environment/api-manifests"
        puppet.manifest_file  = "init.pp"
        puppet.module_path = "test-environment/puppet-modules"
    end
  end


  config.vm.define "db" do |db|
     db.vm.box = "precise64"
     db.vm.box_url = "http://files.vagrantup.com/precise64.box"

     db.vm.network :private_network, ip: "10.1.0.201"
     db.vm.network "forwarded_port", guest: 3306, host: 3333

     db.vm.synced_folder "doc/mysql", "/mysql"

     db.vm.provision :puppet do |puppet|
        puppet.manifests_path = "test-environment/db-manifests"
        puppet.manifest_file  = "init.pp"
        puppet.module_path = "test-environment/puppet-modules"
     end
  end

end
