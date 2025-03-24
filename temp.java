kubectl exec -it $(kubectl get pods -n your-namespace -l app.kubernetes.io/name=rabbitmq -o jsonpath="{.items[0].metadata.name}") -n your-namespace -- rabbitmq-plugins list
