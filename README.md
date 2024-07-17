# MyLocalTon

This is a fork of the MyLocalTon project, which adds ability to create a funded wallet in a headless mode.
To create a wallet on startup, provide `mnemonic` param when starting the application, e.g.:
```bash
java -Dmnemonic="..." -jar mylocalton.jar nogui
```

A funded V3R2 wallet will be created for the given mnemonic, in the default workchain (0) and with the default subwallet
ID (698983191), as defined in the `myLocalTon/settings.json` file. Wallet's raw address will be visible in the startup logs.

## Build for linux/amd64 on a Mac

To build a linux/amd64 jar on ARM Mac, use the provided Docker image:
```bash
docker build -t mylocalton --platform linux/amd64 .
```

The resulting image can then be used to run MyLocalTon on Docker:
```bash
docker run mylocalton # No user-wallet
docker run mylocalton java -Dmnemonic="..." -jar /opt/app/mylocalton.jar nogui # Create user-wallet on startup
```

Or the resulting jar can be extracted from the image by running:
```bash
docker cp $(docker create --name mylocalton mylocalton):/opt/app/mylocalton.jar ./mylocalton.jar && docker rm mylocalton
```
