Vagrant.configure("2") do |config|

  config.vm.define "api", primary: true do |api|
    api.vm.box = "precise64"
    api.vm.box_url = "http://files.vagrantup.com/precise64.box"

    api.vm.provider :virtualbox do |vb|
    #   vb.gui = true
        vb.customize ["modifyvm", :id, "--memory", "1024"]
    end

    api.vm.network :private_network, ip: "54.228.218.83"

    api.vm.synced_folder "build/libs", "/war"

    api.vm.provision :puppet do |puppet|
        puppet.manifests_path = "test-environment/api-manifests"
        puppet.manifest_file  = "init.pp"
    end
  end


  config.vm.define "db" do |db|
     db.vm.box = "precise64"
     db.vm.box_url = "http://files.vagrantup.com/precise64.box"

     db.vm.network :private_network, ip: "54.228.12.221"

     db.vm.synced_folder "../anycook-core/doc/mysql", "/mysql"

     db.vm.provision :puppet do |puppet|
        puppet.manifests_path = "test-environment/db-manifests"
        puppet.manifest_file  = "init.pp"
        puppet.module_path = "test-environment/db-puppet-modules"
     end
  end

end
