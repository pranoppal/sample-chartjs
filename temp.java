kubectl exec -it <rabbitmq-pod> -- rabbitmqctl add_user <your-username> <your-password>
kubectl exec -it <rabbitmq-pod> -- rabbitmqctl set_user_tags <your-username> administrator
kubectl exec -it <rabbitmq-pod> -- rabbitmqctl set_permissions -p / <your-username> ".*" ".*" ".*"
