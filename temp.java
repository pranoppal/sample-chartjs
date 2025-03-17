controller:
  service:
    enabled: true

ingress:
  enabled: true
  ingressClassName: nginx  # Ensure you have an Ingress Controller (e.g., NGINX)
  hosts:
    - host: my-app.example.com  # Change to your domain or external IP
      paths:
        - path: /api
          pathType: Prefix
          backend:
            service:
              name: spring-boot-service
              port: 8080  # Port of your Spring Boot app
        - path: /rabbitmq
          pathType: Prefix
          backend:
            service:
              name: rabbitmq-service
              port: 15672  # RabbitMQ Management UI
