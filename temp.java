az webapp show --name myWebApp --resource-group myResourceGroup --query "siteConfig.linuxFxVersion"
az webapp config set --name myWebApp --resource-group myResourceGroup --linux-fx-version "DOCKER|myacr.azurecr.io/myapp:latest"
