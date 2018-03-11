package com.datametica.cockatoos.configuration.outputs

import com.fasterxml.jackson.annotation.JsonProperty

case class Redis(@JsonProperty("host") host: String,
                 @JsonProperty("port") port: Option[String],
                 @JsonProperty("auth") auth: Option[String],
                 @JsonProperty("db") db: Option[String]) {
  require(Option(host).isDefined, "Redis database connection: host is mandatory.")
}
