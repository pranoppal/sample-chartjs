kubectl get secret my-tls-secret -o jsonpath="{.data.tls\.key}" | base64 --decode | openssl rsa -check
