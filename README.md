# ArchaicHopper 5.0

Reconstruction project targeting Java 21 and Paper API 1.21.4, with `folia-supported: true`.

## Build

Run `mvn clean package`. The output is `target/ArchaicHopper-5.0.jar`.

## Important status

This is an initial source scaffold, not a verified drop-in replacement for FoliaHopper 5.0. The batch adjustment uses `InventoryMoveItemEvent`; it must be integration-tested on the exact Folia build before production use. Do not install alongside FoliaHopper, because both plugins would act on hopper transfers.
