# jsonSchema4s

jsonSchema4s is a simple library for generating a simple json schema from a case class with http://json-schema.org/ format.

## Usage

The base method is `JsonSchemaGenerator.generateSchema` and it returns a `JsonSchemaElement` ready to be serialized to json to generate the respecting json schema.

The json serialization it's not provided due to not adding extra dependencies, use the `JsonSchemaElement` on a more complex class to be serializated and also if you already have a json serializer you can use it. (Note: the serialization was tested with https://github.com/json4s/json4s)

#### Example

```scala

import com.despegar.library.serialization.json._

val schema: JsonSchemaElement = JsonSchemaGenerator.generateSchema[ACaseClass]

// You need to use your own scala json serializer
val schemaAsJson = someSerializer.serialize(schema)
...
```

## Supported Types

The library supports case class, Option[A], Seq[A], String, Boolean, Int, Long, BigDecimal.

These were the only ones implemented just by the need

## WARNING

It's experimental due to scala reflection universal api. The recommended use is for documentation purposes.
