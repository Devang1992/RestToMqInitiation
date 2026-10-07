# PCSS enrichment flow with state store retry

Kafka Streams topology: dedup, IsTDNYB and GLA checks, PCSS preference lookup behind a circuit breaker, and a punctuator-driven retry store that keeps per-customer ordering.