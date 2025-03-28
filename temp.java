kubectl logs -n <ingress-namespace> -l app.kubernetes.io/name=ingress-nginx --tail=100 -f
