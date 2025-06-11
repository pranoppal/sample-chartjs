# Extract modulus from private key
openssl pkey -in key.pem -pubout -outform PEM | openssl rsa -pubin -modulus -noout

# Extract modulus from certificate
openssl x509 -in cert-chain.pem -modulus -noout
