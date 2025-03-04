apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
      - name: my-app
        image: <ACR_NAME>.azurecr.io/<YOUR_IMAGE>:latest  # Change this to your image
        ports:
        - containerPort: 80
