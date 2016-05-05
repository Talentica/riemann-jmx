# Mesos Chronos Configuration

Chronos Jobs for collecting events can be added using following CLI:

```
curl -v -X POST -H 'Content-Type: application/json' -d @kafka-riemann-jmx-events-000.json http://172.28.128.4:4400/scheduler/iso8601

curl -v -X POST -H 'Content-Type: application/json' -d @kafka-riemann-jmx-events-001.json http://172.28.128.4:4400/scheduler/iso8601

curl -v -X POST -H 'Content-Type: application/json' -d @kafka-riemann-jmx-events-002.json http://172.28.128.4:4400/scheduler/iso8601

```
