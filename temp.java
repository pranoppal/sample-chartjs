replicaCount: 2

image:
  repository: myregistry.azurecr.io/my-springboot-app
  tag: latest
  pullPolicy: IfNotPresent

service:
  type: LoadBalancer
  port: 8080

env:
  - name: SPRING_RABBITMQ_HOST
    value: my-rabbitmq
  - name: SPRING_RABBITMQ_PORT
    value: "5672"
  - name: SPRING_RABBITMQ_USERNAME
    valueFrom:
      secretKeyRef:
        name: my-rabbitmq
        key: rabbitmq-username
  - name: SPRING_RABBITMQ_PASSWORD
    valueFrom:
      secretKeyRef:
        name: my-rabbitmq
        key: rabbitmq-password
