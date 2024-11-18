#!/bin/bash

timestamp=$(date +'%Y-%m-%d %H:%M:%S,%3N')

echo "[$timestamp] INFO - creating kafka topics..."
kafka-topics --create --topic LOJA_NOVO_PEDIDO \
  --replication-factor 1 \
  --partitions 1 \
  --bootstrap-server kafka-broker:9093

kafka-topics --create --topic ECOMMERCE_NEW_ORDER \
  --replication-factor 1 \
  --partitions 1 \
  --bootstrap-server kafka-broker:9093
echo "[$timestamp] INFO - kafka topics created!"