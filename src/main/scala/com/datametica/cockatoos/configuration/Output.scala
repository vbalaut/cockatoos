package com.datametica.cockatoos.configuration

/**
  * Created by vbalaut on 10/03/18.
  */
import com.fasterxml.jackson.annotation.JsonProperty
import com.datametica.cockatoos.configuration.outputs._

case class Output(@JsonProperty("cassandra") cassandra: Option[Cassandra],
                  @JsonProperty("redshift") redshift: Option[Redshift],
                  @JsonProperty("redis") redis: Option[Redis],
                  @JsonProperty("segment") segment: Option[Segment],
                  @JsonProperty("jdbc") jdbc: Option[JDBC],
                  @JsonProperty("jdbcquery") jdbcquery: Option[JDBC],
                  @JsonProperty("file") file: Option[File]
                 ) {}

object Output {
  def apply(): Output = new Output(None ,None, None, None, None, None, None)
}